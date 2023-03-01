package com.basetrack.basetrackqr.qrScan.ui.mainCamera

import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basetrack.basetrackqr.R
import com.basetrack.basetrackqr.db.FireBaseCloudStorage
import com.basetrack.basetrackqr.db.QrResult
import com.basetrack.basetrackqr.qrScan.qrUtils.QRCameraConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.Realm
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainCameraViewModel @Inject constructor(private val sharedPreferences: SharedPreferences) : ViewModel()  {

    private val _qrTextViewText = MutableLiveData<String>()
    val qrTextViewText : LiveData<String>
    get() = _qrTextViewText

    private val _qrTextViewVisibility = MutableLiveData<Int>()
    val qrTextViewVisibility : LiveData<Int>
    get() = _qrTextViewVisibility

    private val _scanVisibility = MutableLiveData<Int>()
    val scanVisibility : LiveData<Int>
    get() = _scanVisibility

    private val _scanText = MutableLiveData<Int>()
    val scanText : LiveData<Int>
        get() = _scanText

    private val _animationVisibility = MutableLiveData<Int>()
    val animationVisibility : LiveData<Int>
    get() = _animationVisibility

    private val _animationPlay = MutableLiveData<Boolean>()
    val animationPlay : LiveData<Boolean>
    get() = _animationPlay

    private var isFirstQr : Boolean = true

    var realm: Realm = Realm.getDefaultInstance()

    private val storage = FireBaseCloudStorage()

    private val config = QRCameraConfiguration(lensFacing = CameraSelector.LENS_FACING_BACK)


    fun firstQrText(){
        _scanText.value = R.string.scan_barcode
    }

    fun firstQrClick(){
        _qrTextViewText.value = ""
        _scanText.value = R.string.scan_qr
        isFirstQr = false
    }

    fun secondQrClick(){
        _qrTextViewText.value  =""
    }

    fun trackingScanQrClick(){
        _scanText.value = R.string.ok
    }

    fun saveDataInServer(firstQr : String, secondQr : String) = viewModelScope.launch {
        val email = sharedPreferences.getString("Login", "")
        Log.e("email", email.toString())
        if (email != null) {
            if(sharedPreferences.getString("DocumentCreate", "")!="First"){
                storage.addDevice(firstQr, secondQr)
                sharedPreferences.edit().putString("DocumentCreate", "First").apply()
            }
            else
                storage.addDevice(firstQr, secondQr)
        }
    }

    fun saveDataInLocal(firstQr : String, secondQr : String) = viewModelScope.launch {

        realm.beginTransaction()

        //TODO replace on Device after parsing qr
        val result: QrResult = realm.createObject(QrResult::class.java)

        val date  = getCurrentDateTime()
        val dateInString = date.toString("yyyy/MM/dd HH:mm:ss")

        result.firstQrResult = firstQr
        result.secondQrResult = secondQr
        result.date = dateInString
        realm.commitTransaction()



        _animationPlay.value = true
        _animationVisibility.value = View.VISIBLE
        _qrTextViewVisibility.value = View.INVISIBLE
        _scanVisibility.value = View.INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            _animationPlay.value = false
            _animationVisibility.value = View.INVISIBLE
            _qrTextViewVisibility.value = View.VISIBLE
            _scanVisibility.value = View.VISIBLE
        }, 1300)

     isFirstQr = true
     _qrTextViewText.value = ""
    }

    fun switchCameraClick(): QRCameraConfiguration {
        if (config.lensFacing == CameraSelector.LENS_FACING_BACK) {
            config.lensFacing = CameraSelector.LENS_FACING_FRONT
        } else {
            config.lensFacing = CameraSelector.LENS_FACING_BACK
        }
        return config
    }

    fun getConfig(): QRCameraConfiguration {
        return config
    }

    fun isFirstQr(): Boolean {
        return isFirstQr
    }

    fun realmClose(){
        realm.close()
    }

    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }




}