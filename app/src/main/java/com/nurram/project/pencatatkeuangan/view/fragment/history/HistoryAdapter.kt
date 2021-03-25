package com.nurram.project.pencatatkeuangan.view.fragment.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ItemDateBinding
import com.nurram.project.pencatatkeuangan.databinding.ItemRowBinding
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.utils.CurencyFormatter.convertAndFormat
import com.nurram.project.pencatatkeuangan.utils.DateUtil
import java.util.*

class HistoryAdapter(
    private val context: Context,
    private var datas: MutableList<Record>?,
    private val fromGraph: Boolean,
    private val clickUtils: (Record, String) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var date: Date? = null
    override fun getItemViewType(position: Int): Int {
        if(datas != null) {
            return datas!![position].type
        }

        return 0
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding: ViewBinding

        if(p1 == 0) {
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
        if(datas != null) {
            if(p0.itemViewType == 0) {
                p0 as MainHolder
                p0.bind(datas!![p1], clickUtils)
            } else {
                p0 as DateHolder
                p0.bind(datas!![p1].date!!)
            }
        }
    }

    fun setData(records: MutableList<Record>?) {
        if (records == null || records.isEmpty()) {
            datas?.clear()
        } else {
            var date = DateUtil.formatDate(records[0].date!!)
            records.add(0, Record(type = 1, date = records[0].date))

            var i = 0
            while (i <= records.size - 1) {
                val formattedDate = DateUtil.formatDate(records[i].date!!)

                if(date != formattedDate) {
                    date = formattedDate
                    records.add(i, Record(type = 1, date = records[i].date))
                } else {
                    i++
                }
            }

            datas = records
        }

        notifyDataSetChanged()
    }

    inner class MainHolder(private val binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var record: Record

        fun bind(record: Record, clickUtils: (Record, String) -> Unit) {
            this.record = record

            binding.apply {
                itemTitle.text = record.judul
                itemUang.text = convertAndFormat(record.total.toLong())
                itemDelete.setOnClickListener { clickUtils(record, "delete") }
                itemUpdate.setOnClickListener { clickUtils(record, "edit") }
                itemView.setOnClickListener { clickUtils(record, "edit") }

                if (!fromGraph) {
                    itemDelete.setOnClickListener { clickUtils(record, "delete") }
                    itemUpdate.setOnClickListener { clickUtils(record, "edit") }
                } else {
                    itemDelete.visibility = View.GONE
                    itemUpdate.visibility = View.GONE
                }

                if (record.description == "income") {
                    itemColor.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorAccent
                        )
                    )
                    itemUang.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                } else {
                    itemColor.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorRed
                        )
                    )
                    itemUang.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                }
            }
        }
    }

    inner class DateHolder(private val binding: ItemDateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(date: Date) {
            binding.itemDate.text = DateUtil.formatDate(date)
        }
    }
}