package com.nurram.project.pencatatkeuangan.view.fragment.debt

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.nurram.project.pencatatkeuangan.databinding.ItemRowBinding
import com.nurram.project.pencatatkeuangan.db.Debt
import com.nurram.project.pencatatkeuangan.utils.CurencyFormatter.convertAndFormat
import com.nurram.project.pencatatkeuangan.utils.DateUtil

class DebtAdapter(
    private var datas: MutableList<Debt>?,
    private val clickUtils: (Debt, String) -> Unit
) : RecyclerView.Adapter<DebtAdapter.MainHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MainHolder {
        val inflater = LayoutInflater.from(p0.context)
        val binding = ItemRowBinding.inflate(inflater, p0, false)
        return MainHolder(binding)
    }

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onBindViewHolder(p0: MainHolder, p1: Int) {
        if (datas != null) {
            p0.bind(datas!![p1], clickUtils)
        }
    }

    fun setData(debt: MutableList<Debt>?) {
        datas = debt
        notifyDataSetChanged()
    }

    inner class MainHolder(private val binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var debt: Debt

        fun bind(debt: Debt, clickUtils: (Debt, String) -> Unit) {
            this.debt = debt

            binding.apply {
                itemJudul.text = debt.judul
                itemUang.text = convertAndFormat(debt.total.toLong())
                itemDate.text = DateUtil.formatDate(debt.date)
                itemDelete.setOnClickListener { clickUtils(debt, "delete") }
                itemUpdate.setOnClickListener { clickUtils(debt, "edit") }
            }
        }
    }
}