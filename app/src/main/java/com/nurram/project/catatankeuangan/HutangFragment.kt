package com.nurram.project.catatankeuangan

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
import com.nurram.project.catatankeuangan.db.Hutang
import com.nurram.project.catatankeuangan.utils.HutangAdapter
import kotlinx.android.synthetic.main.add_dialog_layout.view.*
import kotlinx.android.synthetic.main.fragment_hutang.*
import java.text.SimpleDateFormat
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
        viewModel?.getAllHutang()?.observe(this, Observer {
            adapter?.setData(it?.toMutableList())
        })
    }

    private fun populateRecycler() {
        adapter = HutangAdapter(context!!, null) { it, it1 ->
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
        val dialog = context?.let { AlertDialog.Builder(it) }
        val dialogView = layoutInflater.inflate(R.layout.add_dialog_layout, null)

        dialogView.dialog_title.setText(hutang.judul)
        dialogView.dialog_amount.setText(hutang.jumlah.toString())
        dialogView.dialog_checkbox_income.visibility = View.GONE

        dialog?.setView(dialogView)
        dialog?.setCancelable(true)
        dialog?.setPositiveButton(R.string.dialog_simpan) { _, _ ->
            val date = SimpleDateFormat(getString(R.string.date_pattern))
            val innerHutang = Hutang(
                hutang.id, dialogView.dialog_title.text.toString(),
                dialogView.dialog_amount.text.toString().toInt(),
                date.format(Calendar.getInstance().time)
            )

            viewModel?.updateHutang(innerHutang)
        }

        dialog?.show()
    }
}