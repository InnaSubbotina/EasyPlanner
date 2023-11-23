package com.inness.shoppinglistapp.database

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inness.shoppinglistapp.R
import com.inness.shoppinglistapp.databinding.ListShopnameItemBinding
import com.inness.shoppinglistapp.databinding.ShopLibraryListItemBinding
import com.inness.shoppinglistapp.databinding.ShopListItemBinding
import com.inness.shoppinglistapp.entities.ShopListNameItem
import com.inness.shoppinglistapp.entities.ShopListItem

class ShopListItemAdapter(private val listener: Listener) : ListAdapter<ShopListItem, ShopListItemAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return if (viewType == 0) {
            ItemHolder.createShopItem(parent)
        } else {
            ItemHolder.createLibraryItem(parent)
        }
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        if (getItem(position).itemType == 0) {
            holder.setItemData(getItem(position), listener)
        } else {
            holder.setLibraryData(getItem(position), listener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType
    }

    class ItemHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun setItemData(shopListItem: ShopListItem, listener: Listener) {
            val binding = ShopListItemBinding.bind(view)
            binding.apply {
                tvName.text = shopListItem.name
                tvInfo.text = shopListItem.itemInfo
                tvInfo.visibility = infoVisibility(shopListItem)
                checkBox.isChecked = shopListItem.itemChecked
                setPaintFlagAndColor(binding)
                checkBox.setOnClickListener {
                    listener.onClickItem(
                        shopListItem.copy(itemChecked = checkBox.isChecked), CHECK_BOX)
                }
                ibEdit.setOnClickListener {
                    listener.onClickItem(shopListItem, EDIT)
                }
                itemView.setOnLongClickListener {
                    shopListItem.id?.let { it1 -> listener.deleteListItem(it1) }
                    true
                }

            }
        }

        fun setLibraryData(shopListItem: ShopListItem, listener: Listener) {
            val binding = ShopLibraryListItemBinding.bind(view)
            binding.apply {
                tvName.text = shopListItem.name
                ibEdit.setOnClickListener {
                    listener.onClickItem(shopListItem, EDIT_LIBRARY_ITEM)
                }
                imDeleteLib.setOnClickListener {
                       listener.onClickItem(shopListItem, DELETE_LIBRARY_ITEM)
                }
                itemView.setOnClickListener {
                    listener.onClickItem(shopListItem, ADD)
                }
            }
        }

        private fun infoVisibility(shopListItem: ShopListItem) : Int {
            return if (shopListItem.itemInfo.isEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        private fun setPaintFlagAndColor(binding: ShopListItemBinding) {
            binding.apply {
                if(checkBox.isChecked) {
                    tvName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvName.setTextColor(ContextCompat.getColor(binding.root.context,R.color.gray_checkbox))
                    tvInfo.setTextColor(ContextCompat.getColor(binding.root.context,R.color.gray_checkbox))
                } else {
                    tvName.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvInfo.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvName.setTextColor(ContextCompat.getColor(binding.root.context,R.color.black_checkbox))
                    tvInfo.setTextColor(ContextCompat.getColor(binding.root.context,R.color.black_checkbox))
                }
              }
            }

        companion object {
            fun createShopItem(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                    .inflate(R.layout.shop_list_item, parent, false))
            }
            fun createLibraryItem(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                    .inflate(R.layout.shop_library_list_item, parent, false))
            }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<ShopListItem>() {
        override fun areItemsTheSame(
            oldItem: ShopListItem,
            newItem: ShopListItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ShopListItem,
            newItem: ShopListItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface Listener {
        fun onClickItem(shopListItem: ShopListItem, state: Int)
        fun deleteListItem(id: Int)
    }

    companion object{
        const val EDIT = 0
        const val CHECK_BOX = 1
        const val EDIT_LIBRARY_ITEM = 2
        const val DELETE_LIBRARY_ITEM = 3
        const val ADD = 4

    }
}