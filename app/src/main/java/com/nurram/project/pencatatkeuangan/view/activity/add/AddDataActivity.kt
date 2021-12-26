package com.nurram.project.pencatatkeuangan.view.activity.add

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ActivityAddDataBinding
import com.nurram.project.pencatatkeuangan.db.Debt
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import com.nurram.project.pencatatkeuangan.utils.PrefUtil
import com.nurram.project.pencatatkeuangan.view.ViewModelFactory
import com.nurram.project.pencatatkeuangan.view.activity.wallet.WalletActivity
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainViewModel
import java.util.*

class AddDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddDataBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var walletId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = PrefUtil(this)
        walletId = pref.getStringFromPref(WalletActivity.prefKey, DEFAULT_WALLET)

        val factory = ViewModelFactory(this.application, walletId)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        binding = ActivityAddDataBinding.inflate(layoutInflater)
        binding.apply {
            setContentView(root)
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Create"

            btnSaveData.setOnClickListener { saveData() }
        }
    }

    private fun saveData() {
        binding.apply {
            val title = etTitle.text.toString()
            val amount = etAmount.text.toString()
            val description = etDesc.text.toString()
            val category = when(rgCategory.checkedRadioButtonId) {
                R.id.rb_income -> INCOME
                R.id.rb_expense -> EXPENSE
                else -> DEBT
            }

            var selectedDate = Date()
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            etDate.setOnClickListener {
                DatePickerDialog(this@AddDataActivity, { _, year, monthOfYear, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, monthOfYear, dayOfMonth)
                    etDate.setText(DateUtil.formatDate(calendar.time))
                    selectedDate = calendar.time
                }, year, month, day).show()
            }

            if (title.isBlank() ||
                amount.isBlank() ||
                etDate.text.isNullOrBlank() ||
                category.isBlank()
            ) {
                Toast.makeText(
                    this@AddDataActivity,
                    R.string.toast_isi_kolom,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                when(category) {
                    DEBT -> {
                        val debt = Debt(0, title, amount.toInt(), selectedDate, walletId)
                        viewModel.insertDebt(debt)
                    }
                    else -> {
                        val record = Record(
                            0,
                            title,
                            amount.toLong(),
                            selectedDate,
                            description,
                            walletId,
                            category
                        )

                        viewModel.insertRecord(record)
                    }
                }
            }

            finish()
        }
    }

    companion object {
        private const val INCOME = "income"
        private const val EXPENSE = "expenses"
        private const val DEBT = "debt"
        private const val DEFAULT_WALLET = "def"
    }
}