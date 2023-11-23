package com.inness.shoppinglistapp.activities

import android.app.Application
import com.inness.shoppinglistapp.database.MainDataBase

class MainApp: Application() {
    val database by lazy { MainDataBase.getDataBase(this) }
}