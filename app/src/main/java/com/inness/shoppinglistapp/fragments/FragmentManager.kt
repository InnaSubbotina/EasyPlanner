package com.inness.shoppinglistapp.fragments

import androidx.appcompat.app.AppCompatActivity
import com.inness.shoppinglistapp.R

object FragmentManager {
    var currentFrag: BaseFragment? = null

    fun setFragment(newFrag: BaseFragment, activity: AppCompatActivity) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flPlaceHolder, newFrag)
        transaction.commit()
        currentFrag = newFrag
    }
}