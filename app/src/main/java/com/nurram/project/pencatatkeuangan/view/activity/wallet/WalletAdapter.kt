package com.nurram.project.pencatatkeuangan.view.activity.wallet

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nurram.project.pencatatkeuangan.databinding.ItemWalletBinding
import com.nurram.project.pencatatkeuangan.db.Wallet
import com.nurram.project.pencatatkeuangan.utils.GONE

class WalletAdapter(
    private val onItemClick: (Wallet, Int) -> Unit
) : ListAdapter<Wallet, WalletAdapter.WalletHolder>(diff_callback) {

    companion object {
        private val diff_callback = object : DiffUtil.ItemCallback<Wallet>() {
            override fun areItemsTheSame(oldItem: Wallet, newItem: Wallet): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Wallet, newItem: Wallet): Boolean =
                oldItem == newItem

        }
    }

    inner class WalletHolder(val binding: ItemWalletBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(wallet: Wallet, position: Int) {
            binding.apply {

                if (wallet.id == "def") {
                    walletDelete.GONE()
                }

                walletNameItem.text = "${position + 1}. ${wallet.name}"
                walletDelete.setOnClickListener { onItemClick(wallet, 0) }
            }

            itemView.setOnClickListener { onItemClick(wallet, 1) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletHolder {
        val inflater = LayoutInflater.from(parent.context)
        return WalletHolder(ItemWalletBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: WalletHolder, position: Int) =
        holder.bind(getItem(position), position)
}