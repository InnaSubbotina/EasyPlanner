package com.inness.shoppinglistapp.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceManager
import com.inness.shoppinglistapp.R
import com.inness.shoppinglistapp.activities.MainActivity.Companion.CHANNEL_ID
import com.inness.shoppinglistapp.databinding.ActivityMainBinding
import com.inness.shoppinglistapp.dialogs.NewListDialog
import com.inness.shoppinglistapp.fragments.FragmentManager
import com.inness.shoppinglistapp.fragments.NoteFragment
import com.inness.shoppinglistapp.fragments.ReminderFragment
import com.inness.shoppinglistapp.fragments.ShopListNamesFragment
import com.inness.shoppinglistapp.settings.SettingsActivity
import com.inness.shoppinglistapp.utils.AlarmReceiver
import com.inness.shoppinglistapp.utils.AlarmReceiver.Companion.CHANNEL_ID

class MainActivity : AppCompatActivity(), NewListDialog.Listener {
    lateinit var binding: ActivityMainBinding
    private var currentMenuItemId = R.id.shop_list
    private var currentTheme = ""
    private lateinit var defPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()
        currentTheme = defPref.getString("theme_key","bright_blue").toString()
        FragmentManager.setFragment(ShopListNamesFragment.newInstance(),this)
        setBottomNavListener()

    }

       private fun setBottomNavListener(){
        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.settings->{
                    startActivity(Intent(this,SettingsActivity::class.java))
                }
                R.id.notes->{
                    currentMenuItemId = R.id.notes
                    FragmentManager.setFragment(NoteFragment.newInstance(),this)
                }
                R.id.shop_list->{
                    currentMenuItemId = R.id.shop_list
                    FragmentManager.setFragment(ShopListNamesFragment.newInstance(),this)
                }
                R.id.reminder-> {
                    currentMenuItemId = R.id.reminder
                    FragmentManager.setFragment(ReminderFragment.newInstance(),this)
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNav.selectedItemId = currentMenuItemId
        if(defPref.getString("theme_key","bright_blue") != currentTheme) recreate()

    }

    private fun getSelectedTheme() : Int {
        return if(defPref.getString("theme_key","bright_blue") == "bright_blue") {
            R.style.Theme_ShoppingListAppBrightBlue
        } else {
            R.style.Theme_ShoppingListAppBlue
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }


    companion object{
        const val CHANNEL_ID = "foxandroid"
    }

    override fun onClick(name: String) {
       Log.d("MyLog", "Name: $name")
    }

}