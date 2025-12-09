package com.example.recyclerviewproject.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewproject.databinding.ItemHeaderBinding
import com.example.recyclerviewproject.databinding.ItemType1Binding
import com.example.recyclerviewproject.databinding.ItemType2Binding
import com.example.recyclerviewproject.model.ListItem

class MultiTypeAdapter(
    private val items: MutableList<ListItem>,
    private val onItemClick: (ListItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM_1 = 1
        private const val TYPE_ITEM_2 = 2
    }

    fun getItems(): List<ListItem> = items

    fun addItem(item: ListItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun updateItems(newItems: List<ListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.Header -> TYPE_HEADER
            is ListItem.ItemType1 -> TYPE_ITEM_1
            is ListItem.ItemType2 -> TYPE_ITEM_2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }
            TYPE_ITEM_1 -> {
                val binding = ItemType1Binding.inflate(inflater, parent, false)
                ItemType1ViewHolder(binding)
            }
            TYPE_ITEM_2 -> {
                val binding = ItemType2Binding.inflate(inflater, parent, false)
                ItemType2ViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is ListItem.ItemType1 -> (holder as ItemType1ViewHolder).bind(item)
            is ListItem.ItemType2 -> (holder as ItemType2ViewHolder).bind(item)
        }

        holder.itemView.setOnClickListener {
            onItemClick(items[position])
        }
    }

    override fun getItemCount() = items.size

    inner class HeaderViewHolder(
        private val binding: ItemHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(header: ListItem.Header) {
            binding.tvHeaderTitle.text = header.title
        }
    }

    inner class ItemType1ViewHolder(
        private val binding: ItemType1Binding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListItem.ItemType1) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
            binding.ivIcon.setImageResource(item.iconRes)
        }
    }

    inner class ItemType2ViewHolder(
        private val binding: ItemType2Binding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListItem.ItemType2) {
            binding.tvProductName.text = item.name
            binding.tvPrice.text = item.price
            binding.ratingBar.rating = item.rating
            binding.tvRating.text = item.rating.toString()
            binding.ivProductImage.setImageResource(item.imageRes)
        }
    }
}