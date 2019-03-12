package com.nurram.project.catatankeuangan.utils

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nurram.project.catatankeuangan.R
import com.nurram.project.catatankeuangan.db.Hutang
import com.nurram.project.catatankeuangan.utils.CurencyFormatter.convertAndFormat
import kotlinx.android.synthetic.main.item_row.view.*

class HutangAdapter(
    private val context: Context,
    private var datas: MutableList<Hutang>?,
    private val clickUtils: (Hutang, String) -> Unit
) : RecyclerView.Adapter<HutangAdapter.MainHolder>() {


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

    fun setData(hutang: MutableList<Hutang>?) {
        datas = hutang
        notifyDataSetChanged()
    }

    inner class MainHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var hutang: Hutang

        fun bind(hutang: Hutang, clickUtils: (Hutang, String) -> Unit) {
            this.hutang = hutang

            view.item_judul.text = this.hutang.judul
            view.item_uang.text = convertAndFormat(this.hutang.jumlah)
            view.item_tanggal.text = this.hutang.tanggal
            view.item_delete.setOnClickListener { clickUtils(hutang, "delete") }
            view.item_update.setOnClickListener { clickUtils(hutang, "edit") }
        }
    }
}