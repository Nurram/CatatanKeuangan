package com.nurram.project.pencatatkeuangan.view.fragment.report

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ItemDateBinding
import com.nurram.project.pencatatkeuangan.databinding.ItemRowBinding
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.utils.CurrencyFormatter.convertAndFormat
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import com.nurram.project.pencatatkeuangan.view.activity.add.AddDataActivity
import java.util.*

class ReportAdapter(
    private val context: Context,
    private val clickUtils: (Record, String) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var datas = arrayListOf<Record>()
    var date: Date? = null

    override fun getItemViewType(position: Int): Int {
        if (datas.isNullOrEmpty()) {
            return 0
        }

        return datas[position].type
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding: ViewBinding

        if (p1 == 0) {
            binding = ItemRowBinding.inflate(inflater, p0, false)
            return ReportHolder(binding)
        }

        binding = ItemDateBinding.inflate(inflater, p0, false)
        return DateHolder(binding)
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val data = datas[p1]

        if (p0.itemViewType == 0) {
            p0 as ReportHolder
            p0.bind(data, clickUtils)
        } else {
            p0 as DateHolder
            p0.bind(data.date!!)
        }
    }

    inner class ReportHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var record: Record

        fun bind(record: Record, clickUtils: (Record, String) -> Unit) {
            this.record = record

            binding.apply {
                itemTitle.text = record.judul
                itemDesc.text = record.note
                itemUang.text = convertAndFormat(record.total)

                when (record.description) {
                    AddDataActivity.INCOME -> {
                        itemUang.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                    }
                    AddDataActivity.EXPENSE -> {
                        itemUang.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                    }
                    else -> {
                        itemUang.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                    }
                }
            }
        }
    }

    inner class DateHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: Date) {
            binding.itemDate.text = DateUtil.formatDate(date)
        }
    }

    override fun getItemCount(): Int = datas.size

    fun setData(datas: List<Record>) {
        this.datas.clear()
        this.datas.addAll(datas)
        notifyDataSetChanged()
    }

    fun clearList() {
        datas.clear()
        notifyDataSetChanged()
    }
}