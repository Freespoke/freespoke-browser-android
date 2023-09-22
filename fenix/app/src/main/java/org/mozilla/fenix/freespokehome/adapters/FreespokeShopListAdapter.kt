package org.mozilla.fenix.freespokehome.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.mozilla.fenix.apiservice.model.Shop
import org.mozilla.fenix.databinding.ShopItemBinding

class FreespokeShopListAdapter(val data: List<Shop>,
                               val onItemClick: (String) -> Unit):
    RecyclerView.Adapter<FreespokeShopListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ShopItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ShopItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val item = data[position]
            icon.load(item.thumbnail)
            title.text = item.title
            root.setOnClickListener { onItemClick.invoke(item.url) }

        }
    }
}
