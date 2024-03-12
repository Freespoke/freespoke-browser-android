package org.mozilla.fenix.freespokehome

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import mozilla.components.browser.storage.sync.PlacesHistoryStorage
import mozilla.components.concept.storage.HistoryMetadata
import org.mozilla.fenix.FenixApplication
import org.mozilla.fenix.apiservice.FreespokeApi
import org.mozilla.fenix.apiservice.model.QuickLinkObject
import org.mozilla.fenix.apiservice.model.ShopCollection
import org.mozilla.fenix.apiservice.model.TrendingNews
import org.mozilla.fenix.components.bookmarks.BookmarksUseCase
import org.mozilla.fenix.components.bookmarks.BookmarksUseCase.Companion.DEFAULT_BOOKMARKS_DAYS_AGE_TO_RETRIEVE
import org.mozilla.fenix.freespokeaccount.profile.ProfileUiModel
import org.mozilla.fenix.freespokeaccount.profile.ProfileUiModel.Companion.mapToUiProfile
import org.mozilla.fenix.freespokeaccount.store.FreespokeProfileStore
import org.mozilla.fenix.freespokeaccount.store.UpdateProfileAction
import org.mozilla.fenix.home.recentbookmarks.RecentBookmark
import java.util.concurrent.TimeUnit

class FreespokeHomeViewModel(
    val bookmarksUseCase: BookmarksUseCase,
    val historyStorage: PlacesHistoryStorage,
    val freespokeProfileStore: FreespokeProfileStore
): ViewModel() {

    val newsData = MutableLiveData<List<TrendingNews>>()
    val shopsData = MutableLiveData<ShopCollection>()
    val quickLinksData = MutableLiveData<QuickLinkObject>()
    val bookmarkData = MutableLiveData<List<RecentBookmark>>()
    val profileData = MutableLiveData<ProfileUiModel?>()

    val defaultBookmarks = listOf("https://freespoke.com/",
        "https://freespoke-support.freshdesk.com/support/tickets/new",
        "https://freespoke.substack.com/",
        "https://freespoke.com/join/step-1")

    val historyData: LiveData<List<HistoryMetadata>> = liveData {
        val data = historyStorage.getHistoryMetadataSince(Long.MIN_VALUE).take(10)
        emit(data)
    }

    fun getNews() {
        viewModelScope.launch {
            try {
                val data = FreespokeApi.service.getTrendingNews(1, 2)
                newsData.value = data
            } catch (e: Exception) {
                Log.e("API", e.localizedMessage ?: "")
            }
        }
    }

    fun getShopCollections() {
        viewModelScope.launch {
            try {
                val data = FreespokeApi.service.getShops(1, 4)
                shopsData.value = data
            } catch (e: Exception) {
                Log.e("API", e.localizedMessage ?: "")
            }
        }
    }

    fun getQuickLinks() {
        viewModelScope.launch {
            try {
                val data = FreespokeApi.service.getFreespokeQuickLinks(3, "android")
                quickLinksData.value = data
            } catch (e: Exception) {
                Log.e("API", e.localizedMessage ?: "")
            }
        }
    }

    fun getBookmarks(sharedPref: SharedPreferences) {
        viewModelScope.launch {
            if (!sharedPref.getBoolean(IS_DEFAULT_BOOKMARKS_ADDED, false)) {
                defaultBookmarks.map {
                    bookmarksUseCase.addBookmark.invoke(it, it)
                }
                with(sharedPref.edit()) {
                    putBoolean(IS_DEFAULT_BOOKMARKS_ADDED, true)
                    apply()
                }
            }
            val maxAgeInMs: Long = TimeUnit.DAYS.toMillis(DEFAULT_BOOKMARKS_DAYS_AGE_TO_RETRIEVE)
            val data = bookmarksUseCase.retrieveRecentBookmarks.invoke(10, maxAgeInMs).take(4)
            bookmarkData.value = data
        }
    }

    fun getProfileData() {
        //todo if(!userLoggedIn) profileData.value = null

//        viewModelScope.launch {
//            try {
//                val profile = FreespokeApi.getUserProfileData()
//                profileData.value = profile.mapToUiProfile()
//                freespokeProfileStore.dispatch(
//                    UpdateProfileAction(profile)
//                )
//            } catch (e: Exception) {
//                Log.e("API", e.localizedMessage ?: "")
//            }
//        }
    }

    companion object {
        const val IS_DEFAULT_BOOKMARKS_ADDED = "is_default_bookmarks_added"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])
                return FreespokeHomeViewModel(
                    (application as FenixApplication).components.useCases.bookmarksUseCases,
                    application.components.core.historyStorage,
                    application.components.freespokeProfileStore) as T
            }
        }
    }
}

