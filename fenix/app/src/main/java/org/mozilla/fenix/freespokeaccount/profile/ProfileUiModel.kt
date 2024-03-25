/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.freespokeaccount.profile

import org.mozilla.fenix.apiservice.model.UserProfileData

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
                hasNativeSubscription = attributes.subscription?.subscriptionPaymentSource == "android-native",
                manageSubscriptionLink = manageSubscriptionLink
            )
        }
    }
}
