package com.nurram.project.catatankeuangan

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
import com.nurram.project.catatankeuangan.db.Record
import com.nurram.project.catatankeuangan.utils.DateUtil
import com.nurram.project.catatankeuangan.utils.RiwayatAdapter
import kotlinx.android.synthetic.main.add_dialog_layout.view.*
import kotlinx.android.synthetic.main.fragment_riwayat.*
import java.util.*


class RiwayatFragment : Fragment() {
    private var viewModel: MainViewModel? = null
    private var adapter: RiwayatAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_riwayat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProviders.of(it).get(MainViewModel::class.java) }
        populateRecycler()
        viewModel?.getAllRecords()?.observe(viewLifecycleOwner, Observer {
            adapter?.setData(it?.toMutableList())
        })
    }

    private fun populateRecycler() {
        adapter = context?.let {
            RiwayatAdapter(it, null, false) { it, it1 ->
                if (it1 == "delete") {
                    (parentFragment?.activity as MainActivity).reduceValue(it.keterangan, it.jumlah)

                    viewModel?.deleteRecord(it)
                    Toast.makeText(context, R.string.toast_hapus_berhasil, Toast.LENGTH_SHORT).show()
                } else {
                    showAddDataDialog(it)
                }
            }
        }

        riwayat_recycler.layoutManager =
            LinearLayoutManager(context)
        riwayat_recycler.setHasFixedSize(true)
        riwayat_recycler.adapter = adapter
    }

    private fun showAddDataDialog(record: Record) {
        val dialog = context?.let { AlertDialog.Builder(it) }
        val dialogView = layoutInflater.inflate(R.layout.add_dialog_layout, null)

        dialogView.dialog_title.setText(record.judul)
        dialogView.dialog_amount.setText(record.jumlah.toString())
        dialogView.dialog_date.text = "Transaction date: ${DateUtil.formatDate(record.tanggal)}"
        dialogView.dialog_checkbox_income.isEnabled = false

        var selectedDate = ""
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        dialogView.dialog_show_date.setOnClickListener {
            DatePickerDialog(context, { _, year, monthOfYear, dayOfMonth ->
                val date = "$dayOfMonth $monthOfYear $year"
                dialogView.dialog_date.text = "Transaction date: ${DateUtil.formatDate(date)}"
                selectedDate = "$dayOfMonth $monthOfYear $year"
            }, year, month, day).show()
        }

        dialog?.setView(dialogView)
        dialog?.setCancelable(true)
        dialog?.setPositiveButton(R.string.dialog_simpan) { _, _ ->
            val innerRecord = Record(
                record.id, dialogView.dialog_title.text.toString(),
                dialogView.dialog_amount.text.toString().toInt(),
                selectedDate,
                record.keterangan
            )

            viewModel?.updateRecord(innerRecord)
        }

        dialog?.show()
    }
}
