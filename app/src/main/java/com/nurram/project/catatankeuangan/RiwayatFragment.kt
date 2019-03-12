package com.nurram.project.catatankeuangan

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.nurram.project.catatankeuangan.db.Record
import com.nurram.project.catatankeuangan.utils.RiwayatAdapter
import kotlinx.android.synthetic.main.add_dialog_layout.view.*
import kotlinx.android.synthetic.main.fragment_riwayat.*
import java.text.SimpleDateFormat
import java.util.*


class RiwayatFragment : Fragment() {
    private var viewModel: MainViewModel? = null
    private var adapter: RiwayatAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_riwayat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProviders.of(it).get(MainViewModel::class.java) }
        populateRecycler()
        viewModel?.getAllRecords()?.observe(this, Observer {
            adapter?.setData(it?.toMutableList())
        })
    }

    private fun populateRecycler() {
        adapter = RiwayatAdapter(context!!, null, false) { it, it1 ->
            if (it1 == "delete") {
                (activity as MainActivity).reduceValue(it.keterangan, it.jumlah)

                viewModel?.deleteRecord(it)
                Toast.makeText(context, R.string.toast_hapus_berhasil, Toast.LENGTH_SHORT).show()
            } else {
                showAddDataDialog(it)
            }
        }

        riwayat_recycler.layoutManager = LinearLayoutManager(context)
        riwayat_recycler.setHasFixedSize(true)
        riwayat_recycler.adapter = adapter
    }

    private fun showAddDataDialog(record: Record) {
        val dialog = context?.let { AlertDialog.Builder(it) }
        val dialogView = layoutInflater.inflate(R.layout.add_dialog_layout, null)

        dialogView.dialog_judul.setText(record.judul)
        dialogView.dialog_uang.setText(record.jumlah.toString())
        dialogView.dialog_checkbox_masukan.isEnabled = false

        dialog?.setView(dialogView)
        dialog?.setCancelable(true)
        dialog?.setPositiveButton(R.string.dialog_simpan) { _, _ ->
            val date = SimpleDateFormat(getString(R.string.date_pattern))
            val innerRecord = Record(
                record.id, dialogView.dialog_judul.text.toString(),
                dialogView.dialog_uang.text.toString().toInt(),
                date.format(Calendar.getInstance().time),
                record.keterangan
            )

            viewModel?.updateRecord(innerRecord)
        }

        dialog?.show()
    }
}
