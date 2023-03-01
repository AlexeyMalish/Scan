
package com.basetrack.basetrackqr.tracking.ui.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.io.File
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.basetrack.basetrackqr.BuildConfig
import com.basetrack.basetrackqr.R
import com.basetrack.basetrackqr.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

val EXTENSION_WHITELIST = arrayOf("JPG")

@AndroidEntryPoint
class GalleryFragment internal constructor() : Fragment() {

    private var _fragmentGalleryBinding: FragmentGalleryBinding? = null

    private val fragmentGalleryBinding get() = _fragmentGalleryBinding!!

    private val args: GalleryFragmentArgs by navArgs()

    lateinit var mediaList: MutableList<File>

    inner class MediaPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int = mediaList.size
        override fun getItem(position: Int): Fragment = PhotoFragment.create(mediaList[position])
        override fun getItemPosition(obj: Any): Int = POSITION_NONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        val rootDirectory = File(args.rootDirectory)

        mediaList = rootDirectory.listFiles {filter ->
            EXTENSION_WHITELIST.contains(filter.extension.toUpperCase(Locale.ROOT))
        }?.sortedDescending()?.toMutableList() ?: mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentGalleryBinding = FragmentGalleryBinding.inflate(inflater, container, false)
        return fragmentGalleryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mediaList.isEmpty()) {
            fragmentGalleryBinding.deleteButton.isEnabled = false
            fragmentGalleryBinding.shareButton.isEnabled = false
        }

        fragmentGalleryBinding.photoViewPager.apply {
            offscreenPageLimit = 2
            adapter = MediaPagerAdapter(childFragmentManager)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            fragmentGalleryBinding.cutoutSafeArea.padWithDisplayCutout()
        }

        fragmentGalleryBinding.backButton.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigateUp()
        }

        fragmentGalleryBinding.shareButton.setOnClickListener {

            mediaList.getOrNull(fragmentGalleryBinding.photoViewPager.currentItem)?.let { mediaFile ->

                val intent = Intent().apply {
                    val mediaType = MimeTypeMap.getSingleton()
                            .getMimeTypeFromExtension(mediaFile.extension)
                    val uri = FileProvider.getUriForFile(
                            view.context, BuildConfig.APPLICATION_ID + ".provider", mediaFile)
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = mediaType
                    action = Intent.ACTION_SEND
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }

                startActivity(Intent.createChooser(intent, getString(R.string.share_hint)))
            }
        }

        fragmentGalleryBinding.Ok.setOnClickListener {
            _setNaklad.value = mediaList.getOrNull(fragmentGalleryBinding.photoViewPager.currentItem)
            findNavController().navigate(R.id.action_galleru_to_tracking)
        }

        fragmentGalleryBinding.deleteButton.setOnClickListener {

            mediaList.getOrNull(fragmentGalleryBinding.photoViewPager.currentItem)?.let { mediaFile ->

                AlertDialog.Builder(view.context, android.R.style.Theme_Material_Dialog)
                        .setTitle(getString(R.string.delete_title))
                        .setMessage(getString(R.string.delete_dialog))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes) { _, _ ->

                            mediaFile.delete()

                            MediaScannerConnection.scanFile(
                                    view.context, arrayOf(mediaFile.absolutePath), null, null)

                            mediaList.removeAt(fragmentGalleryBinding.photoViewPager.currentItem)
                            fragmentGalleryBinding.photoViewPager.adapter?.notifyDataSetChanged()

                            if (mediaList.isEmpty()) {
                                Navigation.findNavController(requireActivity(), R.id.fragment_container).navigateUp()
                            }

                        }

                        .setNegativeButton(android.R.string.no, null)
                        .create().showImmersive()
            }
        }
    }

    override fun onDestroyView() {
        _fragmentGalleryBinding = null
        super.onDestroyView()
    }

    companion object{
        private val _setNaklad = MutableLiveData<File>()
        val setNaklad : LiveData<File>
            get() = _setNaklad
    }
}
