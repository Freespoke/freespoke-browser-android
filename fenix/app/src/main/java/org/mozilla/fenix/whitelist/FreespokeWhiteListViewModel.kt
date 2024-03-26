package org.mozilla.fenix.whitelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mozilla.fenix.ext.application
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.utils.Settings

class FreespokeWhiteListViewModel(
    private val preferenceRepository: WhiteListPreferenceRepository,
    settings: Settings,
) : ViewModel() {

    private val _whiteListData: MutableStateFlow<List<String>?> = MutableStateFlow(null)
    val whiteListData = _whiteListData.asStateFlow()
    private val _adsBlockEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val adsBlockEnabled = _adsBlockEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            _adsBlockEnabled.value = settings.adsBlockFeatureEnabled
            _whiteListData.value = preferenceRepository.getWhiteList().filterNot { it.isBlank() }
        }
    }


    fun removeItem(urlDomain: String) {
        val newList = this.whiteListData.value?.toMutableList()?.apply {
            removeAt(indexOf(urlDomain))
        }
        _whiteListData.value = newList
        updateWhiteList(newList)
    }

    fun updateAdsBlockState(isEnabled: Boolean) {
        viewModelScope.launch {
            _adsBlockEnabled.emit(isEnabled)
        }
    }

    fun addToWhiteList(domainText: String): Boolean {
        if (whiteListData.value?.contains(domainText.lowercase()) == true) return false
        val newList = this.whiteListData.value?.toMutableList()?.apply {
            add(domainText.lowercase())
        }
        _whiteListData.value = newList
        updateWhiteList(newList)
        return true
    }

    private fun updateWhiteList(list: MutableList<String>?) {
        viewModelScope.launch {
            preferenceRepository.writeWhiteList(list.orEmpty())
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
                return FreespokeWhiteListViewModel(
                    WhiteListPreferenceRepository(application.application),
                    application.components.settings,
                ) as T
            }
        }
    }
}
