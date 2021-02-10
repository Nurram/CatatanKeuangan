package com.nurram.project.pencatatkeuangan

import android.app.DatePickerDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.nurram.project.pencatatkeuangan.db.Hutang
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import com.nurram.project.pencatatkeuangan.utils.HutangAdapter
import kotlinx.android.synthetic.main.add_dialog_layout.view.*
import kotlinx.android.synthetic.main.fragment_hutang.*
import java.util.*

class HutangFragment : Fragment() {
    private var adapter: HutangAdapter? = null
    private var viewModel: MainViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hutang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProviders.of(it).get(MainViewModel::class.java) }
        populateRecycler()
        viewModel?.getAllHutang()?.observe(viewLifecycleOwner, Observer {
            adapter?.setData(it?.toMutableList())
        })
    }

    private fun populateRecycler() {
        adapter = HutangAdapter(requireContext(), null) { it, it1 ->
            if (it1 == "delete") {
                (parentFragment?.activity as MainActivity).reduceValue("", it.jumlah)

                viewModel?.deleteHutang(it)
                Toast.makeText(context, R.string.toast_hapus_berhasil, Toast.LENGTH_SHORT).show()
            } else {
                showAddDataDialog(it)
            }
        }

        hutang_recycler.layoutManager =
            LinearLayoutManager(context)
        hutang_recycler.setHasFixedSize(true)
        hutang_recycler.adapter = adapter
    }

    private fun showAddDataDialog(hutang: Hutang) {
        val builder = context?.let { AlertDialog.Builder(it) }
        val dialogView = layoutInflater.inflate(R.layout.add_dialog_layout, null)

        dialogView.dialog_title.setText(hutang.judul)
        dialogView.dialog_amount.setText(hutang.jumlah.toString())
        dialogView.dialog_date.text = "Transaction date: ${DateUtil.formatDate(hutang.tanggal)}"
        dialogView.dialog_checkbox_income.visibility = View.GONE

        var selectedDate = hutang.tanggal
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        dialogView.dialog_show_date.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val date = "$dayOfMonth $monthOfYear $year"
                dialogView.dialog_date.text = "Transaction date: ${DateUtil.formatDate(date)}"
                selectedDate = "$dayOfMonth $monthOfYear $year"
            }, year, month, day).show()
        }

        builder?.setView(dialogView)
        builder?.setCancelable(true)
        builder?.setPositiveButton(R.string.dialog_simpan, null)

        val dialog = builder?.create()
        dialog?.show()
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            if (dialogView.dialog_title.text.isNotBlank() && dialogView.dialog_amount.text.isNotBlank()
                && selectedDate.isNotBlank()
            ) {
                val innerHutang = Hutang(
                    hutang.id, dialogView.dialog_title.text.toString(),
                    dialogView.dialog_amount.text.toString().toInt(),
                    selectedDate
                )

                viewModel?.updateHutang(innerHutang)
                dialog.dismiss()
            } else {
                Toast.makeText(context, R.string.toast_isi_kolom, Toast.LENGTH_SHORT).show()
            }
        }
    }
}