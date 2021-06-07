package com.nurram.project.pencatatkeuangan.view.activity.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ajts.androidmads.library.SQLiteToExcel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ActivityMainBinding
import com.nurram.project.pencatatkeuangan.databinding.SaldoDialogLayoutBinding
import com.nurram.project.pencatatkeuangan.utils.CurencyFormatter
import com.nurram.project.pencatatkeuangan.utils.PrefUtil
import com.nurram.project.pencatatkeuangan.view.activity.dark.DarkOptionsActivity
import com.nurram.project.pencatatkeuangan.view.activity.graph.GraphActivity
import com.nurram.project.pencatatkeuangan.view.fragment.discount.DiscCalcFragment
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainFragment
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainViewModel
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var excelConverter: SQLiteToExcel

    private val directoryPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
    private val tableList = arrayListOf("record_table", "debt_table")
    private var permissionGranted = false
    private var alreadyConverted = false

    private var totalExpenses = 0L
    private var totalIncome = 0L
    private var totalDebt = 0L

    private var mInterstitialAd: InterstitialAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        initDarkMode()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.title = null

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )

        excelConverter = SQLiteToExcel(this, "record_db", directoryPath)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.mainToolbar,
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
                R.id.sql_to_xls -> {
                    showConvertDialog()
                }
                R.id.dark_mode -> {
                    startActivity(DarkOptionsActivity.getIntent(this))
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

        MobileAds.initialize(this) { }
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        InterstitialAd.load(
            this,
            "ca-app-pub-2987316684275669/4833789355",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mInterstitialAd = null
                }
            })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true
            } else {
                Toast.makeText(this, getString(R.string.konversi_excel_ditolak), Toast.LENGTH_LONG)
                    .show()
            }
        }
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

    private fun initDarkMode() {
        val sharedPref = PrefUtil(this)
        when (sharedPref.getFromPref("dark")) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
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
        dialog.setMessage(R.string.delete_all)
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

    private fun showConvertDialog() {
        val dialog = AlertDialog.Builder(this)

        dialog.setTitle(getString(R.string.perhatian))
        dialog.setMessage(getString(R.string.lakukan_konversi))
        dialog.setCancelable(true)
        dialog.setPositiveButton("Yes") { _, _ ->
            if (permissionGranted) {
                if (alreadyConverted) {
                    Toast.makeText(
                        this,
                        getString(R.string.data_sudah_pernah_dikonversi),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    convertDbToExcel()
                }
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.konversi_tidak_diijinkan),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        dialog.setNegativeButton("Cancel") { dialog1, _ ->
            dialog1.dismiss()
        }

        dialog.show()
    }

    private fun convertDbToExcel() {
        excelConverter.exportSpecificTables(
            tableList,
            "Catatan keuangan.xls",
            object : SQLiteToExcel.ExportListener {
                override fun onError(e: Exception?) {
                    Toast.makeText(this@MainActivity, e?.message, Toast.LENGTH_SHORT).show()
                }

                override fun onStart() {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.mulai_konversi),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onCompleted(filePath: String?) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd!!.show(this@MainActivity)
                    }

                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.selesai_konversi),
                        Toast.LENGTH_SHORT
                    ).show()
                    alreadyConverted = true
                }
            })
    }
}
