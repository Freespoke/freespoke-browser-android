/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.freespokehome

import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import mozilla.appservices.places.BookmarkRoot
import org.mozilla.fenix.BrowserDirection
import org.mozilla.fenix.FenixApplication
import org.mozilla.fenix.GleanMetrics.RecentBookmarks
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.NavGraphDirections
import org.mozilla.fenix.analytics.MatomoAnalytics
import org.mozilla.fenix.components.components
import org.mozilla.fenix.databinding.FragmentFreespokeHomeBinding
import org.mozilla.fenix.ext.hideToolbar
import org.mozilla.fenix.ext.requireComponents
import org.mozilla.fenix.freespokehome.adapters.FreespokeBookmarksAdapter
import org.mozilla.fenix.freespokehome.adapters.FreespokeHistoryAdapter
import org.mozilla.fenix.freespokehome.adapters.FreespokeShopListAdapter
import org.mozilla.fenix.freespokehome.adapters.QuickLinksAdapter
import org.mozilla.fenix.freespokehome.adapters.TrendingNewsAdapter
import org.mozilla.fenix.settings.SupportUtils

class FreespokeHomeFragment : Fragment() {

    @VisibleForTesting
    @Suppress("VariableNaming")
    internal var _binding: FragmentFreespokeHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FreespokeHomeViewModel by viewModels {
        FreespokeHomeViewModel.Factory
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFreespokeHomeBinding.inflate(inflater, container, false)

        with(binding) {
            searchView.setOnClickListener {
                (requireActivity().application as FenixApplication).trackEvent(
                    MatomoAnalytics.HOME,
                    MatomoAnalytics.HOME_SEARCH,
                    MatomoAnalytics.SEARCH)
                val directions = NavGraphDirections.actionGlobalHome(true)
                findNavController().navigate(directions)
            }

            viewModel.bookmarkData.observe(viewLifecycleOwner) { data ->
                val adapter = FreespokeBookmarksAdapter(data, viewLifecycleOwner) {
                    (requireActivity().application as FenixApplication).trackEvent(
                        MatomoAnalytics.HOME,
                        MatomoAnalytics.HOME_BOOKMARKS,
                        MatomoAnalytics.CLICK)
                    it.url?.let { url -> openItem(url) }
                }
                bookmarkList.adapter = adapter
                bookmarksContainer.visibility = if (data.isEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }

            viewModel.newsData.observe(viewLifecycleOwner) { news ->
                val adapter = TrendingNewsAdapter(news, requireContext()) {
                    (requireActivity().application as FenixApplication).trackEvent(
                        MatomoAnalytics.HOME,
                        MatomoAnalytics.HOME_TRENDING_NEWS,
                        it.name)
                    openItem(it.url)
                }
                trendingNewsList.layoutManager = LinearLayoutManager(requireContext())
                trendingNewsList.adapter = adapter
                newsContainer.visibility = if (news.isEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }

            viewModel.shopsData.observe(viewLifecycleOwner) { shops ->
                val adapter = FreespokeShopListAdapter(shops.collections) {
                    (requireActivity().application as FenixApplication).trackEvent(
                        MatomoAnalytics.HOME,
                        MatomoAnalytics.HOME_SHOP,
                        it)
                    openItem(it)
                }
                shopList.layoutManager = GridLayoutManager(requireContext(), 2)
                shopList.adapter = adapter
                shopCollectionContainer.visibility = if (shops.collections.isEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }

            viewModel.quickLinksData.observe(viewLifecycleOwner) { links ->
                quickLinksLabel.text = links.label
                val adapter = QuickLinksAdapter(links.data) {
                    (requireActivity().application as FenixApplication).trackEvent(
                        MatomoAnalytics.HOME,
                        MatomoAnalytics.HOME_FREESPOKEWAY_CLICK + it.title,
                        MatomoAnalytics.CLICK)
                    when (it.category) {
                        "Search" -> {
                            val directions = NavGraphDirections.actionGlobalHome(true)
                            findNavController().navigate(directions)
                        }
                        "News" -> {
                            (activity as HomeActivity).showNews()
                        }
                        else -> {
                            openItem(it.url)
                        }

                    }
                }
                quickLinksList.adapter = adapter
            }

            StrictMode.allowThreadDiskReads()
            viewModel.getNews()
            viewModel.getShopCollections()
            viewModel.getQuickLinks()
            viewModel.getBookmarks(requireActivity().getPreferences(Context.MODE_PRIVATE))
            viewModel.getProfileData()

            viewModel.historyData.observe(viewLifecycleOwner) { data ->
                val adapter = FreespokeHistoryAdapter(data) {
                    (requireActivity().application as FenixApplication).trackEvent(
                        MatomoAnalytics.HOME,
                        MatomoAnalytics.HOME_RECENTLY_ITEMS,
                        MatomoAnalytics.CLICK)
                    openItem(it)
                }
                recentlyViewedList.layoutManager = GridLayoutManager(requireContext(), 5)
                recentlyViewedList.adapter = adapter
                recentViewedContainer.visibility = if (data.isEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
            viewMoreBookmarksBtn.setOnClickListener {
                RecentBookmarks.showAllBookmarks.add()
                findNavController().navigate(
                    FreespokeHomeFragmentDirections.actionGlobalBookmarkFragment(BookmarkRoot.Mobile.id),
                )
            }

            viewMoreRecentlyViewsBtn.setOnClickListener {
                findNavController().navigate(
                    FreespokeHomeFragmentDirections.actionGlobalHistoryFragment(),
                )
            }

            viewMoreShopsBtn.setOnClickListener {
                (requireActivity().application as FenixApplication).trackEvent(
                    MatomoAnalytics.HOME,
                    MatomoAnalytics.HOME_SHOP_VIEW_MORE,
                    MatomoAnalytics.CLICK)
                openItem(SupportUtils.getFreespokeURLForTopic(SupportUtils.SumoTopic.PRODUCTS))
            }

            viewMoreNewsBtn.setOnClickListener {
                (requireActivity().application as FenixApplication).trackEvent(
                    MatomoAnalytics.HOME,
                    MatomoAnalytics.HOME_TRENDING_NEWS_MORE_CLICKED,
                    MatomoAnalytics.CLICK)
                openItem(SupportUtils.getFreespokeURLForTopic(SupportUtils.SumoTopic.NEWS))
            }

            viewModel.profileData.observe(viewLifecycleOwner) { profileModel ->
                profileModel?.let {
                    freespokeProfile.updateProfile(it)
                }
            }

            freespokeProfile.setOnProfileClick {
                findNavController().navigate(
                    FreespokeHomeFragmentDirections.actionFreespokeHomeFragmentToFreespokeProfileFragment()
                )
            }

        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        hideToolbar()

        requireComponents.useCases.sessionUseCases.updateLastAccess()
    }

    private fun openItem(url: String) {
        (activity as HomeActivity).openToBrowserAndLoad(
            searchTermOrURL = url,
            newTab = true,
            from = BrowserDirection.FromGlobal,
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
