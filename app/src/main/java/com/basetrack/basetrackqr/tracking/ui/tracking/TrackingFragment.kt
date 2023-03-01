package com.basetrack.basetrackqr.tracking.ui.tracking

import android.app.DatePickerDialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.collection.ArraySet
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.basetrack.basetrackqr.R
import com.basetrack.basetrackqr.databinding.TrackingBinding
import com.basetrack.basetrackqr.db.gsonModel.SaveModel
import com.basetrack.basetrackqr.tracking.ui.bottomSheet.BottomFragment
import com.basetrack.basetrackqr.tracking.ui.photo.GalleryFragment
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*


@AndroidEntryPoint
class TrackingFragment : Fragment() {

    private val viewModel: TrackingViewModel by viewModels()

    private lateinit var binding: TrackingBinding
    private var nakladImage: File? = null

    private val action = arrayListOf("REFUND", "DELIVERY", "INSTALLATION", "REPLENISHMENT")

    private val type = arrayListOf("Kit", "Charging", "Holder")

    var sPref: SharedPreferences? = null

    private val SAVED_TEXT = "saved_text"


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.tracking, container, false)

        initLayouts()

        observeParams()


        return binding.root
    }

    private fun observeParams() {
        GalleryFragment.setNaklad.observe(viewLifecycleOwner, Observer {
            if (it.exists()) {
                binding.nakladImage.visibility = View.VISIBLE
                nakladImage = it
                Glide.with(this).load(it).into(binding.nakladImage)
                binding.nakladButton.background =
                    requireContext().getDrawable(R.drawable.ic_baseline_replay_24)
                binding.replayPhotoText.text = requireContext().getString(R.string.replace_photo)
            } else {
                binding.replayPhotoText.text = requireContext().getString(R.string.take_photo)
            }

        })

        viewModel.companyText.observe(viewLifecycleOwner, Observer {
            binding.company.setText(it)
        })
        viewModel.date.observe(viewLifecycleOwner, Observer {
            binding.date.setText(it)
        })
        viewModel.transportText.observe(viewLifecycleOwner, Observer {
            binding.Transporter.setText(it)
        })
    }


    private fun initLayouts() {

        loadText()
        ////Company
        //TODO add from server
        //TODO val company = viewModel.getCompany()
        //
        val company = listOf("X5Retail", "BaseTrack")
        binding.qrListTracking.setOnClickListener {
            val dialog = BottomFragment()
            dialog.show(requireActivity().supportFragmentManager, "tag")
        }

        binding.nakladButton.setOnClickListener {
            saveText()
            findNavController().navigate(R.id.action_trackingFragmentToCapturePhotoFragment)
        }

        binding.addHistory.setOnClickListener {
            saveText()
            findNavController().navigate(R.id.action_trackingFragmentToQrResultFragment)
        }

        binding.scan.setOnClickListener {
            saveText()
            val bundle = Bundle()
            bundle.putBoolean("trackingScan", true)
            findNavController().navigate(R.id.action_tracking_to_scan_fragment, bundle)
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line, company
        )
        binding.company.setAdapter(adapter)
        binding.company.setOnClickListener {
            binding.company.showDropDown()
        }

        val actionAdapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line, action
        )
        binding.action.setAdapter(actionAdapter)
        binding.action.setOnClickListener {
            binding.action.showDropDown()
        }

        val typeAdapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line, type
        )
        binding.typeThing.setAdapter(typeAdapter)
        binding.typeThing.setOnClickListener {
            binding.typeThing.showDropDown()
        }

        binding.typeThing.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Текст меняется
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString() != "Kit")
                    binding.count.visibility = View.VISIBLE
                else
                    binding.count.visibility = View.GONE
            }

        })



        binding.date.setOnClickListener {
            val myCalendar = viewModel.getCalendar()
            DatePickerDialog(
                requireContext(), viewModel.data, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        binding.send.setOnClickListener {
            val transporter = binding.Transporter.text.toString()
            val companyText = binding.company.text.toString()
            val data = binding.date.text.toString()
            val imageNaklad = binding.nakladImage.drawable
            val actionText = binding.action.text.toString()
            val typeText = binding.typeThing.text.toString()
            val count = binding.count.text.toString()
            if (transporter.isEmpty() || transporter.length < 3) {
                Toast.makeText(requireContext(), requireContext().getString(R.string.carrier_field_empty), Toast.LENGTH_LONG)
                    .show()
            } else if (companyText.isEmpty() || companyText.length < 2) {
                Toast.makeText(requireContext(), requireContext().getString(R.string.company_field_empty), Toast.LENGTH_LONG)
                    .show()
            } else if (data.isEmpty() || data.length < 4) {
                Toast.makeText(requireContext(), requireContext().getString(R.string.date_field_empty), Toast.LENGTH_LONG).show()
            } else if (imageNaklad == null || nakladImage == null) {
                Toast.makeText(requireContext(), requireContext().getString(R.string.invoice_picture_empty), Toast.LENGTH_LONG)
                    .show()
            } else if (qrList.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.barcode_empty),
                    Toast.LENGTH_LONG
                ).show()
            } else if (actionText.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.workType_field_empty),
                    Toast.LENGTH_LONG
                ).show()
            } else if (typeText != "Kit") {
                if (count.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.count_empty),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                val saveModel = SaveModel(transporter, companyText, data, ArrayList())
                binding.progress.visibility = View.VISIBLE
                viewModel.sendSave(qrList, saveModel, nakladImage!!)
                //TODO call viewModel.saveInfo(transporter, companyText, actionText, data, )
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.tracking_ok),
                    Toast.LENGTH_LONG
                ).show()
                findNavController().navigate(R.id.action_tracking_to_home)
            }

        }
    }

    companion object {
        val qrList = ArraySet<String>()
    }


    ///Save element after transition to next fragment
    private fun saveText(){
        sPref = requireContext().getSharedPreferences(SAVED_TEXT, MODE_PRIVATE)
        val ed: SharedPreferences.Editor? = sPref?.edit()
        ed?.putString("transporter", binding.Transporter.text.toString())
        ed?.putString("company", binding.company.text.toString())
        ed?.putString("comment", binding.comment.text.toString())
        ed?.putString("action", binding.action.text.toString())
        ed?.putString("type", binding.typeThing.text.toString())
        ed?.putString("dateDelivery", binding.date.text.toString())
        ed?.putString("count", binding.count.text.toString())
        ed?.apply()
    }

    private fun loadText(){
        sPref = requireContext().getSharedPreferences(SAVED_TEXT, MODE_PRIVATE)
        binding.Transporter.setText(sPref?.getString("transporter", ""))
        binding.company.setText(sPref?.getString("company", ""))
        binding.comment.setText(sPref?.getString("comment", ""))
        binding.action.setText(sPref?.getString("action", ""))
        binding.typeThing.setText(sPref?.getString("type", ""))
        binding.date.setText(sPref?.getString("dateDelivery", ""))
        binding.count.setText(sPref?.getString("count", ""))

    }


}
