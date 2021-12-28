package com.nurram.project.pencatatkeuangan.view.fragment.main

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.FilterDialogLayoutBinding
import com.nurram.project.pencatatkeuangan.databinding.FragmentMainBinding
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.utils.*
import com.nurram.project.pencatatkeuangan.view.ViewModelFactory
import com.nurram.project.pencatatkeuangan.view.activity.wallet.WalletActivity
import java.util.*

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var walletId: String
    private lateinit var dataAdapter: MainAdapter

    private val date = DateUtil.getCurrentMonthAndYearDate()
    private val calendar = DateUtil.getMaxDateCalendar()
    private var records = listOf<Record>()
    private var isNewest = true

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
        walletId = pref.getStringFromPref(WalletActivity.prefKey, DEFAULT_WALLET)
        val factory = ViewModelFactory(requireActivity().application, walletId)

        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        initObservable()

        binding.history.apply {
            tvTransactionMonth.text =
                getString(R.string.transactions_in_s, DateUtil.getCurrentMonthName())

            historySort.setOnClickListener {
                isNewest = !isNewest

                records = records.reversed()
                submitList(records)

                setOrderIcon()
            }

            var isFiltered = false
            historyFilter.setOnClickListener {
                if (isFiltered) {
                    historyFilterText.text = getString(R.string.filter)
                    getAllRecords()
                } else {
                    showFilterDialog()
                }

                isFiltered = !isFiltered
            }
        }

        populateRecycler()
        getAllRecords()
    }

    private fun initObservable() {
        viewModel.getBalance()?.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.tvCurrentBalance.text = CurrencyFormatter.convertAndFormat(it)
            } else {
                binding.tvCurrentBalance.text = CurrencyFormatter.convertAndFormat(0)
            }
        })

        viewModel.getCurrentTotalExpenses(date, calendar.time)?.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.tvCurrentExpense.text = CurrencyFormatter.convertAndFormat(it.toLong())
            } else {
                binding.tvCurrentExpense.text = CurrencyFormatter.convertAndFormat(0)
            }
        })

        viewModel.getCurrentTotalIncome(date, calendar.time)?.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.tvCurrentIncome.text = CurrencyFormatter.convertAndFormat(it.toLong())
            } else {
                binding.tvCurrentIncome.text = CurrencyFormatter.convertAndFormat(0)
            }
        })
    }

    private fun setOrderIcon() {
        binding.history.apply {
            if (!isNewest) historySortImage.rotationX = 180.0.toFloat()
            else historySortImage.rotationX = 0.toFloat()

            if (!isNewest) historySortText.text = getString(R.string.sort_oldest)
            else historySortText.text = getString(R.string.sort_newest)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
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
            val datePicker =
                DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, monthOfYear, dayOfMonth)
                    startDate = calendar.time
                    dialogView.filterStartDate.text = DateUtil.formatDate(calendar.time)
                }, year, month, day)
            datePicker.datePicker.minDate = date.time
            datePicker.datePicker.maxDate = calendar.timeInMillis
            datePicker.show()
        }

        dialogView.filterEndDate.setOnClickListener {
            val datePicker =
                DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, monthOfYear, dayOfMonth)
                    endDate = calendar.time
                    dialogView.filterEndDate.text = DateUtil.formatDate(calendar.time)
                }, year, month, day)
            datePicker.datePicker.minDate = date.time
            datePicker.datePicker.maxDate = calendar.timeInMillis
            datePicker.show()
        }

        dialog?.setView(dialogView.root)
        dialog?.setCancelable(true)
        dialog?.setPositiveButton(R.string.dialog_save) { _, _ ->
            if (startDate != null && endDate != null) {
                viewModel.getFilteredRecord(startDate!!, endDate!!, isNewest)?.observe(
                    viewLifecycleOwner,
                    {
                        records = it
                        submitList(it)
                        dataAdapter.notifyDataSetChanged()

                        binding.history.historyFilterText.text = getString(R.string.remove_filter)
                    })
            }
        }

        dialog?.show()
    }

    private fun populateRecycler() {
        dataAdapter = MainAdapter(requireContext()) { record, view ->
            val bundle = Bundle()
            bundle.putParcelable(RECORD_DATA, record)
            view.findNavController().navigate(
                R.id.action_navigation_home_to_addDataActivity,
                bundle
            )
        }

        binding.history.historyRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = dataAdapter
        }
    }

    private fun getAllRecords() {
        viewModel.getAllRecords(isNewest, date, calendar.time)?.observe(viewLifecycleOwner, {
            records = it
            submitList(records)

            if (records.isNotEmpty()) {
                binding.history.historyRecycler.VISIBLE()
                binding.history.historyEmpty.GONE()
            } else {
                binding.history.historyRecycler.GONE()
                binding.history.historyEmpty.VISIBLE()
            }

            resetOrderIcon()
        })
    }

    private fun resetOrderIcon() {
        isNewest = true
        setOrderIcon()
    }

    private fun submitList(records: List<Record>) {
        val data = records.dropWhile { it.type == 1 }
        val mappedData = viewModel.mapData(data as ArrayList<Record>)
        dataAdapter.submitList(mappedData)
    }

    companion object {
        const val DEFAULT_WALLET = "def"
        const val RECORD_DATA = "data"
    }
}