package com.nurram.project.pencatatkeuangan.view.fragment.discount

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.FragmentDiscCalcBinding
import com.nurram.project.pencatatkeuangan.utils.CurrencyFormatter

class DiscCalcFragment : Fragment() {
    private lateinit var binding: FragmentDiscCalcBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiscCalcBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            discountCalculate.setOnClickListener {
                val amount = discountAmount.text.toString().toInt()
                val discount = discountValue.text.toString().toInt()
                val save = ((amount * discount) / 100).toLong()
                val result = (amount - save).toLong()

                discountResult.apply {
                    visibility = View.VISIBLE
                    text = "${getString(R.string.price_after_discount)} ${
                        CurrencyFormatter.convertAndFormat(result)
                    }"
                }

                discountSave.apply {
                    visibility = View.VISIBLE
                    text =
                        "${getString(R.string.you_save)} ${CurrencyFormatter.convertAndFormat(save)}"
                }
            }
        }
    }
}