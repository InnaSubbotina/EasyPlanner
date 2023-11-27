package com.inness.shoppinglistapp.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.inness.shoppinglistapp.R
import com.inness.shoppinglistapp.databinding.ActivityNewNoteBinding
import com.inness.shoppinglistapp.entities.NoteItem
import com.inness.shoppinglistapp.fragments.NoteFragment
import com.inness.shoppinglistapp.utils.HtmlManager
import com.inness.shoppinglistapp.utils.MyTouchListener
import com.inness.shoppinglistapp.utils.TimeManager

class NewNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewNoteBinding
    private var note: NoteItem? = null
    private var pref: SharedPreferences? = null
    private lateinit var defPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        pref = PreferenceManager.getDefaultSharedPreferences(this)
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        setTextSize()
        setContentView(binding.root)
        actionBarSettings()
        getNote()
        initSetPickerListener()
        onClickColorPicker()
    }

    private fun initSetPickerListener() {
        binding.colorPicker.setOnTouchListener(MyTouchListener())
    }

    private fun onClickColorPicker() = with(binding) {
        ibRed.setOnClickListener {
            setColorForSelectedText(R.color.picker_red)
        }
        ibOran.setOnClickListener {
            setColorForSelectedText(R.color.picker_oran)
        }
        ibGreen.setOnClickListener {
            setColorForSelectedText(R.color.picker_green)
        }
        ibBlue.setOnClickListener {
            setColorForSelectedText(R.color.picker_blue)
        }
        ibPink.setOnClickListener {
            setColorForSelectedText(R.color.picker_pink)
        }
        ibBlack.setOnClickListener {
            setColorForSelectedText(R.color.picker_black)
        }
    }

    private fun getNote() {
        val serializNote = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY)
        if (serializNote != null) {
            note = serializNote as NoteItem
            fillNote()
        }
    }

    private fun fillNote() = with(binding) {
        edTitle.setText(note?.title)
        edDescription.setText(note?.content?.let { HtmlManager.getFromHtml(it).trim() })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_nemu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_save) {
            setMainResult()
        } else if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.id_bold) {
            setBoltForSelectedText()
        } else if (item.itemId == R.id.id_colorPicker) {
            if (binding.colorPicker.isShown) {
                closeColorPicker()
            } else {
                openColorPicker()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBoltForSelectedText() = with(binding) {
        val startPosition = edDescription.selectionStart
        val endPosition = edDescription.selectionEnd

        val styles = edDescription.text.getSpans(startPosition, endPosition, StyleSpan::class.java)
        var boldStyle: StyleSpan? = null
        if (styles.isNotEmpty()) {
            edDescription.text.removeSpan(styles[0])
        } else {
            boldStyle = StyleSpan(Typeface.BOLD)
        }
        edDescription.text.setSpan(
            boldStyle,
            startPosition,
            endPosition,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        edDescription.text.trim()
        edDescription.setSelection(startPosition)
    }

    private fun setColorForSelectedText(colorId: Int) = with(binding) {
        val startPosition = edDescription.selectionStart
        val endPosition = edDescription.selectionEnd
        val styles =
            edDescription.text.getSpans(startPosition, endPosition, ForegroundColorSpan::class.java)
        if (styles.isNotEmpty()) edDescription.text.removeSpan(styles[0])
        edDescription.text.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(this@NewNoteActivity, colorId)
            ),
            startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        edDescription.text.trim()
        edDescription.setSelection(startPosition)
    }

    private fun setMainResult() {
        var editState = "new"
        val tempNote: NoteItem?
        if (note == null) {
            tempNote = createNewNote()
        } else {
            editState = "update"
            tempNote = updateNote()
        }
        val intent = Intent().apply {
            putExtra(NoteFragment.NEW_NOTE_KEY, tempNote)
            putExtra(NoteFragment.EDIT_STATE_KEY, editState)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun updateNote(): NoteItem? = with(binding) {
        return note?.copy(
            title = edTitle.text.toString(),
            content = HtmlManager.toHtml(edDescription.text)
        )
    }

    private fun createNewNote(): NoteItem {
        return NoteItem(
            null,
            binding.edTitle.text.toString(),
            HtmlManager.toHtml(binding.edDescription.text),
            TimeManager.getCurrentTime(),
            ""
        )
    }

    private fun actionBarSettings() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openColorPicker() {
        binding.colorPicker.visibility = View.VISIBLE
        val openAnim = AnimationUtils.loadAnimation(this, R.anim.open_color_picker)
        binding.colorPicker.startAnimation(openAnim)
    }

    private fun closeColorPicker() {
        val openAnim = AnimationUtils.loadAnimation(this, R.anim.close_color_picker)
        openAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.colorPicker.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        binding.colorPicker.startAnimation(openAnim)
    }

    private fun setTextSize() = with(binding) {
        edTitle.setTextSize(pref?.getString("title_size_key", "16"))
        edDescription.setTextSize(pref?.getString("content_size_key", "14"))
    }

    private fun EditText.setTextSize(size: String?) {
        if (size != null) this.textSize = size.toFloat()
    }

    private fun getSelectedTheme(): Int {
        return if (defPref.getString("theme_key", "bright_blue") == "bright_blue") {
            R.style.Theme_NewNoteActivityBrightBlue
        } else {
            R.style.Theme_NewNoteActivityBlue
        }
    }
}