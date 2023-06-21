/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.settings.about

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.pm.PackageInfoCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import mozilla.components.support.utils.ext.getPackageInfoCompat
import org.mozilla.fenix.BrowserDirection
import org.mozilla.fenix.BuildConfig
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.R
import org.mozilla.fenix.crashes.CrashListActivity
import org.mozilla.fenix.databinding.FragmentAboutBinding
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.ext.showToolbar
import org.mozilla.fenix.settings.SupportUtils
import org.mozilla.fenix.settings.SupportUtils.getFreespokeGithubRepo
import org.mozilla.fenix.settings.about.AboutItemType.ABOUT
import org.mozilla.fenix.settings.about.AboutItemType.FREESPOKE_REPOSITORY
import org.mozilla.fenix.settings.about.AboutItemType.LICENSING_INFO
import org.mozilla.fenix.settings.about.AboutItemType.PRIVACY_POLICY
import org.mozilla.fenix.settings.about.AboutItemType.SUPPORT
import org.mozilla.fenix.settings.about.AboutItemType.TERMS
import org.mozilla.fenix.utils.Do
import org.mozilla.geckoview.BuildConfig as GeckoViewBuildConfig

/**
 * Displays the logo and information about the app, including library versions.
 */
class AboutFragment : Fragment(), AboutPageListener {

    private lateinit var appName: String
    private var aboutPageAdapter: AboutPageAdapter? = AboutPageAdapter(this)
    private var _binding: FragmentAboutBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        appName = getString(R.string.app_name)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (aboutPageAdapter == null) {
            aboutPageAdapter = AboutPageAdapter(this)
        }

        binding.aboutList.run {
            adapter = aboutPageAdapter
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL,
                ),
            )
        }

        lifecycle.addObserver(
            SecretDebugMenuTrigger(
                logoView = binding.wordmark,
                settings = view.context.settings(),
            ),
        )

        populateAboutHeader()
        aboutPageAdapter?.submitList(populateAboutList())
    }

    override fun onResume() {
        super.onResume()
        showToolbar(getString(R.string.preferences_about, appName))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        aboutPageAdapter = null
        _binding = null
    }

    private fun populateAboutHeader() {
        val aboutText = try {
            val packageInfo =
                requireContext().packageManager.getPackageInfoCompat(requireContext().packageName, 0)
            val versionCode = PackageInfoCompat.getLongVersionCode(packageInfo).toString()
            val maybeFenixGitHash = if (BuildConfig.GIT_HASH.isNotBlank()) ", ${BuildConfig.GIT_HASH}" else ""
            val maybeGecko = getString(R.string.gecko_view_abbreviation)
            val geckoVersion =
                GeckoViewBuildConfig.MOZ_APP_VERSION + "-" + GeckoViewBuildConfig.MOZ_APP_BUILDID
            val appServicesAbbreviation = getString(R.string.app_services_abbreviation)
            val appServicesVersion = mozilla.components.Build.applicationServicesVersion

            String.format(
                "%s (Build #%s)%s\n%s: %s\n%s: %s",
                packageInfo.versionName,
                versionCode,
                maybeFenixGitHash,
                maybeGecko,
                geckoVersion,
                appServicesAbbreviation,
                appServicesVersion,
            )
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }

        val content = getString(R.string.about_content, appName)
        val buildDate = BuildConfig.BUILD_DATE

        binding.aboutText.text = aboutText
        binding.aboutContent.text = content
        binding.buildDate.text = buildDate
    }

    private fun populateAboutList(): List<AboutPageItem> {
        return listOf(
            AboutPageItem(
                AboutItem.ExternalLink(
                    SUPPORT,
                    SupportUtils.getFreespokeSupportURLPage(SupportUtils.FreespokeSupportPage.CONTACT),
                ),
                getString(R.string.about_support),
            ),
            AboutPageItem(
                AboutItem.ExternalLink(
                    ABOUT,
                    SupportUtils.getFreespokeURLForTopic(SupportUtils.SumoTopic.ABOUT),
                ),
                getString(R.string.about),
            ),
            AboutPageItem(
                AboutItem.ExternalLink(
                    TERMS,
                    SupportUtils.getFreespokeURLForTopic(SupportUtils.SumoTopic.TERMS),
                ),
                getString(R.string.about_terms),
            ),
            AboutPageItem(
                AboutItem.ExternalLink(
                    PRIVACY_POLICY,
                    SupportUtils.getFreespokeURLForTopic(SupportUtils.SumoTopic.PRIVACY_POLICY),
                ),
                getString(R.string.about_privacy_policy),
            ),
            AboutPageItem(
                AboutItem.Libraries,
                getString(R.string.about_other_open_source_libraries),
            ),
            AboutPageItem(
                AboutItem.ExternalLink(FREESPOKE_REPOSITORY, getFreespokeGithubRepo()),
                getString(R.string.freespoke_for_android),
            ),
            AboutPageItem(
                AboutItem.Crashes,
                getString(R.string.about_crashes),
            ),
        )
    }

    private fun openLinkInNormalTab(url: String) {
        (activity as HomeActivity).openToBrowserAndLoad(
            searchTermOrURL = url,
            newTab = true,
            from = BrowserDirection.FromAbout,
        )
    }

    private fun openLibrariesPage() {
        val navController = findNavController()
        navController.navigate(R.id.action_aboutFragment_to_aboutLibrariesFragment)
    }

    override fun onAboutItemClicked(item: AboutItem) {
        Do exhaustive when (item) {
            is AboutItem.ExternalLink -> {
                when (item.type) {
                    ABOUT, SUPPORT, PRIVACY_POLICY, LICENSING_INFO, TERMS, FREESPOKE_REPOSITORY -> {} // no telemetry needed
                }

                openLinkInNormalTab(item.url)
            }
            is AboutItem.Libraries -> {
                openLibrariesPage()
            }
            is AboutItem.Crashes -> {
                startActivity(Intent(requireContext(), CrashListActivity::class.java))
            }
        }
    }

    companion object {
        private const val ABOUT_LICENSE_URL = "about:license"
    }
}
