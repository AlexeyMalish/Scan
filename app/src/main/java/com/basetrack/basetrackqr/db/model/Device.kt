package com.basetrack.basetrackqr.db.model

import io.realm.RealmObject
import io.realm.annotations.Required

open class Device : RealmObject() {
    //Scan barcode

    var barcode : Long = 0

    //Scan qrcode and parse
    @Required
    var name : String = ""

    @Required
    var type : String = ""

    @Required
    var uniq_key : String = ""

    @Required
    var vers : String = ""

  
    var maker_id : Long = 0

    @Required
    var info : String = ""

    @Required
    var reg_date : String = ""
}