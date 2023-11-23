package com.inness.shoppinglistapp.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Ringtone
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.inness.shoppinglistapp.database.NoteAdapter
import com.inness.shoppinglistapp.databinding.ActivityAlarmRingBinding
import com.inness.shoppinglistapp.entities.AlarmItem
import com.inness.shoppinglistapp.entities.NoteItem
import com.inness.shoppinglistapp.utils.AlarmReceiver
import com.inness.shoppinglistapp.utils.HtmlManager
import com.inness.shoppinglistapp.utils.TimeManager

class AlarmRingsActivity : AppCompatActivity() {

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var ringtone: Ringtone
    private lateinit var binding: ActivityAlarmRingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmRingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBarSettings()

        val t = intent.getStringExtra("title")
        val d = intent.getStringExtra("desc")
        binding.tvTitleRem.text = t
        binding.tvDescriptionRem.text = d

        binding.tvCurrentTime.text = TimeManager.getCurrentTimeForRingAct()
        binding.buttonCancelAlarm.setOnClickListener {
            cancelAlarm()
        }
    }

    private fun cancelAlarm() {
        alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        intent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE)
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
            Toast.makeText(this, "Будильник отменен", Toast.LENGTH_SHORT).show()
        }

    }

    private fun actionBarSettings() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        if(ringtone != null && ringtone.isPlaying){
            ringtone.stop()
        }
        super.onDestroy()
    }

}
