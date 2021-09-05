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
import com.nurram.project.pencatatkeuangan.utils.*
import com.nurram.project.pencatatkeuangan.view.ViewModelFactory
import com.nurram.project.pencatatkeuangan.view.activity.wallet.WalletActivity
import java.util.*

class DebtFragment : Fragment() {
    private lateinit var binding: FragmentDebtBinding
    private lateinit var walletId: String
    private lateinit var debtAdapter: DebtAdapter
    private lateinit var viewModel: DebtViewModel

    private var debts = listOf<Debt>()
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

        val pref = PrefUtil(requireContext())
        walletId = pref.getStringFromPref(WalletActivity.prefKey, "def")
        val factory = ViewModelFactory(requireActivity().application, walletId)
        viewModel = ViewModelProvider(requireActivity(), factory).get(DebtViewModel::class.java)

        populateRecycler()
        getAllDebts()

        binding.debtSort.setOnClickListener {
            isNewest = !isNewest

            debts = debts.reversed()
            debts.let { it1 ->
                binding.debtRecycler.adapter = debtAdapter
                submitList(it1)
            }

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
        viewModel.getAllDebts(isNewest)?.observe(viewLifecycleOwner, {
            debts = it
            submitList(it)

            if (it.isNotEmpty()) {
                binding.debtRecycler.VISIBLE()
                binding.debtEmpty.GONE()
            } else {
                binding.debtRecycler.GONE()
                binding.debtEmpty.VISIBLE()
            }
        })
    }

    private fun populateRecycler() {
        debtAdapter = DebtAdapter { it, it1 ->
            if (it1 == "delete") {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(getString(R.string.attention))
                    setMessage(R.string.delete_record_confirmation)
                    setCancelable(true)
                    setPositiveButton("Yes") { _, _ ->
                        viewModel.deleteDebt(it)
                        Toast.makeText(context, R.string.data_success_delete, Toast.LENGTH_SHORT).show()
                    }
                    setNegativeButton("Cancel") { innerDialog, _ ->
                        innerDialog.dismiss()
                    }
                    show()
                }
            } else {
                showUpdateDataDialog(it)
            }
        }

        binding.debtRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            this.adapter = debtAdapter
        }
    }

    private fun submitList(records: List<Debt>) {
        val data = records.dropWhile { it.type == 1 }
        val mappedData = viewModel.mapData(data as ArrayList<Debt>)
        debtAdapter.submitList(mappedData)
    }

    @SuppressLint("SetTextI18n")
    private fun showUpdateDataDialog(debt: Debt) {
        val dialog = context?.let { AlertDialog.Builder(it) }
        val dialogView = AddDialogLayoutBinding.inflate(layoutInflater)

        var selectedDate = debt.date
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        dialogView.apply {
            dialogTitle.setText(debt.judul)
            dialogAmount.setText(debt.total.toString())
            dialogDate.text = "Transaction date: ${debt.date?.let { DateUtil.formatDate(it) }}"
            dialogCheckboxIncome.visibility = View.GONE
        }

        dialogView.dialogShowDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                dialogView.dialogDate.text =
                    "Transaction date: ${DateUtil.formatDate(calendar.time)}"
                selectedDate = calendar.time
            }, year, month, day).show()
        }

        dialog?.apply {
            setView(dialogView.root)
            setCancelable(true)
            setPositiveButton(R.string.dialog_save) { _, _ ->
                if (dialogView.dialogTitle.text.isNotBlank() &&
                    dialogView.dialogAmount.text.isNotBlank() &&
                    dialogView.dialogAmount.text.toString().toLong() > 0
                ) {
                    val totalIncomeString = dialogView.dialogAmount.text.toString()
                    val totalIncome = CurrencyFormatter.isAmountValid(
                        requireContext(),
                        totalIncomeString
                    )
                    val innerRecord = Debt(
                        debt.id,
                        dialogView.dialogTitle.text.toString(),
                        totalIncome,
                        selectedDate,
                        walletId
                    )

                    viewModel.updateDebt(innerRecord)

                    resetOrderIcon()
                } else {
                    Toast.makeText(context, R.string.toast_isi_kolom, Toast.LENGTH_SHORT).show()
                }
            }
            show()
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
                viewModel.getFilteredDebt(startDate!!, endDate!!, isNewest)
                    ?.observe(viewLifecycleOwner, {
                        debts = it
                        submitList(it)
                        debtAdapter.notifyDataSetChanged()

                        binding.debtFilterText.text = getString(R.string.remove_filter)
                    })
            }
        }

        dialog?.show()
    }
}