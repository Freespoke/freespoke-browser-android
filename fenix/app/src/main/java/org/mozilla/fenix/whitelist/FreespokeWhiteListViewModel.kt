package org.mozilla.fenix.whitelist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.utils.Settings
import org.mozilla.fenix.whitelist.store.FreespokeWhiteListStore
import org.mozilla.fenix.whitelist.store.UpdateWhiteListAction
import org.mozilla.fenix.whitelist.store.WhiteListData

class FreespokeWhiteListViewModel(
    freespokeWhiteListStore: FreespokeWhiteListStore,
    settings: Settings,
) : ViewModel() {

    private val _whiteListData: MutableStateFlow<List<WhiteListData>?> = MutableStateFlow(null)
    val whiteListData = _whiteListData.asStateFlow()
    private val _adsBlockEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val adsBlockEnabled = _adsBlockEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            _adsBlockEnabled.value = settings.adsBlockFeatureEnabled
            try {
                val whiteList = /*listOf("domain.com", "site.biz", "url.ru", "domain.com", "domain.com", "domain.com")*/
                    emptyList<String>()
                _whiteListData.value = whiteList.mapToWhiteList()
                freespokeWhiteListStore.dispatch(
                    UpdateWhiteListAction(whiteList),
                )
            } catch (e: Exception) {
                Log.e("API", e.localizedMessage ?: "")
            }
        }
    }


    fun removeItem(whiteListData: WhiteListData) {
        val newList = this.whiteListData.value?.toMutableList()?.apply {
            removeAt(indexOf(whiteListData))
        }
        _whiteListData.value = newList
    }

    fun updateAdsBlockState(isEnabled: Boolean) {
        viewModelScope.launch {
            _adsBlockEnabled.emit(isEnabled)
        }
    }

    fun addToWhiteList(domainText: String) {
        val newList = this.whiteListData.value?.toMutableList()?.apply {
            add(WhiteListData(domainText))
        }
        _whiteListData.value = newList
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
                return FreespokeWhiteListViewModel(FreespokeWhiteListStore(), application.components.settings) as T
            }
        }
    }
}

private fun List<String>?.mapToWhiteList(): List<WhiteListData> {
    val data = mutableListOf<WhiteListData>()
    this?.forEach {
        data.add(WhiteListData(it))
    }
    return data
}
