package com.inness.shoppinglistapp.fragments


import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inness.shoppinglistapp.R
import com.inness.shoppinglistapp.activities.AlarmActivity
import com.inness.shoppinglistapp.activities.MainApp
import com.inness.shoppinglistapp.database.AlarmAdapter
import com.inness.shoppinglistapp.databinding.FragmentReminderBinding
import com.inness.shoppinglistapp.dialogs.DeleteDialog
import com.inness.shoppinglistapp.entities.AlarmItem
import com.inness.shoppinglistapp.utils.AlarmReceiver
import com.inness.shoppinglistapp.utils.SwipeToDelete
import com.inness.shoppinglistapp.viewmodel.MainViewModel

class ReminderFragment : BaseFragment(), AlarmAdapter.Listener {

    private lateinit var binding: FragmentReminderBinding
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: AlarmAdapter
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent


    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onEditResult()
    }

    override fun onClickNew() {
        binding.newItemFab.setOnClickListener {
            editLauncher.launch(Intent(activity,AlarmActivity::class.java))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentReminderBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        initRcView()
        onClickNew()
    }

    private fun initRcView() = with(binding) {
        rcViewReminder.layoutManager = LinearLayoutManager(activity)
        adapter = AlarmAdapter(this@ReminderFragment)
        rcViewReminder.adapter = adapter
    }

    private fun observer() {
        mainViewModel.allAlarm.observe(
            viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun onEditResult() {
        editLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                val editState  = it.data?.getStringExtra(EDIT_STATE_KEY)
                //data это интент который мы передаем
                if (editState == "update") {
                    mainViewModel.updateAlarmItem(it.data?.getSerializableExtra(NEW_ALARM_KEY) as AlarmItem)
                } else {
                    mainViewModel.insertAlarmItem(it.data?.getSerializableExtra(NEW_ALARM_KEY) as AlarmItem)
                }
            }
        }
    }

    override fun onClickAlarmSwitch(alarm: AlarmItem) {
        val title = alarm.title
        val desc = alarm.content
        val myNotification = "$title\n$desc"
        alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pendingIntent = Intent(activity, AlarmReceiver::class.java).let { intent ->
            intent.putExtra("myNotification",myNotification)
            PendingIntent.getBroadcast(activity,alarm.id!!,intent,PendingIntent.FLAG_IMMUTABLE)
        }
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            alarm.timeInMillis,
            pendingIntent
        )
        Toast.makeText(activity,getString(R.string.reminder_turn_off),Toast.LENGTH_LONG).show()
    }

    override fun onClickAlarmSwitchOff(alarm: AlarmItem) {
        alarmManager.cancel(pendingIntent)
        Toast.makeText(activity, getString(R.string.reminder_was_cancel), Toast.LENGTH_SHORT).show()
    }

    override fun deleteAlarmItem(id: Int) {
        DeleteDialog.showDialog(context as AppCompatActivity, object : DeleteDialog.Listener {
            override fun onClick() {
                mainViewModel.deleteAlarmItem(id)
                Toast.makeText(activity,getString(R.string.reminder_was_delete),Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onClickAlarmItem(alarm: AlarmItem) {
        val intent = Intent (activity, AlarmActivity::class.java).apply {
            putExtra(NEW_ALARM_KEY,alarm)
        }
        editLauncher.launch(intent)
    }

    companion object{
        const val NEW_ALARM_KEY = "title_key"
        const val EDIT_STATE_KEY = "edit_state_key"
        @JvmStatic
        fun newInstance() = ReminderFragment()
    }
}





