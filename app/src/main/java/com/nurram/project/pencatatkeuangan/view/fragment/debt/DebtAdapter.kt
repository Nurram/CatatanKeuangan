package com.nurram.project.pencatatkeuangan.view.fragment.debt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.nurram.project.pencatatkeuangan.databinding.ItemDateBinding
import com.nurram.project.pencatatkeuangan.databinding.ItemRowBinding
import com.nurram.project.pencatatkeuangan.db.Debt
import com.nurram.project.pencatatkeuangan.utils.CurencyFormatter.convertAndFormat
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import java.util.*

class DebtAdapter(
    private var datas: MutableList<Debt>?,
    private val clickUtils: (Debt, String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var date: Date? = null
    override fun getItemViewType(position: Int): Int {
        if (datas != null) {
            return datas!![position].type
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

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        if (datas != null) {
            if (p0.itemViewType == 0) {
                p0 as DebtAdapter.MainHolder
                p0.bind(datas!![p1], clickUtils)
            } else {
                p0 as DebtAdapter.DateHolder
                p0.bind(datas!![p1].date!!)
            }
        }
    }

    fun setData(debt: MutableList<Debt>?) {
        if (debt == null || debt.isEmpty()) {
            datas?.clear()
        } else {
            var date = DateUtil.formatDate(debt[0].date!!)
            debt.add(0, Debt(type = 1, date = debt[0].date))

            var i = 0
            while (i <= debt.size - 1) {
                val formattedDate = DateUtil.formatDate(debt[i].date!!)

                if (date != formattedDate) {
                    date = formattedDate
                    debt.add(i, Debt(type = 1, date = debt[i].date))
                } else {
                    i++
                }
            }

            datas = debt
        }
        notifyDataSetChanged()
    }

    inner class MainHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var debt: Debt

        fun bind(debt: Debt, clickUtils: (Debt, String) -> Unit) {
            this.debt = debt

            binding.apply {
                itemTitle.text = debt.judul
                itemUang.text = convertAndFormat(debt.total.toLong())
//                itemDate.text = DateUtil.formatDate(debt.date!!)
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