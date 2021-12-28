package com.nurram.project.pencatatkeuangan.view.fragment.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ItemDateBinding
import com.nurram.project.pencatatkeuangan.databinding.ItemRowBinding
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.utils.CurrencyFormatter.convertAndFormat
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import com.nurram.project.pencatatkeuangan.utils.VISIBLE
import com.nurram.project.pencatatkeuangan.view.activity.add.AddDataActivity
import java.util.*

class MainAdapter(
    private val context: Context,
    private val clickUtils: (Record, View) -> Unit,
) : ListAdapter<Record, RecyclerView.ViewHolder>(DIFF_UTIL) {
    var date: Date? = null

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Record>() {
            override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean =
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
        val inflater = LayoutInflater.from(context)
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
                p0 as MainHolder
                p0.bind(data, clickUtils)
            } else {
                p0 as DateHolder
                p0.bind(data.date!!)
            }
        }
    }

    inner class MainHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var record: Record

        fun bind(record: Record, clickUtils: (Record, View) -> Unit) {
            this.record = record

            binding.apply {
                itemTitle.text = record.judul
                itemDesc.text = record.note
                itemView.setOnClickListener { clickUtils(record, binding.root) }

                if (record.note.isNotEmpty()) itemDesc.VISIBLE()

                when (record.description) {
                    AddDataActivity.INCOME -> {
                        itemUang.text =
                            context.getString(R.string.plus_value, convertAndFormat(record.total))
                        itemUang.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                    }
                    AddDataActivity.EXPENSE -> {
                        itemUang.text =
                            context.getString(R.string.minus_value, convertAndFormat(record.total))
                        itemUang.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                    }
                    else -> {
                        itemUang.text =
                            context.getString(R.string.minus_value, convertAndFormat(record.total))
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
}