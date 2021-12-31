package com.nurram.project.pencatatkeuangan.view.fragment.setting

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.nurram.project.pencatatkeuangan.R
import com.nurram.project.pencatatkeuangan.databinding.FragmentSettingBinding
import com.nurram.project.pencatatkeuangan.utils.PrefUtil
import com.nurram.project.pencatatkeuangan.view.ViewModelFactory
import com.nurram.project.pencatatkeuangan.view.activity.main.MainActivity
import com.nurram.project.pencatatkeuangan.view.activity.wallet.WalletActivity
import com.nurram.project.pencatatkeuangan.view.fragment.main.MainFragment

class SettingFragment : Fragment() {
    private lateinit var viewModel: SettingViewModel

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as MainActivity
        activity.hideMenu()
        activity.setTitle(getString(R.string.settings))

        val pref = PrefUtil(requireContext())
        val walletId = pref.getStringFromPref(WalletActivity.prefKey, MainFragment.DEFAULT_WALLET)
        val factory = ViewModelFactory(requireActivity().application, walletId)
        viewModel = ViewModelProvider(this, factory)[SettingViewModel::class.java]

        binding.apply {
            llDarkMode.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_navigation_setting_to_darkOptionsActivity)
            }
            llConvertExcel.setOnClickListener {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
            llDeleteAll.setOnClickListener { showDeleteDialog() }
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.attention))
            setMessage(R.string.delete_all)
            setCancelable(true)
            setPositiveButton("Yes") { _, _ ->
                viewModel.deleteAllRecords()
            }
            setNegativeButton("Cancel") { innerDialog, _ ->
                innerDialog.dismiss()
            }
            show()
        }
    }
}