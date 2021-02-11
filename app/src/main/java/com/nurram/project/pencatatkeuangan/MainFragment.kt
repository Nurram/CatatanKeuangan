package com.nurram.project.pencatatkeuangan

import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.nurram.project.pencatatkeuangan.db.Hutang
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.utils.CurencyFormatter
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import com.nurram.project.pencatatkeuangan.utils.PagerAdapter
import kotlinx.android.synthetic.main.add_dialog_layout.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*

class MainFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: PagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getJumlahPengeluaran()?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                main_jumlah_pengeluaran.text = CurencyFormatter.convertAndFormat(it.toLong())
            } else {
                main_jumlah_pengeluaran.text = CurencyFormatter.convertAndFormat(0)
            }
        })

        viewModel.getJumlahPemasukan()?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                main_jumlah_pemasukan.text = CurencyFormatter.convertAndFormat(it.toLong())
            } else {
                main_jumlah_pemasukan.text = CurencyFormatter.convertAndFormat(0)
            }
        })

        adapter = PagerAdapter(childFragmentManager)
        main_view_pager.adapter = adapter
        main_view_pager.offscreenPageLimit = 3
        main_tab_layout.setupWithViewPager(main_view_pager)
        main_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                if (p0 == 1) {
                    riwayat_fab.hide()
                    hutang_fab.show()
                } else {
                    riwayat_fab.show()
                    hutang_fab.hide()
                }
            }

            override fun onPageSelected(p0: Int) {
                if (p0 == 0) {
                    riwayat_fab.hide()
                    hutang_fab.show()
                } else {
                    riwayat_fab.show()
                    hutang_fab.hide()
                }
            }
        })

        riwayat_fab.setOnClickListener { showAddDataDialog("riwayat") }
        hutang_fab.setOnClickListener { showAddDataDialog("utang") }
    }

    private fun showAddDataDialog(key: String) {
        val builder = context?.let { AlertDialog.Builder(it) }
        val dialogView = layoutInflater.inflate(R.layout.add_dialog_layout, null)

        var selectedDate = DateUtil.getCurrentDate()
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        when (key) {
            "utang" -> dialogView.dialog_checkbox_income.visibility = View.GONE
        }

        builder?.setView(dialogView)
        dialogView.dialog_show_date.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val date = "$dayOfMonth $monthOfYear $year"
                dialogView.dialog_date.text = "Transaction date: ${DateUtil.formatDate(date)}"
                selectedDate = "$dayOfMonth $monthOfYear $year"
            }, year, month, day).show()
        }

        builder?.setCancelable(true)
        builder?.setPositiveButton(R.string.dialog_simpan, null)
        val dialog = builder?.create()
        dialog?.show()
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            val isPemasukan = if (dialogView.dialog_checkbox_income.isChecked) {
                "pemasukan"
            } else {
                "pengeluaran"
            }

            if (dialogView.dialog_title.text.isNotBlank() && dialogView.dialog_amount.text.isNotBlank()
                && selectedDate.isNotBlank()
            ) {

                val jumlahPemasukan = dialogView.dialog_amount.text.toString()

                if (key == "riwayat") {
                    val record = Record(
                        0,
                        dialogView.dialog_title.text.toString(),
                        jumlahPemasukan.toLong(),
                        selectedDate,
                        isPemasukan
                    )

                    viewModel.insertRecord(record)
                } else {
                    val hutang = Hutang(
                        0,
                        dialogView.dialog_title.text.toString(),
                        jumlahPemasukan.toInt(),
                        selectedDate
                    )

                    viewModel.insertHutang(hutang)
                }

                dialog.dismiss()
            } else {
                Toast.makeText(context, R.string.toast_isi_kolom, Toast.LENGTH_SHORT).show()
            }
        }
    }
}