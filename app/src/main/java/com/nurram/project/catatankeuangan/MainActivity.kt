package com.nurram.project.catatankeuangan

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.nurram.project.catatankeuangan.db.Record
import com.nurram.project.catatankeuangan.utils.CurencyFormatter
import com.nurram.project.catatankeuangan.utils.MainAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_dialog_layout.view.*
import kotlinx.android.synthetic.main.saldo_dialog_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    private var jumlahPengeluaran = 0
    private var jumlahSaldo = 0
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)
        supportActionBar?.title = null

        if (savedInstanceState != null) {
            jumlahSaldo = savedInstanceState.getInt("jumlahSaldo")
            jumlahPengeluaran = savedInstanceState.getInt("jumlahPengeluaran")
        }

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        populateRecycler()
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
                jumlahSaldo = it
            } else {
                main_jumlah_pemasukan.text = CurencyFormatter.convertAndFormat(0)
            }
        })

        viewModel.getAllDatas()?.observe(this, Observer {
            it?.let { it1 -> adapter.setData(it1.toMutableList()) }
        })

        main_fab.setOnClickListener { showAddDataDialog() }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        outState?.putInt("jumlahSaldo", jumlahSaldo)
        outState?.putInt("jumlahPengeluaran", jumlahPengeluaran)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_reset) {
            viewModel.deleteAll()
        } else {
            showSaldoDialog()
        }

        return true
    }

    private fun populateRecycler() {
        adapter = MainAdapter(this, null) {
            viewModel.deleteData(it)
            jumlahSaldo = 0
            jumlahPengeluaran = 0

            Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
        }

        adapter.setHasStableIds(true)

        main_recycler.layoutManager = LinearLayoutManager(this)
        main_recycler.setHasFixedSize(true)
        main_recycler.adapter = adapter
    }

    private fun showAddDataDialog() {
        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.add_dialog_layout, null)

        dialog.setView(dialogView)
        dialog.setCancelable(true)
        dialog.setPositiveButton("Simpan") { innerDialog, _ ->
            val isPemasukan = if (dialogView.dialog_checkbox_masukan.isChecked) {
                "pemasukan"
            } else {
                "pengeluaran"
            }
            val jumlahPemasukan = dialogView.dialog_uang.text.toString()
            val date = SimpleDateFormat("dd-MM-yyyy HH:mm")
            val record = Record(
                0,
                dialogView.dialog_judul.text.toString(),
                jumlahPemasukan.toInt(),
                date.format(Calendar.getInstance().time),
                isPemasukan
            )

            viewModel.insertData(record)
            innerDialog.dismiss()
        }

        dialog.show()
    }

    private fun showSaldoDialog() {
        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.saldo_dialog_layout, null)

        dialogView.dialog_main_pemasukan.text = CurencyFormatter.convertAndFormat(jumlahSaldo)
        dialogView.dialog_main_pengeluaran.text = CurencyFormatter.convertAndFormat(jumlahPengeluaran)
        dialogView.dialog_main_saldo.text = CurencyFormatter.convertAndFormat(jumlahSaldo - jumlahPengeluaran)

        dialog.setView(dialogView)
        dialog.setTitle("Jumlah saldo kamu")
        dialog.setCancelable(true)
        dialog.setPositiveButton("Oke", null)
        dialog.show()
    }
}
