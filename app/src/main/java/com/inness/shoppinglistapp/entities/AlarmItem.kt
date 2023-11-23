package com.inness.shoppinglistapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "alarm_item")
data class AlarmItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "time")
    val time: String,

    @ColumnInfo(name = "timeInMillis")
    val timeInMillis: Long

) : Serializable
