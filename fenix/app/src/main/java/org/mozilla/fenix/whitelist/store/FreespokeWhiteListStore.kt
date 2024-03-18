package org.mozilla.fenix.whitelist.store

import mozilla.components.lib.state.Action
import mozilla.components.lib.state.State
import mozilla.components.lib.state.Store

class FreespokeWhiteListStore : Store<UserWhiteListDataState, WhiteListStoreAction>(
    initialState = UserWhiteListDataState(),
    reducer = { _, action ->
        when (action) {
            is ClearStore -> UserWhiteListDataState()
            is UpdateWhiteListAction -> {
                UserWhiteListDataState(action.whiteList)
            }
        }
    },
)

//todo add DB
data class UserWhiteListDataState(val whiteList: List<String>? = null) : State

sealed class WhiteListStoreAction : Action

//todo add DB
data class UpdateWhiteListAction(val whiteList: List<String>) : WhiteListStoreAction()

data class WhiteListData(
    val urlDomain: String
)

object ClearStore : WhiteListStoreAction()
