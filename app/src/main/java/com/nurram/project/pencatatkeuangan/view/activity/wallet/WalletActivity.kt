package com.nurram.project.pencatatkeuangan.view.activity.wallet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ActivityWalletBinding
import com.nurram.project.pencatatkeuangan.databinding.AddWalletDialogBinding
import com.nurram.project.pencatatkeuangan.db.Wallet
import com.nurram.project.pencatatkeuangan.utils.PrefUtil
import com.nurram.project.pencatatkeuangan.view.ViewModelFactory
import com.nurram.project.pencatatkeuangan.view.activity.main.MainActivity
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainFragment
import java.util.*

class WalletActivity : AppCompatActivity() {
    companion object {
        const val prefKey = "selectedWallet"
        const val prefDefault = "def"
    }

    private lateinit var viewModel: WalletViewModel
    private lateinit var pref: PrefUtil
    private lateinit var adapter: WalletAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.walletToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pref = PrefUtil(this)
        adapter = WalletAdapter { wallet, code ->
            when (code) {
                0 -> showDeleteDialog(wallet)
                1 -> showUpdateDataDialog(wallet)
                else -> {
                    pref.saveToPref(prefKey, wallet.id)
                    moveToMain()
                }
            }
        }

        binding.apply {
            walletRv.adapter = adapter
            walletRv.layoutManager = LinearLayoutManager(this@WalletActivity)
        }

        val pref = PrefUtil(this)
        val walletId = pref.getStringFromPref(prefKey, MainFragment.DEFAULT_WALLET)
        val factory = ViewModelFactory(application, walletId)
        viewModel = ViewModelProvider(this, factory).get(WalletViewModel::class.java)

        viewModel.getWallet()?.observe(this, { adapter.submitList(it) })

        MobileAds.initialize(this) { }
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    @SuppressLint("SetTextI18n")
    private fun showAddDataDialog() {
        val dialog = AlertDialog.Builder(this)
        val dialogView = AddWalletDialogBinding.inflate(layoutInflater)

        dialog.apply {
            setView(dialogView.root)
            setCancelable(true)
            setPositiveButton(R.string.dialog_save) { _, _ ->
                val title = dialogView.dialogTitle.text.toString()
                val wallet1 = Wallet(Calendar.getInstance().timeInMillis.toString(), title)

                if (title.isNotEmpty()) viewModel.insertWallet(wallet1)
            }
            show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showUpdateDataDialog(wallet: Wallet) {
        val dialog = AlertDialog.Builder(this)
        val dialogView = AddWalletDialogBinding.inflate(layoutInflater)
        dialogView.dialogTitle.setText(wallet.name)

        dialog.apply {
            setView(dialogView.root)
            setCancelable(true)
            setPositiveButton(R.string.dialog_save) { _, _ ->
                val title = dialogView.dialogTitle.text.toString()
                wallet.name = dialogView.dialogTitle.text.toString()
                if (title.isNotEmpty()) {
                    viewModel.updateWallet(wallet)
                    adapter.notifyDataSetChanged()
                }
            }
            show()
        }
    }

    private fun showDeleteDialog(wallet: Wallet) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.attention))
        dialog.setMessage(R.string.delete_wallet_confirmation)
        dialog.setCancelable(true)
        dialog.setPositiveButton("Yes") { _, _ ->
            viewModel.deleteWallet(wallet)
            pref.saveToPref(prefKey, prefDefault)
            Toast.makeText(this, R.string.data_success_delete, Toast.LENGTH_SHORT).show()

            moveToMain()
        }
        dialog.setNegativeButton("Cancel") { innerDialog, _ ->
            innerDialog.dismiss()
        }

        dialog.show()
    }

    private fun moveToMain() {
        val i = Intent(this, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
    }
}