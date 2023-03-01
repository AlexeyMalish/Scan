package com.basetrack.basetrackqr.login.utils

class Constants {
    companion object {
        const val MIN_PIN_LENGTH = 8
        const val EMAIL_REGEX = "[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"
        const val BASE_URL = "https:/beta.btk-sf.net/api/"
        const val SAVE_TAG = "Auth"
    }
}