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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mozilla.fenix.components.billing.Billing
import org.mozilla.fenix.components.billing.Billing.Companion.filterBaseOffers
import org.mozilla.fenix.components.billing.Billing.Companion.filterTrialOffers
import org.mozilla.fenix.ext.components

class FreespokePremiumViewModel(
    private val billing: Billing,
) : ViewModel() {

    private val cachedProductDetails: MutableStateFlow<ProductDetails?> = MutableStateFlow(null)

    private val _subscriptionUiStateFlow: MutableStateFlow<SubscriptionsUiModel?> =
        MutableStateFlow(null)
    val subscriptionUiStateFlow = _subscriptionUiStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            val subscription = billing.querySubscriptionOffers() ?: return@launch
            val subscriptionOffers = subscription.filterTrialOffers().takeIf {
                it.isNotEmpty()
            } ?: subscription.filterBaseOffers()
            //val subscriptionOffers = subscription.filterBaseOffers()

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
    }

    fun launchPurchaseFlow(activity: Activity, offerToken: String, onSuccess: () -> Unit) {
        cachedProductDetails.value?.let { productDetails ->
            billing.launchBillingFlow(
                activity,
                productDetails,
                offerToken,
            )
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
                return FreespokePremiumViewModel(application.components.billing) as T
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
