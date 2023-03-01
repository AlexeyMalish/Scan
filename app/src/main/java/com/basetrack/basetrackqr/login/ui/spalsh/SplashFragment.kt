package com.basetrack.basetrackqr.login.ui.spalsh

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.basetrack.basetrackqr.R
import com.basetrack.basetrackqr.databinding.SplashBinding
import com.basetrack.basetrackqr.db.QrResult
import com.basetrack.basetrackqr.db.model.User
import com.basetrack.basetrackqr.login.utils.Constants.Companion.SAVE_TAG
import dagger.hilt.android.AndroidEntryPoint
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private lateinit var binding : SplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.splash,
            container,
            false
        )

        val sharedPref: SharedPreferences =   requireContext().getSharedPreferences(SAVE_TAG, Context.MODE_PRIVATE)
        val isRegistrationUser =  sharedPref.getString("Authorization", "")
        isRegistrationUser?.let { Log.e("Authorization", it) }
        if(isRegistrationUser == "Authorization"){
            findNavController().navigate(R.id.action_splashFragmentToHomeFragment)
        }
        else{
            findNavController().navigate(
                //TODO R.id.action_splashFragmentToLoginFragment
                R.id.action_splashFragmentToHomeFragment
            )
        }

        return binding.root
    }

}