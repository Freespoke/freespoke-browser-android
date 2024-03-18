package org.mozilla.fenix.whitelist

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.findNavController
import org.mozilla.fenix.R
import org.mozilla.fenix.ext.hideToolbar
import org.mozilla.fenix.theme.FirefoxTheme

class FreespokeWhiteListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                FirefoxTheme {
                    FreespokeWhiteListPage(
                        onAdBlockStatusChange = {
                            //todo add change status
                        },
                        onBack = {
                            findNavController().popBackStack()
                        },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideToolbar()
    }
}
