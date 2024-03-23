/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.freespokepremium

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.android.billingclient.api.ProductDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mozilla.components.lib.state.ext.flow
import org.mozilla.fenix.apiservice.model.UserProfileData
import org.mozilla.fenix.components.billing.Billing
import org.mozilla.fenix.components.billing.Billing.Companion.filterBaseOffers
import org.mozilla.fenix.components.billing.Billing.Companion.filterTrialOffers
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.freespokeaccount.store.FreespokeProfileStore
import org.mozilla.fenix.freespokeaccount.store.UpdateProfileAction
import org.mozilla.fenix.onboarding.view.SubscriptionInfoBlockType

class FreespokePremiumViewModel(
    private val billing: Billing,
    private val freespokeProfileStore: FreespokeProfileStore
) : ViewModel() {

    private val cachedProductDetails: MutableStateFlow<List<ProductDetails>?> = MutableStateFlow(null)

    private val _subscriptionUiStateFlow: MutableStateFlow<SubscriptionsUiModel?> =
        MutableStateFlow(null)
    val subscriptionUiStateFlow = _subscriptionUiStateFlow.asStateFlow()

    private val _uiTypeFlow: MutableStateFlow<SubscriptionInfoBlockType?> =
        MutableStateFlow(null)
    val uiTypeFlow = _uiTypeFlow.asStateFlow()

    init {
        viewModelScope.launch {
            val subscription = billing.querySubscriptionOffers() ?: return@launch
            val trialOffers = subscription.filterTrialOffers()
            val subscriptionOffers = trialOffers.takeIf {
                it.isNotEmpty()
            } ?: (trialOffers + subscription.filterBaseOffers())

            val monthlyOffer = subscriptionOffers.find {
                it.basePlanId == Billing.PREMIUM_MONTHLY_PLAN_ID
            } ?: return@launch

            val yearlyOffer = subscriptionOffers.find {
                it.basePlanId == Billing.PREMIUM_YEARLY_PLAN_ID
            } ?: return@launch

            val uiModel = SubscriptionsUiModel(
                monthlyPrice = monthlyOffer.pricingPhases.pricingPhaseList.find {
                    it.priceAmountMicros != 0L
                }?.formattedPrice ?: return@launch,
                monthlyOfferToken = monthlyOffer.offerToken,
                yearlyPrice = yearlyOffer.pricingPhases.pricingPhaseList.find {
                    it.priceAmountMicros != 0L
                }?.formattedPrice ?: return@launch,
                yearlyOfferToken = yearlyOffer.offerToken,
            )

            cachedProductDetails.value = subscription
            _subscriptionUiStateFlow.value = uiModel
        }
        viewModelScope.launch {
            freespokeProfileStore.flow().collect {
                it.profile?.let { profile ->
                    _uiTypeFlow.value = resolveUiType(profile)
                } ?: run {
                    _uiTypeFlow.value = SubscriptionInfoBlockType.Trial
                }
            }
        }
    }

    private fun resolveUiType(profile: UserProfileData): SubscriptionInfoBlockType {
        val subscriptionNotExpired =
            System.currentTimeMillis() / 1000 < (profile.attributes.subscription?.subscriptionExpiry ?: 0L)
        val subscriptionNativeToPlatform =
            profile.attributes.subscription?.subscriptionPaymentSource == "android-native"
        val userHasSubscribed = profile.attributes.subscription != null

        return when {
            subscriptionNotExpired && subscriptionNativeToPlatform -> SubscriptionInfoBlockType.Upgrade
            subscriptionNotExpired && !subscriptionNativeToPlatform -> SubscriptionInfoBlockType.Cancel
            !userHasSubscribed -> SubscriptionInfoBlockType.Trial
            else -> SubscriptionInfoBlockType.Regular
        }
    }

    fun launchPurchaseFlow(activity: Activity, offerToken: String, onSuccess: () -> Unit) {
        cachedProductDetails.value?.let { productDetails ->
            viewModelScope.launch {
                val accountId = freespokeProfileStore.flow().mapNotNull { it.profile?.id }.first()

                val product = productDetails.firstOrNull {
                    it.subscriptionOfferDetails?.firstOrNull { offer ->
                        offer.offerToken == offerToken
                    } != null
                }

                product?.let {
                    withContext(Dispatchers.Main) {
                        billing.launchBillingFlow(
                            activity,
                            it,
                            offerToken,
                            accountId,
                        )
                    }
                }
            }

            viewModelScope.launch {
                billing.successfulPurchaseTrigger.first()
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            }
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return FreespokePremiumViewModel(
                    application.components.billing,
                    application.components.freespokeProfileStore
                ) as T
            }
        }
    }
}

data class SubscriptionsUiModel(
    val monthlyPrice: String,
    val yearlyPrice: String,
    val monthlyOfferToken: String,
    val yearlyOfferToken: String,
)
