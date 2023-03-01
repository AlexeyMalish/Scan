package com.basetrack.basetrackqr.db

import android.util.Log
import com.basetrack.basetrackqr.db.gsonModel.Code
import com.basetrack.basetrackqr.db.gsonModel.SaveModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.delay
import java.io.File

class FireBaseCloudStorage {
    val db = Firebase.firestore

    fun addDevice(barcode: String, qrCode: String) {
        val id = hashMapOf(
            barcode to qrCode,
        )
            db.collection("participants")
                .document("device")
                .update(id as Map<String, Any>)
                .addOnSuccessListener {
                    Log.d("CloudStorage", "DocumentSnapshot added with ID:")
                }
                .addOnFailureListener { e ->
                    Log.e("CloudStorage", "Error adding document", e)
                }
    }

    fun addUsers(name : String, info : String){
        val id = hashMapOf(
           name to info,
        )
        db.collection("participants")
            .document("users")
            .update(id as Map<String, Any>)
            .addOnSuccessListener {
                Log.d("CloudStorage", "DocumentSnapshot added with ID:")
            }
            .addOnFailureListener { e ->
                Log.e("CloudStorage", "Error adding document", e)
            }
    }

    fun getCompany() : ArrayList<String>{
        val companyArray = ArrayList<String>()
        val docRef = db.collection("participants").document("agent")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    document.data?.let { companyArray.addAll(it.keys) }
                    Log.e("FireBase", "DocumentSnapshot data: ${document.data}")
                    Log.e("FireBase", trackingList.toString())
                } else {
                    Log.e("FireBase", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FireBase", "get failed with ", exception)
            }
        return companyArray
    }


    private val trackingList = HashMap<String, Any>()

    suspend fun addTracking( trackingQrKey: androidx.collection.ArraySet<String>, saveModel: SaveModel, naklad : File) {

        val docRef = db.collection("participants").document("device")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    trackingList.putAll(document.data as HashMap<String, Any>)
                    Log.e("FireBase", "DocumentSnapshot data: ${document.data}")
                    Log.e("FireBase", trackingList.toString())
                } else {
                    Log.e("FireBase", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FireBase", "get failed with ", exception)
            }
        ///TODO нужно дождаться запрос на получение, а затем отправить, как лучше?
        delay(1300)

        val saveQr = HashMap<String, Any?>()
        val trackingQrArray = trackingQrKey.toArray()
        if (trackingList.isNotEmpty()) {
            for (i in 0 until trackingQrKey.size) {
                if (trackingList.containsKey(trackingQrArray[i])) {
                    val code = Code(trackingQrArray[i] as String,
                        trackingList[trackingQrArray[i]] as String
                    )
                    saveModel.comolect?.add(code)
               }
            }
        } else {
            Log.e("FireBase", "trackingList Empty!!")
        }

        Log.e("===>", saveModel.toString())
        if (saveModel.comolect?.isNotEmpty() == true) {

            val gson = Gson()
            val key = gson.toJson(saveModel)
            Log.e("gson", key)
            val map = hashMapOf(
                "key" to key,
            )
            db.collection("tracking")
                .document("track")
                .update(map as Map<String, Any>)
                .addOnSuccessListener {
                    Log.d("CloudStorage", "DocumentSnapshot added with ID:")
                }
                .addOnFailureListener { e ->
                    Log.e("CloudStorage", "Error adding document", e)
                }
        }
    }


}


