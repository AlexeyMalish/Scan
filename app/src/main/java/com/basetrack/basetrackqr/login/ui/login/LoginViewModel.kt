package com.basetrack.basetrackqr.login.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basetrack.basetrackqr.R
import com.basetrack.basetrackqr.db.QrResult
import com.basetrack.basetrackqr.db.model.User
import com.basetrack.basetrackqr.login.network.data.requestData.LoginModel
import com.basetrack.basetrackqr.login.network.data.requestData.Resource
import com.basetrack.basetrackqr.login.network.data.responseData.UserLoginResponse
import com.basetrack.basetrackqr.login.network.repo.LoginRepository
import com.basetrack.basetrackqr.login.utils.Constants.Companion.EMAIL_REGEX
import com.basetrack.basetrackqr.login.utils.Constants.Companion.MIN_PIN_LENGTH
import com.basetrack.basetrackqr.login.utils.Event
import com.basetrack.basetrackqr.login.utils.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.Realm
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: LoginRepository,
    private val utility: Utility
) : ViewModel() {

    private val _loginResponse = MutableLiveData<Event<Resource<UserLoginResponse>>>()
    val loginResponse: LiveData<Event<Resource<UserLoginResponse>>> = _loginResponse

    var realm: Realm = Realm.getDefaultInstance()


    fun validDate(email: String, password: String): Int {
        when {
            email.isEmpty() -> return R.string.email_field_empty

            password.isEmpty() -> return R.string.password_field_empty

            !utility.regexValidator(
                Pattern.compile(
                    EMAIL_REGEX, Pattern.CASE_INSENSITIVE
                ), email.replace(" ", "")
            ) -> return R.string.email_wrong

            password.length < MIN_PIN_LENGTH -> return (
                    R.string.password_wrong
                    )

            else -> {
                return 0
            }
        }
    }


    fun loginUser(email: String, password: String) = viewModelScope.launch {
        login(LoginModel(email, password))
    }

    private suspend fun login(body: LoginModel) {
        _loginResponse.postValue(Event(Resource.Loading))
        try {
            if (utility.hasInternetConnection()) {
                val response = repo.loginUser(body)
                _loginResponse.postValue(handlePicsResponse(response))
            } else {
                _loginResponse.postValue(
                    Event(
                        Resource.Error(
                            "No internet connection"
                        )
                    )
                )
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    _loginResponse.postValue(
                        Event(
                            Resource.Error(
                                "Network Failure"
                            )
                        )
                    )
                }
                else -> {
                    _loginResponse.postValue(
                        Event(
                            Resource.Error(
                                "Conversion Error"
                            )
                        )
                    )
                }
            }
        }
    }

    private fun handlePicsResponse(response: Response<UserLoginResponse>): Event<Resource<UserLoginResponse>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                realm.beginTransaction()
                val user: User = realm.createObject(User::class.java)
                user.id = resultResponse.user.id
                user.email = resultResponse.user.email
                user.name = resultResponse.user.name
                user.roles = resultResponse.user.roles
                user.status = resultResponse.user.status
                user.orgId = resultResponse.user.org.id
                user.orgName = resultResponse.user.org.name
                realm.commitTransaction()
                return Event(Resource.Success(resultResponse))
            }
        }
        return Event(Resource.Error(response.message()))
    }
}