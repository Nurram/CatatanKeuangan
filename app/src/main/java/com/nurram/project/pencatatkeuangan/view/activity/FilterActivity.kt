package com.nurram.project.pencatatkeuangan.view.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ActivityFilterBinding
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import com.nurram.project.pencatatkeuangan.view.activity.add.AddDataActivity
import java.util.*

class FilterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFilterBinding

    private val filters = arrayListOf<Any>()
    private var startDate: Date? = null
    private var endDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        binding = ActivityFilterBinding.inflate(layoutInflater)
        binding.apply {
            setContentView(root)
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = null

            etStartDate.inputType = InputType.TYPE_NULL
            etStartDate.keyListener = null
            etStartDate.setOnFocusChangeListener { _, b ->
                if (b) {
                    val picker =
                        DatePickerDialog(this@FilterActivity, { _, year, monthOfYear, dayOfMonth ->
                            val calendar = Calendar.getInstance()
                            calendar.set(year, monthOfYear, dayOfMonth)
                            etStartDate.setText(DateUtil.formatDate(calendar.time))
                            startDate = calendar.time
                            etStartDate.clearFocus()
                        }, year, month, day)
                    picker.setOnCancelListener { etStartDate.clearFocus() }
                    picker.show()
                }
            }

            etEndDate.inputType = InputType.TYPE_NULL
            etEndDate.keyListener = null
            etEndDate.setOnFocusChangeListener { _, b ->
                if (b) {
                    val picker =
                        DatePickerDialog(this@FilterActivity, { _, year, monthOfYear, dayOfMonth ->
                            val calendar = Calendar.getInstance()
                            calendar.set(year, monthOfYear, dayOfMonth)
                            etEndDate.setText(DateUtil.formatDate(calendar.time))
                            endDate = calendar.time
                            etEndDate.clearFocus()
                        }, year, month, day)
                    picker.setOnCancelListener { etEndDate.clearFocus() }
                    picker.show()
                }
            }

            btnFilter.setOnClickListener {
                val category = when (rgCategory.checkedRadioButtonId) {
                    R.id.rb_all -> ALL
                    R.id.rb_income -> AddDataActivity.INCOME
                    R.id.rb_expense -> AddDataActivity.EXPENSE
                    R.id.rb_debt -> AddDataActivity.DEBT
                    else -> ""
                }

                filters.add(category)
                if (startDate != null && endDate != null) {
                    filters.add(startDate!!)
                    filters.add(endDate!!)
                }

                val i = Intent()
                i.putExtra(CATEGORY, category)
                i.putExtra(START_DATE, startDate)
                i.putExtra(END_DATE, endDate)
                setResult(RESULT_OK, i)
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return true
    }

    companion object {
        private const val ALL = "%"
        const val CATEGORY = "category"
        const val START_DATE = "start_date"
        const val END_DATE = "end_date"
    }
}