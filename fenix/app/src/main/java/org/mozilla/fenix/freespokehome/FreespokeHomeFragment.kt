/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.freespokehome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.mozilla.fenix.BrowserDirection
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.NavGraphDirections
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.FragmentFreespokeHomeBinding
import org.mozilla.fenix.ext.openSetDefaultBrowserOption
import org.mozilla.fenix.settings.SupportUtils
import org.mozilla.fenix.utils.BrowsersCache

class FreespokeHomeFragment : Fragment() {

    @VisibleForTesting
    @Suppress("VariableNaming")
    internal var _binding: FragmentFreespokeHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFreespokeHomeBinding.inflate(inflater, container, false)

        binding.searchView.setOnClickListener {
            val directions = NavGraphDirections.actionGlobalHome(true)
            findNavController().navigate(directions)
        }

        binding.freespokeNewsWebsite.setOnClickListener {
            (activity as HomeActivity).binding.bottomNavigation.selectedItemId = R.id.action_news
            (activity as HomeActivity).openToBrowserAndLoad(
                searchTermOrURL = SupportUtils.getFreespokeURLForTopic(SupportUtils.SumoTopic.NEWS),
                newTab = false,
                from = BrowserDirection.FromGlobal,
            )
        }

        binding.freespokeShopWebsite.setOnClickListener {
            (activity as HomeActivity).binding.bottomNavigation.selectedItemId = R.id.action_shop
            (activity as HomeActivity).openToBrowserAndLoad(
                searchTermOrURL = SupportUtils.getFreespokeURLForTopic(SupportUtils.SumoTopic.PRODUCTS),
                newTab = false,
                from = BrowserDirection.FromGlobal,
            )
        }

        val browsers = BrowsersCache.all(requireContext())
        val isDefaultBrowser = browsers.isDefaultBrowser
        if (isDefaultBrowser) {
            binding.setDefaultBrowserButton.visibility = View.GONE
        } else {
            binding.setDefaultBrowserButton.visibility = View.VISIBLE
            binding.setDefaultBrowserButton.setOnClickListener {
                (activity as HomeActivity).openSetDefaultBrowserOption()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
