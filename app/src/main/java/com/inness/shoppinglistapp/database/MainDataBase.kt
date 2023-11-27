package com.inness.shoppinglistapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.inness.shoppinglistapp.entities.*

@Database(entities = [
    LibraryItem::class,
    NoteItem::class,
    ShopListItem::class,
    ShopListNameItem::class,
    AlarmItem::class],
    version = 1, exportSchema = false)

abstract class MainDataBase : RoomDatabase() {

    abstract fun getDao() : Dao
    companion object{
         @Volatile
         private var INSTANCE: MainDataBase? = null
         fun getDataBase(context: Context): MainDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDataBase::class.java,
                    "shopping_list.db"
                ).build()
                instance
            }
        }
    }
}