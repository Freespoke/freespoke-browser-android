package org.mozilla.fenix.freespokeaccount.profile

import org.mozilla.fenix.apiservice.UserProfileData

data class ProfileUiModel(
    val shortName: String,
    val hasPremium: Boolean,
    val hasNativeSubscription: Boolean,
    val manageSubscriptionLink: String?
) {

    companion object {
        fun UserProfileData.mapToUiProfile(): ProfileUiModel {

            return ProfileUiModel(
                shortName = "${firstName.first()}${lastName.first()}",
                hasPremium = System.currentTimeMillis() / 1000 < (attributes.subscription?.subscriptionExpiry ?: 0L),
                //todo
                hasNativeSubscription = attributes.subscription?.subscriptionPaymentSource == "android",
                manageSubscriptionLink = null
            )
        }
    }
}
