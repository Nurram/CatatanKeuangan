package com.nurram.project.pencatatkeuangan.view.fragment.debt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.nurram.project.pencatatkeuangan.databinding.ItemDateBinding
import com.nurram.project.pencatatkeuangan.databinding.ItemRowBinding
import com.nurram.project.pencatatkeuangan.db.Debt
import com.nurram.project.pencatatkeuangan.utils.CurrencyFormatter.convertAndFormat
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import java.util.*

class DebtAdapter(private val clickUtils: (Debt, String) -> Unit
) : ListAdapter<Debt, RecyclerView.ViewHolder>(DIFF_UTIL) {
    var date: Date? = null

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Debt>() {
            override fun areItemsTheSame(oldItem: Debt, newItem: Debt): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Debt, newItem: Debt): Boolean =
                oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position) != null) {
            return getItem(position).type
        }

        return 0
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(p0.context)
        val binding: ViewBinding

        if (p1 == 0) {
            binding = ItemRowBinding.inflate(inflater, p0, false)
            return MainHolder(binding)
        }

        binding = ItemDateBinding.inflate(inflater, p0, false)
        return DateHolder(binding)
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val data = getItem(p1)

        if (data != null) {
            if (p0.itemViewType == 0) {
                p0 as DebtAdapter.MainHolder
                p0.bind(data, clickUtils)
            } else {
                p0 as DebtAdapter.DateHolder
                p0.bind(data.date!!)
            }
        }
    }

    inner class MainHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var debt: Debt

        fun bind(debt: Debt, clickUtils: (Debt, String) -> Unit) {
            this.debt = debt

            binding.apply {
                itemTitle.text = debt.judul
                itemUang.text = convertAndFormat(debt.total.toLong())
                itemDelete.setOnClickListener { clickUtils(debt, "delete") }
                itemUpdate.setOnClickListener { clickUtils(debt, "edit") }
                itemView.setOnClickListener { clickUtils(debt, "edit") }
            }
        }
    }

    inner class DateHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: Date) {
            binding.itemDate.text = DateUtil.formatDate(date)
        }
    }
}