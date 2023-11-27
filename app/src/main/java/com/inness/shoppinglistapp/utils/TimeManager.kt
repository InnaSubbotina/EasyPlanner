package com.inness.shoppinglistapp.utils

import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import java.util.*

object TimeManager {
    private const val DEF_TIME_FORMAT = "hh:mm:ss - yyyy/MM/dd"

     fun getCurrentTime() : String {
        val formatter = SimpleDateFormat("hh:mm:ss - yyyy/MM/dd", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

    fun getTimeFormat(time: String, defPreference: SharedPreferences): String {
        val defFormatter = SimpleDateFormat(DEF_TIME_FORMAT, Locale.getDefault())
        val defDate = defFormatter.parse(time)
        val newFormat = defPreference.getString("time_format_key", DEF_TIME_FORMAT)
        val newFormatter = SimpleDateFormat(newFormat, Locale.getDefault())
        return if(defDate!= null){
            newFormatter.format(defDate)
        } else {
            time
        }
    }
}