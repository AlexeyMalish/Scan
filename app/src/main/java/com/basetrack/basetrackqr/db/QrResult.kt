package com.basetrack.basetrackqr.db

import io.realm.RealmObject
import io.realm.annotations.Required

open class QrResult : RealmObject() {

    @Required
    var firstQrResult: String = ""

    @Required
    var secondQrResult: String = ""

    @Required
    var date: String = ""


}