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
import com.nurram.project.catatankeuangan.utils.HutangAdapter
import kotlinx.android.synthetic.main.fragment_hutang.*

class HutangFragment : Fragment() {
    private var adapter: HutangAdapter? = null
    private var viewModel: MainViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hutang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProviders.of(it).get(MainViewModel::class.java) }
        populateRecycler()
        viewModel?.getAllHutang()?.observe(this, Observer {
            adapter?.setData(it?.toMutableList())
        })
    }

    private fun populateRecycler() {
        adapter = HutangAdapter(context!!, null) {
            (activity as MainActivity).reduceValue("", it.jumlah)

            viewModel?.deleteHutang(it)
            Toast.makeText(context, R.string.toast_hapus_berhasil, Toast.LENGTH_SHORT).show()
        }

        hutang_recycler.layoutManager = LinearLayoutManager(context)
        hutang_recycler.setHasFixedSize(true)
        hutang_recycler.adapter = adapter
    }
}