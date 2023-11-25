package com.inness.shoppinglistapp.database


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inness.shoppinglistapp.R

import com.inness.shoppinglistapp.databinding.ReminderListItemBinding
import com.inness.shoppinglistapp.entities.AlarmItem

class AlarmAdapter(private val listener: Listener) : ListAdapter <AlarmItem, AlarmAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
       holder.setData(getItem(position),listener)
       }

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view){
        private val binding = ReminderListItemBinding.bind(view)

        fun setData(alarm: AlarmItem,listener: Listener) = with(binding){
            tvTitle.text = alarm.title
            tvDescription.text = alarm.content
            tvMyTime.text = alarm.time

            itemView.setOnClickListener{
                listener.onClickAlarmItem(alarm)
            }
            itemView.setOnLongClickListener {
                 alarm.id?.let { it1 -> listener.deleteAlarmItem(it1) }
                 true
             }
            /*ibDelete.setOnClickListener {
                alarm.id?.let { it1 -> listener.deleteAlarmItem(it1) }
            }*/
            switchSetAlarm.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    alarm.alarmOnOff = true
                    listener.onClickAlarmSwitchOn(alarm)
                } else {
                    alarm.alarmOnOff = false
                    listener.onClickAlarmSwitchOff(alarm)
                }
            }

        }



        companion object {
            fun create(parent: ViewGroup) : ItemHolder {
                return ItemHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.reminder_list_item,parent,false))
            }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<AlarmItem>(){
        override fun areItemsTheSame(oldItem: AlarmItem, newItem: AlarmItem): Boolean {
          return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AlarmItem, newItem: AlarmItem): Boolean {
            return oldItem == newItem
        }
    }

    interface Listener {
        fun deleteAlarmItem(id:Int)
        fun onClickAlarmItem(alarm:AlarmItem)
        fun onClickAlarmSwitchOn(alarm: AlarmItem)
        fun onClickAlarmSwitchOff(alarm: AlarmItem)
    }
}