package com.nurram.project.pencatatkeuangan.view.fragment.debt

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.nurram.project.pencatatkeuangan.view.activity.main.MainActivity
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainViewModel
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.AddDialogLayoutBinding
import com.nurram.project.pencatatkeuangan.databinding.FragmentDebtBinding
import com.nurram.project.pencatatkeuangan.db.Debt
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import java.util.*

class DebtFragment : Fragment() {
    private lateinit var binding: FragmentDebtBinding
    private var adapter: DebtAdapter? = null
    private var viewModel: MainViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDebtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProvider(it).get(MainViewModel::class.java) }
        populateRecycler()
        viewModel?.getAllDebt()?.observe(viewLifecycleOwner, {
            adapter?.setData(it?.toMutableList())
        })
    }

    private fun populateRecycler() {
        adapter = DebtAdapter( null) { it, it1 ->
            if (it1 == "delete") {
                (parentFragment?.activity as MainActivity).reduceValue("", it.total.toLong())

                viewModel?.deleteDebt(it)
                Toast.makeText(context, R.string.toast_hapus_berhasil, Toast.LENGTH_SHORT).show()
            } else {
                showAddDataDialog(it)
            }
        }

        binding.debtRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        binding.debtRecycler.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    private fun showAddDataDialog(debt: Debt) {
        val builder = context?.let { AlertDialog.Builder(it) }
        val dialogView = AddDialogLayoutBinding.inflate(layoutInflater)

       dialogView.apply {
           dialogTitle.setText(debt.judul)
           dialogAmount.setText(debt.total.toString())
           dialogDate.text = "Transaction date: ${debt.date?.let { DateUtil.formatDate(it) }}"
           dialogCheckboxIncome.visibility = View.GONE
       }

        var selectedDate = debt.date
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        dialogView.dialogShowDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                dialogView.dialogDate.text = "Transaction date: ${DateUtil.formatDate(calendar.time)}"
                selectedDate = calendar.time
            }, year, month, day).show()
        }

        builder?.setView(dialogView.root)
        builder?.setCancelable(true)
        builder?.setPositiveButton(R.string.dialog_simpan, null)

        val dialog = builder?.create()
        dialog?.show()
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            if (dialogView.dialogTitle.text.isNotBlank() && dialogView.dialogAmount.text.isNotBlank()
                && selectedDate != null
            ) {
                val innerDebt = Debt(
                    debt.id, dialogView.dialogTitle.text.toString(),
                    dialogView.dialogAmount.text.toString().toInt(),
                    selectedDate
                )

                viewModel?.updateDebt(innerDebt)
                dialog.dismiss()
            } else {
                Toast.makeText(context, R.string.toast_isi_kolom, Toast.LENGTH_SHORT).show()
            }
        }
    }
}