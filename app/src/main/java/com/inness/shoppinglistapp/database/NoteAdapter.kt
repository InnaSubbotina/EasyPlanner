package com.inness.shoppinglistapp.database

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inness.shoppinglistapp.R
import com.inness.shoppinglistapp.databinding.NoteListItemBinding
import com.inness.shoppinglistapp.entities.NoteItem
import com.inness.shoppinglistapp.utils.HtmlManager
import com.inness.shoppinglistapp.utils.TimeManager

class NoteAdapter (private val listener: Listener, private val defPref: SharedPreferences) :
    ListAdapter <NoteItem, NoteAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
       holder.setData(getItem(position),listener,defPref)
    }

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = NoteListItemBinding.bind(view)

        fun setData(note: NoteItem,listener: Listener, defPref: SharedPreferences) = with(binding) {
            tvTitle.text = note.title
            tvDescription.text = HtmlManager.getFromHtml(note.content).trim()
            tvTime.text = TimeManager.getTimeFormat(note.time,defPref)
            itemView.setOnClickListener{
                listener.onClickItem(note)
            }
            itemView.setOnLongClickListener {
                note.id?.let { it1 -> listener.deleteItem(it1) }
                true
            }
        }

        companion object {
            fun create(parent: ViewGroup) : ItemHolder {
                return ItemHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.note_list_item,parent,false))
            }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<NoteItem>() {
        override fun areItemsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {
          return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {
            return oldItem == newItem
        }
    }

    interface Listener {
        fun deleteItem(id:Int)
        fun onClickItem(note:NoteItem)
    }
}