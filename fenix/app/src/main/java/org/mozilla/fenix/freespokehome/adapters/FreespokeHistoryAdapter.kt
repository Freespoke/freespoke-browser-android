package org.mozilla.fenix.freespokehome.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import mozilla.components.concept.storage.HistoryMetadata
import org.mozilla.fenix.R

class FreespokeHistoryAdapter(val data: List<HistoryMetadata>,
                              val onItemClick: (String) -> Unit):
    RecyclerView.Adapter<FreespokeHistoryAdapter.ViewHolder>() {

    inner class ViewHolder( val view: View): RecyclerView.ViewHolder(view) {
        val icon: AppCompatImageView
        val text: TextView
        init {
            icon = view.findViewById(R.id.icon)
            text = view.findViewById(R.id.label)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_history_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            val item = data[position]
            text.text = item.title
            icon.load(item.previewImageUrl)
            view.setOnClickListener {
                item.key.url.let {
                    onItemClick.invoke(it)
                }
            }
        }
    }
}
