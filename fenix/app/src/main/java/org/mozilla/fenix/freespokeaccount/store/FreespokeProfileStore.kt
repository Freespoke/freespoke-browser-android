/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.freespokeaccount.store

import mozilla.components.lib.state.Action
import mozilla.components.lib.state.State
import mozilla.components.lib.state.Store
import org.mozilla.fenix.apiservice.model.UserProfileData

class FreespokeProfileStore : Store<UserProfileDataState, ProfileStoreAction>(
    initialState = UserProfileDataState(),
    reducer = { _, action ->
        when (action) {
            is ClearStore -> UserProfileDataState()
            is UpdateProfileAction -> {
                UserProfileDataState(action.profile)
            }
        }
    },
)

data class UserProfileDataState(val profile: UserProfileData? = null) : State

sealed class ProfileStoreAction : Action
data class UpdateProfileAction(val profile: UserProfileData) : ProfileStoreAction()
object ClearStore : ProfileStoreAction()
