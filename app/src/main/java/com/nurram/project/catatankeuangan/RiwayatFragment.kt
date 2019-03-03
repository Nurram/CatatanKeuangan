package com.nurram.project.catatankeuangan

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.nurram.project.catatankeuangan.utils.RiwayatAdapter
import kotlinx.android.synthetic.main.fragment_riwayat.*


class RiwayatFragment : Fragment() {
    private var viewModel: MainViewModel? = null
    private var adapter: RiwayatAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_riwayat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProviders.of(it).get(MainViewModel::class.java) }
        populateRecycler()
        viewModel?.getAllRecords()?.observe(this, Observer {
            adapter?.setData(it?.toMutableList())
        })
    }

    private fun populateRecycler() {
        adapter = RiwayatAdapter(context!!, null, false) {
            (activity as MainActivity).reduceValue(it.keterangan, it.jumlah)

            viewModel?.deleteRecord(it)
            Toast.makeText(context, R.string.toast_hapus_berhasil, Toast.LENGTH_SHORT).show()
        }

        riwayat_recycler.layoutManager = LinearLayoutManager(context)
        riwayat_recycler.setHasFixedSize(true)
        riwayat_recycler.adapter = adapter
    }
}
