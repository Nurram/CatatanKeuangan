package com.nurram.project.pencatatkeuangan.view.fragment.history

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.AddDialogLayoutBinding
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
        viewModel?.getAllRecords()?.observe(viewLifecycleOwner, {
            adapter?.setData(it?.toMutableList())
        })
    }

    private fun populateRecycler() {
        adapter = context?.let {
            HistoryAdapter(it, null, false) { record, it1 ->
                if (it1 == "delete") {
                    (parentFragment?.activity as MainActivity).reduceValue(record.description, record.total)

                    viewModel?.deleteRecord(record)
                    Toast.makeText(context, R.string.toast_hapus_berhasil, Toast.LENGTH_SHORT).show()
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
            dialogDate.text = "Transaction date: ${DateUtil.formatDate(record.date)}"
            dialogCheckboxIncome.isEnabled = false
        }

        var selectedDate = record.date
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        dialogView.dialogShowDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val date = "$dayOfMonth ${monthOfYear + 1} $year"
                dialogView.dialogDate.text = "Transaction date: ${DateUtil.formatDate(date)}"
                selectedDate = "$dayOfMonth $monthOfYear $year"
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
}
