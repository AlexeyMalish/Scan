package com.basetrack.basetrackqr.login.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.basetrack.basetrackqr.R
import com.basetrack.basetrackqr.databinding.LoginBinding
import com.basetrack.basetrackqr.db.FireBaseCloudStorage
import com.basetrack.basetrackqr.login.network.data.requestData.Resource
import com.basetrack.basetrackqr.login.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class LoginFragment() : Fragment(){

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var binding: LoginBinding

    @Inject
    lateinit var utility : Utility

    lateinit var sharedPref: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.login,
            container,
            false
        )

        sharedPref =  requireContext().getSharedPreferences(com.basetrack.basetrackqr.login.utils.Constants.Companion.SAVE_TAG, android.content.Context.MODE_PRIVATE)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitProcess(-1)
                }
            })

        initLayouts()
        observeParams()
        clearProgressBar()
        return binding.root
    }

    private fun loginUser(){
        val login = binding.etLogin.text.toString().replace(" ", "")
        val password = binding.etPassword.text.toString().replace(" ", "")
        val validate = loginViewModel.validDate(login, password)
        if( validate == 0) {
            loginViewModel.loginUser(login, password)
        }
        else {
            utility.showSnack(requireContext().getString(validate), binding.root, "Ok")
        }
    }

    private fun initLayouts() {
        binding.blogin.setOnClickListener {
            loginUser()
        }

    }

    private fun observeParams() {
        loginViewModel.loginResponse.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Success -> {
                        Log.e("===>", response.value.toString())

                        save("Authorization", "Authorization")
                        save("Login", response.value.user.email)
                        findNavController().navigate(R.id.action_loginFragmentToHomeFragment)
                    }

                    is Resource.Error -> {
                        clearProgressBar()
                        utility.showSnack(requireContext().getString(R.string.wrong_login), binding.root, "Ok")

                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                    else -> {}
                }
            }
        })
    }

    private fun showProgressBar() {
        binding.blogin.visibility = View.INVISIBLE
        binding.progress.visibility = View.VISIBLE
    }

    private fun clearProgressBar(){
        binding.progress.visibility = View.INVISIBLE
        binding.blogin.visibility = View.VISIBLE
    }

    private fun save(key: String, value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(key, value)
        Log.e("save", "savethis")
        editor.apply()
    }


}
