package org.mozilla.fenix.freespokehome.adapters

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import org.mozilla.fenix.compose.ComposeViewHolder
import org.mozilla.fenix.home.recentbookmarks.RecentBookmark
import org.mozilla.fenix.home.recentbookmarks.view.RecentBookmarksHomeScreen

class FreespokeBookmarksAdapter(val data: List<RecentBookmark>,
                                val lifecycleOwner: LifecycleOwner,
                                val onItemClick: (RecentBookmark) -> Unit):
    RecyclerView.Adapter<FreespokeBookmarksAdapter.ViewHolder>() {

    inner class ViewHolder( val view: ComposeView, val lifecycleOwner: LifecycleOwner): ComposeViewHolder(view, lifecycleOwner) {

        @Composable
        override fun Content() {
            RecentBookmarksHomeScreen(
                bookmarks = data,
                onRecentBookmarkClick = onItemClick
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            view = ComposeView(parent.context),
            lifecycleOwner = lifecycleOwner
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Nothing to-do
    }

    override fun getItemCount(): Int {
        return 1
    }
}
