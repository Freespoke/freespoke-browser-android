/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.components.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mozilla.fenix.perf.lazyMonitored

class Billing(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) {

    private val billingClient by lazyMonitored {
        BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                coroutineScope.launch {
                    processPurchases(billingResult, purchases)
                }
            }
            .enablePendingPurchases()
            .build()
    }

    private val googlePlayConnectionStateFlow: MutableStateFlow<Int> =
        MutableStateFlow(BillingResponseCode.SERVICE_DISCONNECTED)

    private val _successfulPurchaseTrigger: MutableSharedFlow<Unit> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val successfulPurchaseTrigger = _successfulPurchaseTrigger.asSharedFlow()

    private val billingClientStateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (billingResult.responseCode == BillingResponseCode.OK) {
                googlePlayConnectionStateFlow.value = billingResult.responseCode
            }
        }

        override fun onBillingServiceDisconnected() {
            googlePlayConnectionStateFlow.value = BillingResponseCode.SERVICE_DISCONNECTED
            //todo add retry logic
            //billingClient.startConnection(this)
        }
    }

    private fun connectToGooglePlay() {
        billingClient.startConnection(billingClientStateListener)
    }

    private suspend fun <T> BillingClient.whileReady(
        action: suspend () -> T?
    ): T? {
        if (!isReady) {
            connectToGooglePlay()
        }

        return googlePlayConnectionStateFlow.mapNotNull {  billingState ->
            if (billingState == BillingResponseCode.OK) {
                action()
            } else {
                null
            }
        }.firstOrNull()
    }

    suspend fun querySubscriptionOffers(): ProductDetails? {

        return billingClient.whileReady {
            val productDetailsParams = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(PREMIUM_SUBSCRIPTION_ID)
                            .setProductType(ProductType.SUBS)
                            .build(),
                    ),
                )
                .build()

            val productDetailsResult = withContext(Dispatchers.IO) {
                billingClient.queryProductDetails(productDetailsParams)
            }

            if (productDetailsResult.billingResult.responseCode == BillingResponseCode.OK) {
                productDetailsResult.productDetailsList?.firstOrNull()
            } else {
                null
            }
        }
    }

    fun launchBillingFlow(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String,
        accountId: String,
    ) {
        val productDetailsParams = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build(),
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParams)
            .setObfuscatedAccountId(accountId)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    private suspend fun acknowledgePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            withContext(Dispatchers.IO) {
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        _successfulPurchaseTrigger.tryEmit(Unit)
                    }
                    //todo add retry logic
                }
            }
        }
    }

    private suspend fun processPurchases(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingResponseCode.OK) {
            purchases?.forEach {
                if (it.purchaseState == PurchaseState.PURCHASED) {
                    acknowledgePurchase(it)
                }
            }
        }
    }

    suspend fun fetchActiveSubscriptions() {
        billingClient.whileReady {
            val queryPurchasesParams = QueryPurchasesParams.newBuilder()
                .setProductType(ProductType.SUBS)
                .build()

            val subscriptionPurchasesResult = billingClient.queryPurchasesAsync(queryPurchasesParams)
            with(subscriptionPurchasesResult) {
                processPurchases(billingResult, purchasesList)
            }
        }
    }

    companion object {

        fun ProductDetails.filterTrialOffers(): List<ProductDetails.SubscriptionOfferDetails> {
            val offers = subscriptionOfferDetails ?: return emptyList()

            return offers.filter {
                it.offerId == TRIAL_OFFER_ID
            }
        }

        fun ProductDetails.filterBaseOffers(): List<ProductDetails.SubscriptionOfferDetails> {
            val offers = subscriptionOfferDetails ?: return emptyList()

            return offers.filter {
                it.offerId.isNullOrEmpty()
            }
        }


        const val PREMIUM_SUBSCRIPTION_ID = "premium_monthly"
        const val PREMIUM_MONTHLY_PLAN_ID = "premium-monthly"
        const val PREMIUM_YEARLY_PLAN_ID = "premium-annual"
        const val TRIAL_OFFER_ID = "1-month-free-trial"

        const val GOOGLE_PLAY_SUBSCRIPTION_URL =
            "https://play.google.com/store/account/subscriptions"
    }

}
