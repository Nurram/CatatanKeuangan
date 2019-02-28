package com.nurram.project.catatankeuangan.utils

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nurram.project.catatankeuangan.R
import com.nurram.project.catatankeuangan.db.Record
import com.nurram.project.catatankeuangan.utils.CurencyFormatter.convertAndFormat
import kotlinx.android.synthetic.main.item_row.view.*

class MainAdapter(
    private val context: Context,
    private var datas: MutableList<Record>?,
    private val clickUtils: (Record) -> Unit
) : RecyclerView.Adapter<MainAdapter.MainHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MainHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_row, p0, false)
        return MainHolder(view)
    }

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onBindViewHolder(p0: MainHolder, p1: Int) {
        if (datas != null) {
            p0.bind(datas!![p1], clickUtils)
        }
    }

    fun setData(records: MutableList<Record>) {
        datas = records
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        datas?.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class MainHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var record: Record

        fun bind(record: Record, clickUtils: (Record) -> Unit) {
            this.record = record

            view.item_judul.text = record.judul
            view.item_uang.text = convertAndFormat(record.jumlah)
            view.item_tanggal.text = record.tanggal
            view.item_delete.setOnClickListener { clickUtils(record) }

            if (record.keterangan == "pemasukan") {
                view.item_color.setBackgroundColor(context.resources.getColor(R.color.colorAccent))
                view.item_uang.setTextColor(context.resources.getColor(R.color.colorAccent))
            } else {
                view.item_color.setBackgroundColor(Color.RED)
            }
        }
    }
}