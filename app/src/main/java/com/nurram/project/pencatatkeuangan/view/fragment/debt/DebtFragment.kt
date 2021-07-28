package com.nurram.project.pencatatkeuangan.view.fragment.debt

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
import com.nurram.project.pencatatkeuangan.databinding.FragmentDebtBinding
import com.nurram.project.pencatatkeuangan.db.Debt
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import com.nurram.project.pencatatkeuangan.utils.PrefUtil
import com.nurram.project.pencatatkeuangan.view.ViewModelFactory
import com.nurram.project.pencatatkeuangan.view.activity.main.MainActivity
import com.nurram.project.pencatatkeuangan.view.activity.wallet.WalletActivity
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainViewModel
import java.util.*
import kotlin.collections.ArrayList

class DebtFragment : Fragment() {
    private lateinit var binding: FragmentDebtBinding
    private lateinit var walletId: String

    private var adapter: DebtAdapter? = null
    private var viewModel: DebtViewModel? = null
    private var debts: List<Debt>? = null

    private var isNewest = true

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

        viewModel = activity?.let {
            val pref = PrefUtil(requireContext())
            walletId = pref.getStringFromPref(WalletActivity.prefKey, "def")
            val factory = ViewModelFactory(it.application, walletId)
            ViewModelProvider(it, factory).get(DebtViewModel::class.java)
        }

        populateRecycler()
        getAllDebts()

        binding.debtSort.setOnClickListener {
            isNewest = !isNewest

            debts = debts?.reversed()
            debts?.let { it1 -> submitList(it1) }

            setOrderIcon()
        }

        var isFiltered = false
        binding.debtFilter.setOnClickListener {
            if (isFiltered) {
                binding.debtFilterText.text = getString(R.string.filter)
                getAllDebts()
            } else {
                showFilterDialog()
            }

            isFiltered = !isFiltered
        }
    }

    private fun getAllDebts() {
        viewModel?.getAllDebts(isNewest)?.observe(viewLifecycleOwner, {
            debts = it
            submitList(it)
        })
    }

    private fun populateRecycler() {
        adapter = DebtAdapter() { it, it1 ->
            if (it1 == "delete") {
                (parentFragment?.activity as MainActivity).reduceValue("", it.total.toLong())

                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle(getString(R.string.attention))
                dialog.setMessage(R.string.delete_record_confirmation)
                dialog.setCancelable(true)
                dialog.setPositiveButton("Yes") { _, _ ->
                    viewModel?.deleteDebt(it)
                    Toast.makeText(context, R.string.data_success_delete, Toast.LENGTH_SHORT).show()
                }
                dialog.setNegativeButton("Cancel") { innerDialog, _ ->
                    innerDialog.dismiss()
                }

                dialog.show()
            } else {
                showUpdateDataDialog(it)
            }
        }

        binding.debtRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        binding.debtRecycler.adapter = adapter
    }

    private fun submitList(debts: List<Debt>) {
        adapter?.currentList?.clear()
        adapter?.submitList(viewModel!!.mapData(debts as ArrayList<Debt>))
    }

    @SuppressLint("SetTextI18n")
    private fun showUpdateDataDialog(debt: Debt) {
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
                dialogView.dialogDate.text =
                    "Transaction date: ${DateUtil.formatDate(calendar.time)}"
                selectedDate = calendar.time
            }, year, month, day).show()
        }

        builder?.setView(dialogView.root)
        builder?.setCancelable(true)
        builder?.setPositiveButton(R.string.dialog_save, null)

        val dialog = builder?.create()
        dialog?.show()
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            if (dialogView.dialogTitle.text.isNotBlank() && dialogView.dialogAmount.text.isNotBlank()
                && selectedDate != null
            ) {
                val innerDebt = Debt(
                    debt.id, dialogView.dialogTitle.text.toString(),
                    dialogView.dialogAmount.text.toString().toInt(),
                    selectedDate,
                    walletId
                )

                viewModel?.updateDebt(innerDebt)
                resetOrderIcon()

                dialog.dismiss()
            } else {
                Toast.makeText(context, R.string.toast_isi_kolom, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setOrderIcon() {
        if (!isNewest) binding.debtSortImage.rotationX = 180.0.toFloat()
        else binding.debtSortImage.rotationX = 0.toFloat()

        if (!isNewest) binding.debtSortText.text = getString(R.string.sort_oldest)
        else binding.debtSortText.text = getString(R.string.sort_newest)
    }

    fun resetOrderIcon() {
        isNewest = true
        setOrderIcon()
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
                viewModel?.getFilteredDebt(startDate!!, endDate!!, isNewest)
                    ?.observe(viewLifecycleOwner, {
                        debts = it
                        submitList(it)

                        binding.debtFilterText.text = getString(R.string.remove_filter)
                    })
            }
        }

        dialog?.show()
    }
}