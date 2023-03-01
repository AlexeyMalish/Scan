package com.basetrack.basetrackqr.qrScan.ui.qrResult

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.basetrack.basetrackqr.qrScan.ui.utils.adapter.QrResultViewAdapter
import com.basetrack.basetrackqr.R
import com.basetrack.basetrackqr.databinding.QrResultBinding
import com.basetrack.basetrackqr.tracking.ui.tracking.TrackingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrResultFragment : Fragment(), QrResultViewAdapter.OnItemClickListener{


    private lateinit var binding : QrResultBinding
    private val viewModel : QrResultViewModel by viewModels()

    private val valueToTracking  = HashMap<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.qr_result, container, false)

        viewModel.getAllQrResult()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = QrResultViewAdapter(viewModel.qrResult)
        adapter.setOnItemClickListener(this)
        binding.recyclerView.adapter = adapter

        binding.addToTrack.setOnClickListener {
           findNavController().navigate(R.id.action_history_to_tracking)
        }


        return binding.root
    }

    override fun onItemClick(key: String, value: String, delete: Boolean) {
        binding.addToTrack.visibility = View.VISIBLE
        if(delete){
            if(valueToTracking.size>0)
                 valueToTracking.remove(key)
            if(valueToTracking.size==0)
                binding.addToTrack.visibility = View.GONE
           TrackingFragment.qrList.remove(key)
        }
        else{
            valueToTracking[key] = value
            TrackingFragment.qrList.add(key)
        }
    }




}