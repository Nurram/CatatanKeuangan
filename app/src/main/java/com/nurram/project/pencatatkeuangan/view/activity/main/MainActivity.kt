package com.nurram.project.pencatatkeuangan.view.activity.main

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ActivityMainBinding
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.utils.PrefUtil
import com.nurram.project.pencatatkeuangan.view.ViewModelFactory
import com.nurram.project.pencatatkeuangan.view.activity.add.AddDataActivity
import com.nurram.project.pencatatkeuangan.view.activity.wallet.WalletActivity
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val navigationFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navigationController = navigationFragment.navController
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_home,
            R.id.navigation_report,
            R.id.navigation_discount,
            R.id.navigation_setting
        ).build()

        val pref = PrefUtil(this)
        val walletId = pref.getStringFromPref(WalletActivity.prefKey, MainFragment.DEFAULT_WALLET)
        val factory = ViewModelFactory(application, walletId)
        val viewModel = ViewModelProvider(this, factory)[MainActivityViewModel::class.java]

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
            btnAdd.setOnClickListener {
                val i = Intent(this@MainActivity, AddDataActivity::class.java)
                startActivity(i)
            }
        }

        setupActionBarWithNavController(navigationController, appBarConfiguration)
    }
}
