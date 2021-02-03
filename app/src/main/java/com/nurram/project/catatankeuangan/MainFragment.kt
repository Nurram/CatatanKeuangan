package com.nurram.project.catatankeuangan

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.Toast
import com.nurram.project.catatankeuangan.db.Hutang
import com.nurram.project.catatankeuangan.db.Record
import com.nurram.project.catatankeuangan.utils.CurencyFormatter
import com.nurram.project.catatankeuangan.utils.DateUtil
import com.nurram.project.catatankeuangan.utils.PagerAdapter
import kotlinx.android.synthetic.main.add_dialog_layout.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.saldo_dialog_layout.view.*
import java.util.*

class MainFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: PagerAdapter

    private var jumlahPengeluaran = 0
    private var jumlahPemasukan = 0
    private var jumlahHutang = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_main, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            jumlahPemasukan = savedInstanceState.getInt("jumlahPemasukan")
            jumlahPengeluaran = savedInstanceState.getInt("jumlahPengeluaran")
            jumlahHutang = savedInstanceState.getInt("jumlahHutang")
        }

        (activity as MainActivity?)?.setSupportActionBar(main_toolbar)
        (activity as MainActivity?)?.supportActionBar?.title = null

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getJumlahPengeluaran()?.observe(this, Observer {
            if (it != null) {
                main_jumlah_pengeluaran.text = CurencyFormatter.convertAndFormat(it)
                jumlahPengeluaran = it
            } else {
                main_jumlah_pengeluaran.text = CurencyFormatter.convertAndFormat(0)
            }
        })

        viewModel.getJumlahPemasukan()?.observe(this, Observer {
            if (it != null) {
                main_jumlah_pemasukan.text = CurencyFormatter.convertAndFormat(it)
                jumlahPemasukan = it
            } else {
                main_jumlah_pemasukan.text = CurencyFormatter.convertAndFormat(0)
            }
        })

        viewModel.getJumlahHutang()?.observe(this, Observer {
            if (it != null) {
                jumlahHutang = it
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("jumlahPemasukan", jumlahPemasukan)
        outState.putInt("jumlahPengeluaran", jumlahPengeluaran)
        outState.putInt("jumlahHutang", jumlahHutang)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reset -> showDialog()
            R.id.action_saldo -> showSaldoDialog()
            else -> {
                val intent = Intent(activity, GraphActivity::class.java)
                startActivity(intent)
            }
        }

        return true
    }

    private fun showSaldoDialog() {
        val dialog = context?.let { AlertDialog.Builder(it) }
        val dialogView = layoutInflater.inflate(R.layout.saldo_dialog_layout, null)

        dialogView.dialog_main_pemasukan.text = CurencyFormatter.convertAndFormat(jumlahPemasukan)
        dialogView.dialog_main_pengeluaran.text = CurencyFormatter.convertAndFormat(
            jumlahPengeluaran
        )
        dialogView.dialog_main_hutang.text = CurencyFormatter.convertAndFormat(jumlahHutang)
        dialogView.dialog_main_saldo.text =
            CurencyFormatter.convertAndFormat(jumlahPemasukan - (jumlahPengeluaran + jumlahHutang))
        dialog?.setView(dialogView)
        dialog?.setTitle(R.string.dialog_title_saldo)
        dialog?.setCancelable(true)
        dialog?.setPositiveButton("Close", null)
        dialog?.show()
    }

    private fun showDialog() {
        val dialog = context?.let { AlertDialog.Builder(it) }
        dialog?.setTitle(getString(R.string.perhatian))
        dialog?.setMessage(R.string.dialog_message)
        dialog?.setCancelable(true)
        dialog?.setPositiveButton("Yes") { _, _ ->
            viewModel.deleteAllRecord()
            viewModel.deleteAllHutang()

            jumlahPemasukan = 0
            jumlahPengeluaran = 0
            jumlahHutang = 0
        }
        dialog?.setNegativeButton("Cancel") { innerDialog, _ ->
            innerDialog.dismiss()
        }

        dialog?.show()
    }

    fun reduceValue(key: String, amount: Int) {

        when (key) {
            "pemasukan" -> jumlahPemasukan -= amount
            "pengeluaran" -> jumlahPengeluaran -= amount
            else -> jumlahHutang -= amount
        }
    }

    private fun showAddDataDialog(key: String) {
        val dialog = context?.let { AlertDialog.Builder(it) }
        val dialogView = layoutInflater.inflate(R.layout.add_dialog_layout, null)

        var selectedDate = "";
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        when (key) {
            "utang" -> dialogView.dialog_checkbox_income.visibility = View.GONE
        }

        dialog?.setView(dialogView)
        dialogView.dialog_show_date.setOnClickListener {
            DatePickerDialog(context, { _, year, monthOfYear, dayOfMonth ->
                val date = "$dayOfMonth $monthOfYear $year"
                dialogView.dialog_date.text = "Transaction date: ${DateUtil.formatDate(date)}"
                selectedDate = "$dayOfMonth $monthOfYear $year"
            }, year, month, day).show()
        }

        dialog?.setCancelable(true)
        dialog?.setPositiveButton(R.string.dialog_simpan) { innerDialog, _ ->
            val isPemasukan = if (dialogView.dialog_checkbox_income.isChecked) {
                "pemasukan"
            } else {
                "pengeluaran"
            }

            if (!dialogView.dialog_title.text.isBlank() && !dialogView.dialog_amount.text.isBlank()
                && !selectedDate.isBlank()) {

                val jumlahPemasukan = dialogView.dialog_amount.text.toString()

                if (key == "riwayat") {
                    val record = Record(
                        0,
                        dialogView.dialog_title.text.toString(),
                        jumlahPemasukan.toInt(),
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

                innerDialog.dismiss()
            } else {
                Toast.makeText(context, R.string.toast_isi_kolom, Toast.LENGTH_SHORT).show()
            }
        }

        dialog?.show()
    }
}