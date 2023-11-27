package com.inness.shoppinglistapp.activities


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.inness.shoppinglistapp.R
import com.inness.shoppinglistapp.databinding.ActivityAlarmBinding
import com.inness.shoppinglistapp.entities.AlarmItem
import com.inness.shoppinglistapp.fragments.ReminderFragment
import com.inness.shoppinglistapp.utils.HtmlManager
import java.util.*


class AlarmActivity : AppCompatActivity() {

    private lateinit var picker: MaterialTimePicker
    private var calendar = Calendar.getInstance()
    private lateinit var binding: ActivityAlarmBinding
    private var alarm: AlarmItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBarSettings()
        getAlarm()
        binding.ibChooseTime.setOnClickListener {
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText(getString(R.string.choose_time))
            .build()
        picker.show(supportFragmentManager, "android")
        picker.addOnPositiveButtonClickListener {
            binding.tvSelectedTime.setText(getTimeFromPicker())
            calendar = Calendar.getInstance()
            calendar.set(Calendar.SECOND,0)
            calendar.set(Calendar.MILLISECOND,0)
            calendar.set(Calendar.HOUR_OF_DAY,picker.hour)
            calendar.set(Calendar.MINUTE,picker.minute)
        }
    }

    private fun getTimeFromPicker(): String {
        val h = picker.hour
        val m = picker.minute
        return String.format("%02d:%02d", h, m)
    }

    private fun getAlarm() {
        val serializAlarm = intent.getSerializableExtra(ReminderFragment.NEW_ALARM_KEY)
        if (serializAlarm != null) {
            alarm = serializAlarm as AlarmItem
            fillAlarmItem()
        }
    }

    private fun fillAlarmItem() = with(binding) {
        if(alarm != null) {
            edTitleAlarm.setText(alarm?.title)
            edDescriptionAlarm.setText(alarm?.content?.let { HtmlManager.getFromHtml(it).trim()})
            tvSelectedTime.text = alarm?.time
        }
    }

    private fun createNewAlarm(): AlarmItem {
        return AlarmItem(
            null,
            binding.edTitleAlarm.text.toString(),
            binding.edDescriptionAlarm.text.toString(),
            getTimeFromPicker(),
            calendar.timeInMillis,
            null
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_alarm_nemu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.id_save_alarm) {
            setMainResult()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setMainResult() {
        var editState = "new"
        val tempAlarm: AlarmItem?
        if (alarm == null) {
            tempAlarm = createNewAlarm()
        } else {
            editState = "update"
            tempAlarm = updateAlarm()
        }
        val intent = Intent().apply {
            putExtra(ReminderFragment.NEW_ALARM_KEY, tempAlarm)
            putExtra(ReminderFragment.EDIT_STATE_KEY, editState)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun updateAlarm(): AlarmItem? = with(binding) {
        return alarm?.copy(
            title = edTitleAlarm.text.toString(),
            content = edDescriptionAlarm.text.toString(),
            time = getTimeFromPicker()
        )
    }

    private fun actionBarSettings() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }
}