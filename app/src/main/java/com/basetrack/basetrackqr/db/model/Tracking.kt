package com.basetrack.basetrackqr.db.model

import io.realm.RealmObject
import io.realm.annotations.Required
import org.json.JSONObject

open class Tracking : RealmObject(){


    var date : Long = 0


    var userId : Long = 0L

    @Required
    var dateDelivery : String = ""

    var placeId : Long = 0

    var distId : Long = 0

    var thingId : Long = 0


    var count : Long = 0

    @Required
    var type : String = ""

    @Required
    var trip : String = ""
}