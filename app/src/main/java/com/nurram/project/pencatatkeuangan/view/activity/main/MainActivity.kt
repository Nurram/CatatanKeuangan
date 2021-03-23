package com.nurram.project.pencatatkeuangan.view.activity.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainFragment
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainViewModel
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ActivityMainBinding
import com.nurram.project.pencatatkeuangan.databinding.SaldoDialogLayoutBinding
import com.nurram.project.pencatatkeuangan.utils.CurencyFormatter
import com.nurram.project.pencatatkeuangan.view.activity.graph.GraphActivity
import com.nurram.project.pencatatkeuangan.view.fragment.discount.DiscCalcFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private var totalExpenses = 0L
    private var totalIncome = 0L
    private var totalDebt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.title = null

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.mainToolbar,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = true
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, android.R.color.white)

        binding.navView.menu.getItem(0).isChecked = true
        binding.navView.setNavigationItemSelectedListener {
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

            binding.drawerLayout.closeDrawers()
            true
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragment, MainFragment()).commit()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.getTotalExpenses()?.observe(this, {
            if (it != null) {
                totalExpenses = it.toLong()
            }
        })

        viewModel.getTotalIncome()?.observe(this, {
            if (it != null) {
                totalIncome = it.toLong()
            }
        })

        viewModel.getTotalDebt()?.observe(this, {
            if (it != null) {
                totalDebt = it.toLong()
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
            R.id.action_saldo -> showBalanceDialog()
            else -> {
                val intent = Intent(this, GraphActivity::class.java)
                startActivity(intent)
            }
        }

        return true
    }

    private fun showBalanceDialog() {
        val dialog = AlertDialog.Builder(this)
        val dialogView = SaldoDialogLayoutBinding.inflate(layoutInflater)

        dialogView.apply {
            dialogMainIncome.text = CurencyFormatter.convertAndFormat(totalIncome)
            dialogMainExpenses.text = CurencyFormatter.convertAndFormat(
                totalExpenses
            )
            dialogMainDebt.text = CurencyFormatter.convertAndFormat(totalDebt)
            dialogMainSaldo.text =
                CurencyFormatter.convertAndFormat(totalIncome - (totalExpenses + totalDebt))
        }

        dialog.setView(dialogView.root)
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
            viewModel.deleteAllDebt()

            totalIncome = 0
            totalExpenses = 0
            totalDebt = 0
        }
        dialog.setNegativeButton("Cancel") { innerDialog, _ ->
            innerDialog.dismiss()
        }

        dialog.show()
    }

    fun reduceValue(key: String, amount: Long) {
        when (key) {
            "income" -> totalIncome -= amount
            "expenses" -> totalExpenses -= amount
            else -> totalDebt -= amount
        }
    }
}
