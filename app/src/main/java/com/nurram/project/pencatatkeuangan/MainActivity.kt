package com.nurram.project.pencatatkeuangan

import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import com.nurram.project.pencatatkeuangan.utils.CurencyFormatter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.main_toolbar
import kotlinx.android.synthetic.main.saldo_dialog_layout.view.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    private var jumlahPengeluaran = 0
    private var jumlahPemasukan = 0
    private var jumlahHutang = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)
        supportActionBar?.title = null

        val toggle = ActionBarDrawerToggle(this, drawer_layout, main_toolbar, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = true
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, android.R.color.white)

        nav_view.menu.getItem(0).isChecked = true
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    supportActionBar?.title = null
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, MainFragment()).commit()
                }
                R.id.nav_discount -> {
                    supportActionBar?.title = getString(R.string.discount)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, DiscCalcFragment()).commit()
                }
            }

            drawer_layout.closeDrawers()
            true
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragment, MainFragment()).commit()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getJumlahPengeluaran()?.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                jumlahPengeluaran = it
            }
        })

        viewModel.getJumlahPemasukan()?.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                jumlahPemasukan = it
            }
        })

        viewModel.getJumlahHutang()?.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                jumlahHutang = it
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reset -> showDialog()
            R.id.action_saldo -> showSaldoDialog()
            else -> {
                val intent = Intent(this, GraphActivity::class.java)
                startActivity(intent)
            }
        }

        return true
    }

    private fun showSaldoDialog() {
        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.saldo_dialog_layout, null)

        dialogView.dialog_main_pemasukan.text = CurencyFormatter.convertAndFormat(jumlahPemasukan)
        dialogView.dialog_main_pengeluaran.text = CurencyFormatter.convertAndFormat(
            jumlahPengeluaran
        )
        dialogView.dialog_main_hutang.text = CurencyFormatter.convertAndFormat(jumlahHutang)
        dialogView.dialog_main_saldo.text =
            CurencyFormatter.convertAndFormat(jumlahPemasukan - (jumlahPengeluaran + jumlahHutang))
        dialog.setView(dialogView)
        dialog.setTitle(R.string.dialog_title_saldo)
        dialog.setCancelable(true)
        dialog.setPositiveButton("Close", null)
        dialog.show()
    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.perhatian))
        dialog.setMessage(R.string.dialog_message)
        dialog.setCancelable(true)
        dialog.setPositiveButton("Yes") { _, _ ->
            viewModel.deleteAllRecord()
            viewModel.deleteAllHutang()

            jumlahPemasukan = 0
            jumlahPengeluaran = 0
            jumlahHutang = 0
        }
        dialog.setNegativeButton("Cancel") { innerDialog, _ ->
            innerDialog.dismiss()
        }

        dialog.show()
    }

    fun reduceValue(key: String, amount: Int) {
        when (key) {
            "pemasukan" -> jumlahPemasukan -= amount
            "pengeluaran" -> jumlahPengeluaran -= amount
            else -> jumlahHutang -= amount
        }
    }
}
