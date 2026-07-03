package com.example.drivesafe.model

object EsewaConfig {

    // eSewa's public UAT test merchant credentials (from developer.esewa.com.np/pages/Android).
    // Swap these for your real merchant Client ID / Secret Key, and switch ENVIRONMENT to
    // EsewaConfiguration.ENVIRONMENT_PRODUCTION, when you're ready to go live.
    const val CLIENT_ID = "JB0BBQ4aD0UqIThFJwAKBgAXEUkEGQUBBAwdOgABHD4DChwUAB0R"
    const val SECRET_KEY = "BhwIWQQADhIYSxILExMcAgFXFhcOBwAKBgAXEQ=="

    const val CALLBACK_URL = "https://drivesafe-rental.app/esewa/callback"
}
