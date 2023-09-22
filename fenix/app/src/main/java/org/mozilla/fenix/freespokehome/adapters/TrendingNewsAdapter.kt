package org.mozilla.fenix.freespokehome.adapters

import android.content.Context
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.mozilla.fenix.R
import org.mozilla.fenix.apiservice.model.TrendingNews
import org.mozilla.fenix.databinding.TrendingNewsListItemBinding
import java.util.Date

class TrendingNewsAdapter(val data: List<TrendingNews>,
                          val context: Context,
                          val onItemClicked: (TrendingNews) -> Unit) :
    RecyclerView.Adapter<TrendingNewsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: TrendingNewsListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TrendingNewsListItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val item = data[position]
            newsTitle.text = item.name
            updatedDate.text = getUpdatedTitle(item.updatedAt)
            val attribution = SpannableString(context.getString(R.string.attribution, item.image.attribution))
            attribution.setSpan(UnderlineSpan(), 0, attribution.length, 0)
            photoSource.text = attribution
            image.load(item.image.url)
            sourcesCount.text = context.getString(R.string.source_news_count, item.sources)
            leftCount.text = context.getString(R.string.left_count, item.leftCount)
            middleCount.text = context.getString(R.string.middle_count, item.middleCount)
            rightCount.text = context.getString(R.string.right_count, item.rightCount)
            iconsStack.setUpStackIconsView(item.icons)
            root.setOnClickListener { onItemClicked.invoke(item) }
        }
    }

    private fun getUpdatedTitle(updatedAt: Date?): CharSequence? {
        val dayCount =  updatedAt?.let {
            (System.currentTimeMillis() - it.time).div(24 * 60 * 60 * 1000).toInt()
        }
        return context.getString(R.string.update_at_news, dayCount)
    }
}
