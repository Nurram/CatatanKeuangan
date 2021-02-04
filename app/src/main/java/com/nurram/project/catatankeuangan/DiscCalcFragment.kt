package com.nurram.project.catatankeuangan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nurram.project.catatankeuangan.utils.CurencyFormatter
import kotlinx.android.synthetic.main.fragment_disc_calc.*

class DiscCalcFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_disc_calc, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        discount_calculate.setOnClickListener {
            val amount = discount_amount.text.toString().toInt()
            val discount = discount_value.text.toString().toInt()
            val save = (amount*discount)/100
            val result = amount - save

            discount_result.visibility = View.VISIBLE
            discount_result.text = "${getString(R.string.price_after_discount)} ${CurencyFormatter.convertAndFormat(result)}"

            discount_save.visibility = View.VISIBLE
            discount_save.text = "${getString(R.string.you_save)} ${CurencyFormatter.convertAndFormat(save)}"
        }
    }
}