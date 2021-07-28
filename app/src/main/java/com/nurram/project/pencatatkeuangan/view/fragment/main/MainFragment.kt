package com.nurram.project.pencatatkeuangan.view.fragment.main

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.AddDialogLayoutBinding
import com.nurram.project.pencatatkeuangan.databinding.FragmentMainBinding
import com.nurram.project.pencatatkeuangan.db.Debt
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.utils.CurrencyFormatter
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import com.nurram.project.pencatatkeuangan.utils.PrefUtil
import com.nurram.project.pencatatkeuangan.view.ViewModelFactory
import com.nurram.project.pencatatkeuangan.view.activity.wallet.WalletActivity
import com.nurram.project.pencatatkeuangan.view.fragment.debt.DebtFragment
import com.nurram.project.pencatatkeuangan.view.fragment.history.HistoryFragment
import java.util.*

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: PagerAdapter
    private lateinit var walletId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = PrefUtil(requireContext())
        walletId = pref.getStringFromPref(WalletActivity.prefKey, "def")
        val factory = ViewModelFactory(requireActivity().application, walletId)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        viewModel.getTotalExpenses()?.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.mainTotalExpenses.text = CurrencyFormatter.convertAndFormat(it.toLong())
            } else {
                binding.mainTotalExpenses.text = CurrencyFormatter.convertAndFormat(0)
            }
        })

        viewModel.getTotalIncome()?.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.mainTotalIncome.text = CurrencyFormatter.convertAndFormat(it.toLong())
            } else {
                binding.mainTotalIncome.text = CurrencyFormatter.convertAndFormat(0)
            }
        })

        adapter = PagerAdapter(childFragmentManager)
        binding.apply {
            mainViewPager.adapter = adapter
            mainViewPager.offscreenPageLimit = 3
            mainViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(p0: Int) {}

                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                    if (p0 == 1) {
                        historyFab.hide()
                        debtFab.show()
                    } else {
                        historyFab.show()
                        debtFab.hide()
                    }
                }

                override fun onPageSelected(p0: Int) {
                    if (p0 == 0) {
                        historyFab.hide()
                        debtFab.show()
                    } else {
                        historyFab.show()
                        debtFab.hide()
                    }
                }
            })

            binding.mainTabLayout.setupWithViewPager(mainViewPager)

            historyFab.setOnClickListener { showAddDataDialog("history") }
            debtFab.setOnClickListener { showAddDataDialog("utang") }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showAddDataDialog(key: String) {
        val builder = context?.let { AlertDialog.Builder(it) }
        val dialogView = AddDialogLayoutBinding.inflate(layoutInflater)

        var selectedDate = Date()
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        when (key) {
            "utang" -> dialogView.dialogCheckboxIncome.visibility = View.GONE
        }

        builder?.setView(dialogView.root)
        dialogView.dialogShowDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                dialogView.dialogDate.text =
                    "Transaction date: ${DateUtil.formatDate(calendar.time)}"
                selectedDate = calendar.time
            }, year, month, day).show()
        }

        builder?.setCancelable(true)
        builder?.setPositiveButton(R.string.dialog_save, null)
        val dialog = builder?.create()
        dialog?.show()
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            val isIncome = if (dialogView.dialogCheckboxIncome.isChecked) {
                "income"
            } else {
                "expenses"
            }

            if (dialogView.dialogTitle.text.isNotBlank() &&
                dialogView.dialogAmount.text.isNotBlank() &&
                dialogView.dialogAmount.text.toString().toLong() > 0
            ) {

                val totalIncomeString = dialogView.dialogAmount.text.toString()

                if (key == "history") {
                    val totalIncome = CurrencyFormatter.isAmountValidLong(requireContext(),
                        totalIncomeString)
                    val record = Record(
                        0,
                        dialogView.dialogTitle.text.toString(),
                        totalIncome,
                        selectedDate,
                        walletId,
                        isIncome
                    )

                    val fragment = adapter.getItem(0) as HistoryFragment
                    fragment.resetOrderIcon()

                    if (totalIncome > 0) viewModel.insertRecord(record)
                } else {
                    val totalIncome = CurrencyFormatter.isAmountValid(requireContext(),
                        totalIncomeString)

                    val debt = Debt(
                        0,
                        dialogView.dialogTitle.text.toString(),
                        totalIncome,
                        selectedDate,
                        walletId
                    )

                    val fragment = adapter.getItem(1) as DebtFragment
                    fragment.resetOrderIcon()

                    if(totalIncome > 0) viewModel.insertDebt(debt)
                }

                dialog.dismiss()
            } else {
                Toast.makeText(context, R.string.toast_isi_kolom, Toast.LENGTH_SHORT).show()
            }
        }
        dialog?.getButton(AlertDialog.BUTTON_NEGATIVE)?.visibility = View.GONE
    }
}