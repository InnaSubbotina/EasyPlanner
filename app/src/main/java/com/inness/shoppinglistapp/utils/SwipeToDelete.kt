package com.inness.shoppinglistapp.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.inness.shoppinglistapp.database.AlarmAdapter
import com.inness.shoppinglistapp.entities.AlarmItem

class SwipeToDelete(private val deleteListener: (Int) -> Unit) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target:
    RecyclerView.ViewHolder) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        val position: Int = viewHolder.layoutPosition
        val t = viewHolder.adapterPosition

        deleteListener.invoke(position)
    }


}