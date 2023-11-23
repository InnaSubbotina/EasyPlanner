package com.inness.shoppinglistapp.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.inness.shoppinglistapp.R
import com.inness.shoppinglistapp.activities.MainApp
import com.inness.shoppinglistapp.activities.NewNoteActivity
import com.inness.shoppinglistapp.database.NoteAdapter
import com.inness.shoppinglistapp.databinding.FragmentNoteBinding
import com.inness.shoppinglistapp.dialogs.DeleteDialog
import com.inness.shoppinglistapp.entities.NoteItem
import com.inness.shoppinglistapp.viewmodel.MainViewModel


class NoteFragment : BaseFragment(), NoteAdapter.Listener {
    private lateinit var binding: FragmentNoteBinding
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: NoteAdapter
    private lateinit var defPref: SharedPreferences

    private val mainViewModel: MainViewModel by activityViewModels {
       MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onEditResult()
    }

    override fun onClickNew() {
        binding.newItemFab.setOnClickListener {
            editLauncher.launch(Intent(activity,NewNoteActivity::class.java))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
       binding = FragmentNoteBinding.inflate(inflater,container,false)
       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
        onClickNew()
    }

    private fun initRcView() = with(binding){
        defPref=PreferenceManager.getDefaultSharedPreferences(activity)
        rcNewNote.layoutManager = getLayoutManger()
        adapter = NoteAdapter(this@NoteFragment,defPref)
        rcNewNote.adapter = adapter
    }

    private fun getLayoutManger(): RecyclerView.LayoutManager {
        return if(defPref.getString("note_style_key","Linear") == "Linear") {
            LinearLayoutManager(activity)
        } else {
            StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun observer(){
        mainViewModel.allNotes.observe(
            viewLifecycleOwner, {
                adapter.submitList(it)
         })
    }

    private fun onEditResult(){
        editLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                val editState  = it.data?.getStringExtra(EDIT_STATE_KEY)
                if (editState == "update") {
                    mainViewModel.updateNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItem)
                } else {
                    mainViewModel.insertNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItem)
                }
            }
        }
    }

    override fun deleteItem(id: Int) {
        DeleteDialog.showDialog(context as AppCompatActivity, object : DeleteDialog.Listener {
            override fun onClick() {
                mainViewModel.deleteNote(id)
                Toast.makeText(activity,getString(R.string.note_was_delete), Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onClickItem(note: NoteItem) {
        val intent = Intent (activity, NewNoteActivity::class.java).apply {
            putExtra(NEW_NOTE_KEY,note)
        }
       editLauncher.launch(intent)
    }

    companion object {
        const val NEW_NOTE_KEY = "title_key"
        const val EDIT_STATE_KEY = "edit_state_key"
        @JvmStatic
        fun newInstance() = NoteFragment()
    }
}
