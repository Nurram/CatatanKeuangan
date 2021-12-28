package com.nurram.project.pencatatkeuangan.view.activity.notes

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ActivityNotesBinding

class NotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val note = intent.getStringExtra(NOTE_DATA)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        binding.apply {
            setContentView(root)
            setSupportActionBar(tbNote)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = null
            etNote.setText(note)
            etNote.requestFocus()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_data, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> sendData()
            else -> sendData()
        }

        return true
    }

    override fun onBackPressed() = sendData()

    private fun sendData() {
        val i = Intent()
        i.putExtra(NOTE, binding.etNote.text.toString())
        setResult(RESULT_OK, i)
        finish()
    }

    companion object {
        const val NOTE = "note"
        const val NOTE_DATA = "note_data"
    }
}