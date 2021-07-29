package com.nurram.project.pencatatkeuangan.view.fragment.history

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.AddDialogLayoutBinding
import com.nurram.project.pencatatkeuangan.databinding.FilterDialogLayoutBinding
import com.nurram.project.pencatatkeuangan.databinding.FragmentHistoryBinding
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.utils.CurrencyFormatter
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import com.nurram.project.pencatatkeuangan.utils.PrefUtil
import com.nurram.project.pencatatkeuangan.view.ViewModelFactory
import com.nurram.project.pencatatkeuangan.view.activity.main.MainActivity
import com.nurram.project.pencatatkeuangan.view.activity.wallet.WalletActivity
import java.util.*


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var walletId: String

    private var viewModel: HistoryViewModel? = null
    private var adapter: HistoryAdapter? = null
    private var records: List<Record>? = null

    private var isNewest = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.let {
            val pref = PrefUtil(requireContext())
            walletId = pref.getStringFromPref(WalletActivity.prefKey, "def")
            val factory = ViewModelFactory(it.application, walletId)
            ViewModelProvider(it, factory).get(HistoryViewModel::class.java)
        }

        populateRecycler()
        getAllRecords()

        binding.historySort.setOnClickListener {
            isNewest = !isNewest

            records = records?.reversed()
            records?.let { it1 ->
                submitList(it1)
            }

            setOrderIcon()
        }

        var isFiltered = false
        binding.historyFilter.setOnClickListener {
            if (isFiltered) {
                binding.historyFilterText.text = getString(R.string.filter)
                getAllRecords()
            } else {
                showFilterDialog()
            }

            isFiltered = !isFiltered
        }
    }

    private fun setOrderIcon() {
        if (!isNewest) binding.historySortImage.rotationX = 180.0.toFloat()
        else binding.historySortImage.rotationX = 0.toFloat()

        if (!isNewest) binding.historySortText.text = getString(R.string.sort_oldest)
        else binding.historySortText.text = getString(R.string.sort_newest)
    }

    private fun getAllRecords() {
        viewModel?.getAllRecords(isNewest)?.observe(viewLifecycleOwner, {
            records = it
            submitList(it)
        })
    }

    private fun deleteRecords(record: Record) {
        (parentFragment?.activity as MainActivity).reduceValue(
            record.description,
            record.total
        )

        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle(getString(R.string.attention))
        dialog.setMessage(R.string.delete_record_confirmation)
        dialog.setCancelable(true)
        dialog.setPositiveButton("Yes") { _, _ ->
            viewModel?.deleteRecord(record)
            Toast.makeText(context, R.string.data_success_delete, Toast.LENGTH_SHORT).show()
        }
        dialog.setNegativeButton("Cancel") { innerDialog, _ ->
            innerDialog.dismiss()
        }

        dialog.show()
    }

    private fun populateRecycler() {
        adapter = context?.let {
            HistoryAdapter(it) { record, it1 ->
                if (it1 == "delete") {
                    deleteRecords(record)
                } else {
                    showUpdateDataDialog(record)
                }
            }
        }

        binding.historyRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        binding.historyRecycler.adapter = adapter
    }

    private fun submitList(records: List<Record>) {
        val data = records.dropWhile { it.type == 1 }
        val mappedData = viewModel!!.mapData(data as ArrayList<Record>)
        adapter?.submitList(mappedData)
    }

    @SuppressLint("SetTextI18n")
    private fun showUpdateDataDialog(record: Record) {
        val dialog = context?.let { AlertDialog.Builder(it) }
        val dialogView = AddDialogLayoutBinding.inflate(layoutInflater)

        var selectedDate = record.date
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        dialogView.apply {
            dialogTitle.setText(record.judul)
            dialogAmount.setText(record.total.toString())
            dialogDate.text = "${getString(R.string.tanggal_transaksi)} ${
                record.date?.let {
                    DateUtil.formatDate(it)
                }
            }"
            dialogCheckboxIncome.isChecked = when (record.description) {
                "income" -> true
                else -> false
            }

            dialogShowDate.setOnClickListener {
                DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, monthOfYear, dayOfMonth)
                    dialogView.dialogDate.text =
                        "${getString(R.string.tanggal_transaksi)} ${DateUtil.formatDate(calendar.time)}"
                    selectedDate = calendar.time
                }, year, month, day).show()
            }
        }

        dialog?.apply {
            setView(dialogView.root)
            setCancelable(true)
            setPositiveButton(R.string.dialog_save) { _, _ ->

                if (dialogView.dialogTitle.text.isNotBlank() &&
                    dialogView.dialogAmount.text.isNotBlank() &&
                    dialogView.dialogAmount.text.toString().toLong() > 0
                ) {
                    if (dialogView.dialogCheckboxIncome.isChecked) {
                        record.description = "income"
                    } else {
                        record.description = "expenses"
                    }

                    val totalIncomeString = dialogView.dialogAmount.text.toString()
                    val totalIncome = CurrencyFormatter.isAmountValidLong(
                        requireContext(),
                        totalIncomeString
                    )
                    val innerRecord = Record(
                        record.id, dialogView.dialogTitle.text.toString(),
                        totalIncome,
                        selectedDate,
                        walletId,
                        record.description
                    )

                    viewModel?.updateRecord(innerRecord)
                    adapter?.currentList?.filterIndexed { index, record ->
                        if (record.id == innerRecord.id) adapter!!.notifyItemChanged(index)
                        true
                    }

                    resetOrderIcon()
                } else {
                    Toast.makeText(context, R.string.toast_isi_kolom, Toast.LENGTH_SHORT).show()
                }
            }
            setNegativeButton(R.string.dialog_delete) { _, _ ->
                viewModel?.deleteRecord(record)
                Toast.makeText(
                    requireContext(),
                    R.string.data_success_delete, Toast.LENGTH_SHORT
                ).show()
            }

            show()
        }
    }

    fun resetOrderIcon() {
        isNewest = true
        setOrderIcon()
    }

    private fun initAdapter() {

    }
    private fun showFilterDialog() {
        val dialog = context?.let { AlertDialog.Builder(it) }
        val dialogView = FilterDialogLayoutBinding.inflate(layoutInflater)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        var startDate: Date? = null
        var endDate: Date? = null

        dialogView.filterStartDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                startDate = calendar.time
                dialogView.filterStartDate.text = DateUtil.formatDate(calendar.time)
            }, year, month, day).show()
        }

        dialogView.filterEndDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                endDate = calendar.time
                dialogView.filterEndDate.text = DateUtil.formatDate(calendar.time)
            }, year, month, day).show()
        }

        dialog?.setView(dialogView.root)
        dialog?.setCancelable(true)
        dialog?.setPositiveButton(R.string.dialog_save) { _, _ ->
            if (startDate != null && endDate != null) {
                viewModel?.getFilteredRecord(startDate!!, endDate!!, isNewest)?.observe(
                    viewLifecycleOwner,
                    {
                        records = it
                        submitList(it)

                        binding.historyFilterText.text = getString(R.string.remove_filter)
                    })
            }
        }

        dialog?.show()
    }
}
