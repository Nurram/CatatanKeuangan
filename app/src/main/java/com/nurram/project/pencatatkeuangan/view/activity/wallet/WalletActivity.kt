package com.nurram.project.pencatatkeuangan.view.activity.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ActivityWalletBinding
import com.nurram.project.pencatatkeuangan.databinding.AddWalletDialogBinding
import com.nurram.project.pencatatkeuangan.db.Wallet
import com.nurram.project.pencatatkeuangan.utils.PrefUtil
import com.nurram.project.pencatatkeuangan.view.ViewModelFactory
import com.nurram.project.pencatatkeuangan.view.activity.main.MainActivity
import java.util.*

class WalletActivity : AppCompatActivity() {
    companion object {
        fun getIntent(context: Context) = Intent(context, WalletActivity::class.java)

        const val prefKey = "selectedWallet"
        const val prefDefault = "def"
    }

    private lateinit var binding: ActivityWalletBinding
    private lateinit var viewModel: WalletViewModel
    private lateinit var adapter: WalletAdapter
    private lateinit var pref: PrefUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.walletToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pref = PrefUtil(this)
        adapter = WalletAdapter { wallet, code ->
            when (code) {
                0 -> showDeleteDialog(wallet)
                else -> {
                    pref.saveToPref(prefKey, wallet.id)
                    moveToMain()
                }
            }
        }

        binding.apply {
            walletFab.setOnClickListener { showAddDataDialog() }
            walletRv.adapter = adapter
            walletRv.layoutManager = LinearLayoutManager(this@WalletActivity)
        }

        val pref = PrefUtil(this)
        val walletId = pref.getStringFromPref(WalletActivity.prefKey, "def")
        val factory = ViewModelFactory(application, walletId)
        viewModel = ViewModelProvider(this, factory).get(WalletViewModel::class.java)

        viewModel.getWallet()?.observe(this, { adapter.submitList(it) })
    }

    @SuppressLint("SetTextI18n")
    private fun showAddDataDialog() {
        val dialog = AlertDialog.Builder(this)
        val dialogView = AddWalletDialogBinding.inflate(layoutInflater)

        dialog.apply {
            setView(dialogView.root)
            setCancelable(true)
            setPositiveButton(R.string.dialog_save) { _, _ ->

                val wallet1 = Wallet(
                    Calendar.getInstance().timeInMillis.toString(),
                    dialogView.dialogTitle.text.toString()
                )

                viewModel.insertWallet(wallet1)
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