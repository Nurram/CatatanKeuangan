package com.nurram.project.pencatatkeuangan.view.activity.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ajts.androidmads.library.SQLiteToExcel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ActivityMainBinding
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.utils.*
import com.nurram.project.pencatatkeuangan.view.ViewModelFactory
import com.nurram.project.pencatatkeuangan.view.activity.add.AddDataActivity
import com.nurram.project.pencatatkeuangan.view.activity.wallet.WalletActivity
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainFragment
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDarkMode()

        binding = ActivityMainBinding.inflate(layoutInflater)

        val navigationFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navigationController = navigationFragment.navController
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_home,
            R.id.navigation_container_report,
            R.id.navigation_discount,
            R.id.navigation_setting
        ).build()

        val pref = PrefUtil(this)
        val walletId = pref.getStringFromPref(WalletActivity.prefKey, MainFragment.DEFAULT_WALLET)
        val factory = ViewModelFactory(application, walletId)
        viewModel = ViewModelProvider(this, factory)[MainActivityViewModel::class.java]

        //TODO: Delete on next update
        val debtsToRecords = arrayListOf<Record>()
        viewModel.getAllDebts()?.observe(this, {
            if (!it.isNullOrEmpty()) {
                it.forEach { debt ->
                    val record = Record(
                        id = 0,
                        judul = debt.judul,
                        total = debt.total.toLong(),
                        date = debt.date,
                        walletId = debt.walletId,
                        description = AddDataActivity.DEBT
                    )

                    debtsToRecords.add(record)
                }

                viewModel.moveDebtsToRecord(debtsToRecords)
            }
        })
        //TODO: Until here

        viewModel.getWalletById(walletId)?.observe(this, {
            binding.tvWallet.text = it.name
        })

        binding.apply {
            setContentView(root)
            setSupportActionBar(toolbar)
            supportActionBar?.title = null

            botNav.setupWithNavController(navigationController)
            tvWallet.setOnClickListener {
                val i = Intent(this@MainActivity, WalletActivity::class.java)
                startActivity(i)
            }
            fabAdd.setOnClickListener {
                val i = Intent(this@MainActivity, AddDataActivity::class.java)
                startActivity(i)
            }
        }

        setupActionBarWithNavController(navigationController, appBarConfiguration)

        MobileAds.initialize(this) { }
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        InterstitialAd.load(
            this,
            getString(R.string.INTERSTESIAL_ID),
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
                showConvertDialog()
            } else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG)
                    .show()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initDarkMode() {
        val sharedPref = PrefUtil(this)
        when (sharedPref.getIntFromPref("dark")) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun showConvertDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.attention))
            setMessage(getString(R.string.convert_confirmation))
            setCancelable(true)
            setPositiveButton("Yes") { _, _ -> convertDbToExcel() }
            setNegativeButton("Cancel") { dialog1, _ -> dialog1.dismiss() }
            show()
        }
    }

    private fun convertDbToExcel() {
        val directoryPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
        val excelConverter = SQLiteToExcel(this, "record_db", directoryPath)
        val tableList = arrayListOf("record_table", "debt_table", "wallet_table")

        excelConverter.setExcludeColumns(arrayListOf("type"))
        excelConverter.setPrettyNameMapping(
            hashMapOf("judul" to "title", "total" to "amount", "wallet_id" to "wallet id")
        )
        excelConverter.setCustomFormatter { columnName, value ->
            if (value != null) {
                when (columnName) {
                    "date" -> {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = value.toLong()

                        DateUtil.formatDate(calendar.time)
                    }
                    "total" -> {
                        CurrencyFormatter.convertAndFormat(value.toLong())
                    }
                    else -> {
                        value
                    }
                }
            } else {
                ""
            }
        }

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
                        getString(R.string.start_export),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onCompleted(filePath: String?) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd!!.show(this@MainActivity)
                    }

                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.data_exported),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun setTitle(title: String) {
        binding.tvTitle.apply {
            text = title
            this.VISIBLE()
        }
    }

    fun hideMenu() {
        binding.apply {
            tvWallet.GONE()
            tvWalletDesc.GONE()
        }
    }

    fun showMenu() {
        binding.apply {
            tvTitle.GONE()
            tvWallet.VISIBLE()
            tvWalletDesc.VISIBLE()
        }
    }
}
