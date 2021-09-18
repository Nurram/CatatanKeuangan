package com.nurram.project.pencatatkeuangan.view.fragment.discount

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.FragmentDiscCalcBinding
import com.nurram.project.pencatatkeuangan.utils.CurrencyFormatter
import java.util.*

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

        val viewModel = ViewModelProvider(this).get(DiscCalcViewModel::class.java)
        var current = ""

        binding.apply {
            discountAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

                override fun afterTextChanged(s: Editable?) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val stringText = s.toString()

                    if(stringText != current) {
                        discountAmount.removeTextChangedListener(this)

                        val formatted = if(stringText.length > 2) {
                            val cleanString = CurrencyFormatter.getNumber(stringText)
                            CurrencyFormatter.convertAndFormat(cleanString)
                        } else {
                            "Rp"
                        }

                        current = formatted
                        discountAmount.setText(formatted)
                        discountAmount.setSelection(formatted.length)
                        discountAmount.addTextChangedListener(this)
                    }
                }
            })

            discountCalculate.setOnClickListener {
                if(discountAmount.text.length <= 2 || discountValue.text.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), R.string.toast_isi_kolom, Toast.LENGTH_SHORT).show()
                } else {
                    val amount = CurrencyFormatter.getNumber(discountAmount.text.toString())
                    val discount = discountValue.text.toString().toLong()

                    if (amount <= 1000000000) {
                        if (discount <= 100) {
                            val data = viewModel.calculateDiscount(amount, discount)
                            val priceAfterDiscount = data["priceAfterDiscount"]!!
                            val save = data["save"]!!

                            discountResult.apply {
                                visibility = View.VISIBLE
                                text = "${getString(R.string.price_after_discount)} ${
                                    CurrencyFormatter.convertAndFormat(priceAfterDiscount)
                                }"
                            }

                            discountSave.apply {
                                visibility = View.VISIBLE
                                text =
                                    "${getString(R.string.you_save)} ${
                                        CurrencyFormatter.convertAndFormat(
                                            save
                                        )
                                    }"
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.max_discount),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.max_amount),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}