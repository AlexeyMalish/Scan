package com.basetrack.basetrackqr.qrScan.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.basetrack.basetrackqr.R
import com.basetrack.basetrackqr.databinding.HomeFragmentBinding
import com.basetrack.basetrackqr.db.FireBaseCloudStorage
import com.basetrack.basetrackqr.tracking.ui.bottomSheet.BottomFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class HomeFragment : Fragment(){

    private lateinit var binding : HomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)

        initLayouts()


        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitProcess(-1)
                }
            })


        return binding.root
    }

    private fun initLayouts() {
        binding.history.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragmentToQrResultFragment)
        }

        binding.scan.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragmentToMainCameraFragment)
        }

        binding.tracking.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragmentToTrackingFragment)
        }

    }
}