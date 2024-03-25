/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.apiservice.model

import com.squareup.moshi.Json

data class UserProfileData(
    val id: String,
    val attributes: ProfileAttributes,
    val firstName: String,
    val lastName: String,
    val manageSubscriptionLink: String?
)

data class ProfileAttributes(
    @Json(name = "external_account_id")
    val externalAccountId: String,
    val registrationPlatform: String,
    val subscription: Subscription?
)

data class Subscription(
    val subscriptionName: String,
    val subscriptionPaymentSource: String,
    val subscriptionExpiry: Long
)
