/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.components.billing

import com.squareup.moshi.Json

data class TransactionReceipt(
    val productId: String,
    val transactionId: String,
    val originalTransactionId: String = transactionId,
    val purchaseDate: Long,
    val originalPurchaseDate: Long = purchaseDate,
    val expiresDate: Long,
    //cancellationDate: Long,
    val webOrderLineItemId: String,
    val isTrialPeriod: Boolean,
    val isInIntroOfferPeriod: Boolean,
)
