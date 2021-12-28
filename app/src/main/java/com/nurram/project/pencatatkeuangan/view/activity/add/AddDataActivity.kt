package com.nurram.project.pencatatkeuangan.view.activity.add

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ActivityAddDataBinding
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import com.nurram.project.pencatatkeuangan.utils.PrefUtil
import com.nurram.project.pencatatkeuangan.view.ViewModelFactory
import com.nurram.project.pencatatkeuangan.view.activity.notes.NotesActivity
import com.nurram.project.pencatatkeuangan.view.activity.wallet.WalletActivity
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainFragment
import java.util.*

class AddDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddDataBinding
    private lateinit var viewModel: AddDataViewModel
    private lateinit var walletId: String
    private lateinit var record: Record

    private var dataId = 0
    private var selectedDate = Date()
    private var isUpdate = false

    private val result =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val note = it.data?.getStringExtra(NotesActivity.NOTE)
                binding.etDesc.setText(note)
                binding.etDesc.clearFocus()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = PrefUtil(this)
        walletId = pref.getStringFromPref(WalletActivity.prefKey, DEFAULT_WALLET)

        val factory = ViewModelFactory(this.application, walletId)
        viewModel = ViewModelProvider(this, factory)[AddDataViewModel::class.java]

        binding = ActivityAddDataBinding.inflate(layoutInflater)
        binding.apply {
            setContentView(root)
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            etDate.inputType = InputType.TYPE_NULL
            etDate.keyListener = null
            etDate.setOnFocusChangeListener { _, b ->
                if (b) {
                    val picker =
                        DatePickerDialog(this@AddDataActivity, { _, year, monthOfYear, dayOfMonth ->
                            val calendar = Calendar.getInstance()
                            calendar.set(year, monthOfYear, dayOfMonth)
                            etDate.setText(DateUtil.formatDate(calendar.time))
                            selectedDate = calendar.time
                            etDate.clearFocus()
                        }, year, month, day)
                    picker.setOnCancelListener { etDate.clearFocus() }
                    picker.show()
                }
            }

            etDesc.apply {
                inputType = InputType.TYPE_NULL
                keyListener = null
                setOnFocusChangeListener { _, b ->
                    if (b) {
                        val i = Intent(this@AddDataActivity, NotesActivity::class.java)
                        i.putExtra(NotesActivity.NOTE_DATA, etDesc.text.toString())
                        result.launch(i)
                    }
                }
            }
        }

        val record = intent.getParcelableExtra<Record>(MainFragment.RECORD_DATA)
        if (record != null) populateUi(record)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (isUpdate) {
            menu?.add(0, MENU_DELETE, Menu.NONE, getString(R.string.dialog_delete))
                ?.setIcon(R.drawable.ic_delete_black_24dp)
                ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_data, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_save -> saveData()
            else -> deleteRecord(record)
        }

        return true
    }

    private fun populateUi(record: Record) {
        supportActionBar?.title = getString(R.string.update)
        this.record = record

        isUpdate = true
        dataId = record.id
        binding.apply {
            etTitle.setText(record.judul)
            etAmount.setText(record.total.toString())
            etDesc.setText(record.note)

            record.date?.let {
                etDate.setText(DateUtil.formatDate(it))
                selectedDate = it
            }

            when (record.description) {
                INCOME -> rgCategory.check(R.id.rb_income)
                EXPENSE -> rgCategory.check(R.id.rb_expense)
                else -> rgCategory.check(R.id.rb_debt)
            }
        }
    }

    private fun saveData() {
        binding.apply {
            val title = etTitle.text.toString()
            val amount = etAmount.text.toString()
            val description = etDesc.text.toString()
            val category = when (rgCategory.checkedRadioButtonId) {
                R.id.rb_income -> INCOME
                R.id.rb_expense -> EXPENSE
                R.id.rb_debt -> DEBT
                else -> ""
            }

            if (title.isBlank() ||
                amount.isBlank() ||
                category.isBlank()
            ) {
                Toast.makeText(
                    this@AddDataActivity,
                    R.string.toast_isi_kolom,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val record = Record(
                    dataId,
                    title,
                    amount.toLong(),
                    selectedDate,
                    walletId,
                    description,
                    category
                )

                if (isUpdate) {
                    viewModel.updateRecord(record)
                } else {
                    viewModel.insertRecord(record)
                }

                finish()
            }
        }
    }

    private fun deleteRecord(record: Record) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.attention))
        dialog.setMessage(R.string.delete_record_confirmation)
        dialog.setCancelable(true)
        dialog.setPositiveButton("Yes") { _, _ ->
            viewModel.deleteRecord(record)
            Toast.makeText(this, R.string.data_success_delete, Toast.LENGTH_SHORT).show()
            finish()
        }
        dialog.setNegativeButton("Cancel") { innerDialog, _ ->
            innerDialog.dismiss()
        }

        dialog.show()
    }

    companion object {
        const val INCOME = "income"
        const val EXPENSE = "expenses"
        const val DEBT = "debt"
        private const val DEFAULT_WALLET = "def"
        private const val MENU_DELETE = 999
    }
}