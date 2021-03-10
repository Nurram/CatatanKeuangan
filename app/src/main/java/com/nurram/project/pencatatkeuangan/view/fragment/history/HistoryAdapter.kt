package com.nurram.project.pencatatkeuangan.view.fragment.history

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.ItemRowBinding
import com.nurram.project.pencatatkeuangan.db.Record
import com.nurram.project.pencatatkeuangan.utils.CurencyFormatter.convertAndFormat
import com.nurram.project.pencatatkeuangan.utils.DateUtil

class HistoryAdapter(
    private val context: Context,
    private var datas: MutableList<Record>?,
    private val fromGraph: Boolean,
    private val clickUtils: (Record, String) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.MainHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MainHolder {
        val inflater = LayoutInflater.from(context)
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

    fun setData(records: MutableList<Record>?) {
        if (records == null) {
            datas?.clear()
        } else {
            datas = records
        }

        notifyDataSetChanged()
    }

    inner class MainHolder(private val binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var record: Record

        fun bind(record: Record, clickUtils: (Record, String) -> Unit) {
            this.record = record
            Log.d("TAG", "Adapter")
            binding.apply {
                itemJudul.text = record.judul
                itemUang.text = convertAndFormat(record.total.toLong())
                itemDate.text = DateUtil.formatDate(record.date)
                itemDelete.setOnClickListener { clickUtils(record, "delete") }
                itemUpdate.setOnClickListener { clickUtils(record, "edit") }

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
}