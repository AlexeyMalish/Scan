package com.basetrack.basetrackqr.qrScan.ui.qrResult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basetrack.basetrackqr.db.QrResult
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrResultViewModel @Inject constructor() : ViewModel() {

    private lateinit var realm : Realm
    lateinit var qrResult : RealmResults<QrResult>

    fun getAllQrResult() = viewModelScope.launch {
        realm = Realm.getDefaultInstance()
        realm.beginTransaction();
        qrResult  = realm.where(QrResult::class.java).findAll()
        realm.commitTransaction();
    }
}