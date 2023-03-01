package com.basetrack.basetrackqr.qrScan.ui.mainCamera

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.basetrack.basetrackqr.R
import com.basetrack.basetrackqr.databinding.CameraFragmentBinding
import com.basetrack.basetrackqr.qrScan.qrUtils.QRReaderListener
import com.basetrack.basetrackqr.qrScan.ui.customView.QRReaderFragment
import com.basetrack.basetrackqr.tracking.ui.tracking.TrackingFragment
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainCameraFragment : Fragment() {

    private val viewModel : MainCameraViewModel by viewModels()


    private lateinit var binding : CameraFragmentBinding

    private lateinit var qrCodeReader: QRReaderFragment

    private var firstQr = ""
    private var secondQr = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.camera_fragment, container, false)
        binding.animationComplete.visibility = View.INVISIBLE



        observerParams()

        initLayouts()
        return binding.root
    }

    private fun observerParams() {
        viewModel.qrTextViewText.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.qrTextView.text = it
        })

        viewModel.qrTextViewVisibility.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.qrTextView.visibility = it
        })

        viewModel.scanVisibility.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.send.visibility = it
        })

        viewModel.scanText.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.send.text = requireContext().getString(it)
        })

        viewModel.animationVisibility.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.animationComplete.visibility = it
        })

        viewModel.animationPlay.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it)
                binding.animationComplete.playAnimation()
            else
                binding.animationComplete.cancelAnimation()
        })
    }

    private fun initLayouts() {
        qrCodeReader =
            childFragmentManager.findFragmentById(R.id.qrCodeReaderFragment) as QRReaderFragment

        qrCodeReader.setListener(object : QRReaderListener {

            override fun onRead(barcode: Barcode, barcodes: List<Barcode>) {
                barcode.displayValue?.let {
                    binding.qrTextView.text = it
                    defineText(it)
                }

            }

            override fun onError(exception: Exception) {
                binding.qrTextView.text = exception.toString()
            }
        })

        PermissionUtils.permission(PermissionConstants.CAMERA)
            .callback(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
                    qrCodeReader.startCamera(this@MainCameraFragment, viewModel.getConfig())
                }

                override fun onDenied() {
                    Log.e(
                        "MainCameraFragment",
                        "The camera permission must be granted in order to use this app"
                    )
                }
            }).request()


        binding.torchButton.setOnClickListener {
            qrCodeReader.enableTorch(!qrCodeReader.isTorchOn())
            if (qrCodeReader.isTorchOn())
                binding.torchButton.setBackgroundResource(R.drawable.ic_baseline_flash_off_24)
            else
                binding.torchButton.setBackgroundResource(R.drawable.ic_baseline_flash_on_24)
        }

        binding.switchCamera.setOnClickListener {
            qrCodeReader.startCamera(this@MainCameraFragment, viewModel.switchCameraClick())

        }

        binding.qrHistory.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragmentToResultFragment)
        }
    }

    fun defineText(text: String) {
        if(arguments==null||arguments?.getBoolean("trackingScan") != true) {
            binding.end.visibility = View.INVISIBLE
            binding.qrHistory.visibility = View.VISIBLE
                if (viewModel.isFirstQr()) {
                    viewModel.firstQrText()
                    binding.send.setOnClickListener {
                        viewModel.firstQrClick()
                        firstQr = text
                    }
                } else {
                    binding.send.setOnClickListener {
                        viewModel.secondQrClick()
                        secondQr = text
                        if (secondQr.isEmpty() || firstQr.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "Отсконируйте Qr коды сначала!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                          //TODO delete fireBase?  viewModel.saveDataInServer(firstQr, secondQr)
                            viewModel.saveDataInLocal(firstQr, secondQr)
                            firstQr = ""
                            secondQr = ""

                        }
                    }
                }
        }
        else{
            if(arguments?.getBoolean("trackingScan") == true){
                viewModel.trackingScanQrClick()
                binding.end.visibility = View.VISIBLE
                binding.qrHistory.visibility = View.INVISIBLE
                binding.send.setOnClickListener {
                    TrackingFragment.qrList.add(text)
                    Toast.makeText(requireContext(), "Сохранено!", Toast.LENGTH_SHORT).show()
                }
                binding.end.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        viewModel.realmClose()
    }


}
