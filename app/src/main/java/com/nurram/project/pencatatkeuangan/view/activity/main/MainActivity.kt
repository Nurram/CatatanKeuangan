package com.nurram.project.pencatatkeuangan.view.activity.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ajts.androidmads.library.SQLiteToExcel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ActivityMainBinding
import com.nurram.project.pencatatkeuangan.utils.CurrencyFormatter
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import com.nurram.project.pencatatkeuangan.utils.PrefUtil
import com.nurram.project.pencatatkeuangan.utils.VISIBLE
import com.nurram.project.pencatatkeuangan.view.ViewModelFactory
import com.nurram.project.pencatatkeuangan.view.activity.dark.DarkOptionsActivity
import com.nurram.project.pencatatkeuangan.view.activity.wallet.WalletActivity
import com.nurram.project.pencatatkeuangan.view.fragment.discount.DiscCalcFragment
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainFragment
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainViewModel
import com.nurram.project.pencatatkeuangan.view.fragment.report.ReportFragment
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        initDarkMode()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)

        val pref = PrefUtil(this)
        val walletId = pref.getStringFromPref(WalletActivity.prefKey, "def")
        val factory = ViewModelFactory(application, walletId)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        initDrawer()
        initHeaderUi(walletId)

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
                showConvertDialog()
            } else {
                Toast.makeText(this, getString(R.string.konversi_excel_ditolak), Toast.LENGTH_LONG)
                    .show()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initDrawer() {
        supportFragmentManager.beginTransaction().replace(R.id.fragment, MainFragment()).commit()

        ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.mainToolbar,
            R.string.open,
            R.string.close
        ).apply {
            binding.drawerLayout.addDrawerListener(this)
            syncState()
            isDrawerIndicatorEnabled = true
            drawerArrowDrawable.color = ContextCompat.getColor(this@MainActivity, android.R.color.white)

        }

        binding.apply {
            navView.menu.getItem(0).isChecked = true
            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_home -> changeFragment(null, MainFragment())
                    R.id.nav_report -> changeFragment(getString(R.string.report), ReportFragment())
                    R.id.nav_discount -> changeFragment(getString(R.string.discount), DiscCalcFragment())
                    R.id.sql_to_xls -> ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                    )
                    R.id.dark_mode -> startActivity(DarkOptionsActivity.getIntent(this@MainActivity))
                    R.id.reset -> showDeleteDialog()
                }

                drawerLayout.closeDrawers()
                true
            }
        }
    }

    private fun changeFragment(title: String?, fragment: Fragment) {
        supportActionBar?.title = title
        supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment).commit()
    }

    private fun initHeaderUi(walletId: String) {
        val header = binding.navView.getHeaderView(0)
        val headerWalletName = header.findViewById<TextView>(R.id.wallet_name)
        val headerButton = header.findViewById<Button>(R.id.wallet_change)
        headerButton.setOnClickListener {
            val i = WalletActivity.getIntent(this)
            startActivity(i)
        }

        viewModel.getWalletById(walletId)?.observe(this) {
            headerWalletName.text = it.name
        }
    }

    private fun initDarkMode() {
        val sharedPref = PrefUtil(this)
        when (sharedPref.getIntFromPref("dark")) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.attention))
            setMessage(R.string.delete_all)
            setCancelable(true)
            setPositiveButton("Yes") { _, _ ->
                viewModel.deleteAllRecord()
                viewModel.deleteAllDebt()
            }
            setNegativeButton("Cancel") { innerDialog, _ ->
                innerDialog.dismiss()
            }
            show()
        }
    }

    private fun showConvertDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.attention))
            setMessage(getString(R.string.lakukan_konversi))
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
                }
            })
    }
}
