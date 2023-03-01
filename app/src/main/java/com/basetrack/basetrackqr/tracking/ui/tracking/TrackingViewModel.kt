package com.basetrack.basetrackqr.tracking.ui.tracking

import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.util.ArraySet
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.basetrack.basetrackqr.R
import com.basetrack.basetrackqr.db.FireBaseCloudStorage
import com.basetrack.basetrackqr.db.QrResult
import com.basetrack.basetrackqr.db.gsonModel.SaveModel
import com.basetrack.basetrackqr.db.model.Tracking
import com.basetrack.basetrackqr.db.model.User
import com.bumptech.glide.Glide
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class TrackingViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    private val _transporterText = MutableLiveData<String>()
    val transportText: LiveData<String>
        get() = _transporterText

    private val _companyText = MutableLiveData<String>()
    val companyText: LiveData<String>
        get() = _companyText

    private val _date = MutableLiveData<String>()
    val date: LiveData<String>
        get() = _date


    private val storage = FireBaseCloudStorage()
    private val myCalendar: Calendar = Calendar.getInstance()

    var realm: Realm = Realm.getDefaultInstance()


    val data =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

    private fun updateLabel() {
        val myFormat = "dd/MM/yy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        _date.value = sdf.format(myCalendar.time)
    }


    fun getCalendar(): Calendar {
        return myCalendar
    }


    fun getCompany(): ArrayList<String> {
        return storage.getCompany()
    }

    fun sendSave(
        qrList: androidx.collection.ArraySet<String>,
        saveModel: SaveModel,
        nakladImage: File
    ) = viewModelScope.launch {
        storage.addTracking(qrList, saveModel, nakladImage)
    }

    fun saveInfo(
        transporter: String,
        company: String,
        type: String,
        date: String,
        complect: String,
        count: Long,
        trip: String
    ) = viewModelScope.launch {
        realm.beginTransaction()
        val user = realm.where(User::class.java).findFirst()
        val userId = user?.id
        val result: Tracking = realm.createObject(Tracking::class.java)

        val data = System.currentTimeMillis()

        result.date = data
        if (userId != null) {
            result.userId = userId
        }
        result.dateDelivery = date
        ///TODO place, thingID and Dist Id from String to INT!!
        result.count = count
        result.type = type
        result.trip = trip
        realm.commitTransaction()
    }



}

