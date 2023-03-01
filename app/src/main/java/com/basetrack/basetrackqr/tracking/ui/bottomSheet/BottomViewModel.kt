package com.basetrack.basetrackqr.tracking.ui.bottomSheet

import android.os.Build
import android.util.ArraySet
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basetrack.basetrackqr.db.QrResult
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import javax.inject.Inject

@HiltViewModel
class BottomViewModel @Inject constructor() : ViewModel() {
    private lateinit var realm: Realm
    var qrToDisplay = mutableListOf<QrResult>()
    var qrNotFoundString : StringBuilder = StringBuilder()

    @RequiresApi(Build.VERSION_CODES.M)
    fun getAllQrResult(scanQr: androidx.collection.ArraySet<String>) = viewModelScope.launch {
        realm = Realm.getDefaultInstance()
        realm.beginTransaction();
        val qrResult = realm.where(QrResult::class.java).findAll()
        realm.commitTransaction();
        for (qr in scanQr) {
            var qrFound = 0
            qrResult.forEach {
                if (it.firstQrResult == qr) {
                    qrToDisplay.add(it)
                    qrFound++
                }
            }
            /*
                Элементы не найденные в бд
             */
            if(qrFound==0){
                qrNotFoundString.append("$qr\n")
                val qrNotFound = QrResult()
                qrNotFound.firstQrResult = qr
                qrNotFound.secondQrResult = "Kit not found"
                qrNotFound.date = ""
                qrToDisplay.add(qrNotFound)
            }
        }
    }

}