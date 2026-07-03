package com.example.drivesafe.model

object KhaltiConfig {

    // These are sandbox/test keys from test-admin.khalti.com (Khalti's dashboard labels
    // them "Live secret key"/"Live public key" even on the sandbox portal — confusing,
    // but they only work against the sandbox API, not production). When you switch to
    // real production keys from admin.khalti.com, flip IS_PRODUCTION to true.
    const val PUBLIC_KEY = "a4bf080f0ebf49f599e31105c833cd07"
    const val SECRET_KEY = "0a28281600194be28066aca443f782f0"
    const val IS_PRODUCTION = false

    val INITIATE_URL =
        if (IS_PRODUCTION) "https://khalti.com/api/v2/epayment/initiate/"
        else "https://dev.khalti.com/api/v2/epayment/initiate/"

    const val RETURN_URL = "https://drivesafe-rental.app/khalti/return"
    const val WEBSITE_URL = "https://drivesafe-rental.app"
}
