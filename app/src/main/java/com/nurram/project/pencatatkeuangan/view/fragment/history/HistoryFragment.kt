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
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import com.nurram.project.pencatatkeuangan.view.activity.main.MainActivity
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainViewModel
import java.util.*


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding

    private var viewModel: MainViewModel? = null
    private var adapter: HistoryAdapter? = null
    private var records: List<Record>? = null

    private var startDate: Date? = null
    private var endDate: Date? = null

    private var isNewest = true
    private var isFiltered = false

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
        viewModel = activity?.let { ViewModelProvider(it).get(MainViewModel::class.java) }
        populateRecycler()
        getAllRecords()

        binding.historySort.setOnClickListener {
            isNewest = !isNewest

            records = records?.reversed()
            adapter?.setData(records?.toMutableList())

            if(!isNewest) binding.historySortImage.rotationX = 180.0.toFloat()
            else binding.historySortImage.rotationX = 0.toFloat()

            if(!isNewest) binding.historySortText.text = getString(R.string.sort_oldest)
            else binding.historySortText.text = getString(R.string.sort_newest)
        }

        binding.historyFilter.setOnClickListener {
            if(isFiltered) {
                binding.historyFilterText.text = getString(R.string.filter)
                getAllRecords()
            } else {
                showFilterDialog()
            }

            isFiltered = !isFiltered
        }
    }

    private fun getAllRecords() {
        viewModel?.getAllRecords(isNewest)?.observe(viewLifecycleOwner, {
            records = it
            adapter?.setData(it?.toMutableList())
        })
    }

    private fun deleteRecords(record: Record) {
        (parentFragment?.activity as MainActivity).reduceValue(
            record.description,
            record.total
        )

        viewModel?.deleteRecord(record)
        Toast.makeText(context, R.string.toast_hapus_berhasil, Toast.LENGTH_SHORT).show()
    }

    private fun populateRecycler() {
        adapter = context?.let {
            HistoryAdapter(it, null, false) { record, it1 ->
                if (it1 == "delete") {
                   deleteRecords(record)
                } else {
                    showAddDataDialog(record)
                }
            }
        }

        binding.historyRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        binding.historyRecycler.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    private fun showAddDataDialog(record: Record) {
        val dialog = context?.let { AlertDialog.Builder(it) }
        val dialogView = AddDialogLayoutBinding.inflate(layoutInflater)

        dialogView.apply {
            dialogTitle.setText(record.judul)
            dialogAmount.setText(record.total.toString())
            dialogDate.text = "Transaction date: ${record.date?.let { DateUtil.formatDate(it) }}"
            dialogCheckboxIncome.isEnabled = false
        }

        var selectedDate = record.date
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        dialogView.dialogShowDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                dialogView.dialogDate.text =
                    "Transaction date: ${DateUtil.formatDate(calendar.time)}"
                selectedDate = calendar.time
            }, year, month, day).show()
        }

        dialog?.setView(dialogView.root)
        dialog?.setCancelable(true)
        dialog?.setPositiveButton(R.string.dialog_simpan) { _, _ ->
            val innerRecord = Record(
                record.id, dialogView.dialogTitle.text.toString(),
                dialogView.dialogAmount.text.toString().toLong(),
                selectedDate,
                record.description
            )

            viewModel?.updateRecord(innerRecord)
        }

        dialog?.show()
    }

    private fun showFilterDialog() {
        val dialog = context?.let { AlertDialog.Builder(it) }
        val dialogView = FilterDialogLayoutBinding.inflate(layoutInflater)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

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
        dialog?.setPositiveButton(R.string.dialog_simpan) { _, _ ->
           if(startDate != null && endDate != null) {
               viewModel?.getFilteredRecord(startDate!!, endDate!!, isNewest)?.observe(
                   viewLifecycleOwner,
                   {
                       records = it
                       adapter?.setData(it?.toMutableList())

                       binding.historyFilterText.text = getString(R.string.remove_filter)
                   })
           }
        }

        dialog?.show()
    }
}
