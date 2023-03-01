package com.basetrack.basetrackqr.db.model

import io.realm.RealmObject
import io.realm.annotations.Required

open class User : RealmObject() {


    var id : Long = 0

    @Required
    var name : String = ""

    @Required
    var email : String = ""


    var status : Int = 0


    var roles : Int = 0


    var orgId : Int = 0

    @Required
    var orgName : String = ""


    override fun toString(): String {
        return "User(id=$id, name='$name', email='$email', status=$status, roles=$roles, orgId=$orgId, orgName='$orgName')"
    }

}


