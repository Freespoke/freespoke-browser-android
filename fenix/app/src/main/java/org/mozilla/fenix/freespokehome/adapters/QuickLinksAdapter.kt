package org.mozilla.fenix.freespokehome.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.mozilla.fenix.apiservice.model.QuickLink
import org.mozilla.fenix.databinding.QuickLinksItemBinding

class QuickLinksAdapter(val data: List<QuickLink>, val onItemClick: (QuickLink) -> Unit) : RecyclerView.Adapter<QuickLinksAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: QuickLinksItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = QuickLinksItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
        val link = data[position]
            title.text = link.title
            icon.load(link.categoryIcon)
            root.setOnClickListener {
                onItemClick.invoke(link)
            }
        }
    }
}
