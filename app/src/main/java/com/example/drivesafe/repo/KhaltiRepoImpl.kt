package com.example.drivesafe.repo

import android.util.Log
import com.example.drivesafe.model.KhaltiConfig
import com.khalti.checkout.data.Environment
import com.khalti.checkout.data.KhaltiPayConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "KhaltiRepo"

class KhaltiRepoImpl : KhaltiRepo {

    // NOTE: this calls Khalti's Initiate Payment API directly from the app using the
    // secret key, which means the secret key ships inside the APK where it can be
    // extracted. Fine for testing, but a real production build should proxy this call
    // through a backend (e.g. a Firebase Cloud Function) that holds the secret key
    // server-side instead.
    override suspend fun initiatePayment(
        amount: Int,
        purchaseOrderId: String,
        purchaseOrderName: String
    ): String? {
        return withContext(Dispatchers.IO) {
            try {
                val body = JSONObject().apply {
                    put("return_url", KhaltiConfig.RETURN_URL)
                    put("website_url", KhaltiConfig.WEBSITE_URL)
                    put("amount", amount)
                    put("purchase_order_id", purchaseOrderId)
                    put("purchase_order_name", purchaseOrderName)
                }

                val connection = URL(KhaltiConfig.INITIATE_URL).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Authorization", "Key ${KhaltiConfig.SECRET_KEY}")
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }

                val responseCode = connection.responseCode
                val stream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
                val response = stream?.bufferedReader()?.use { it.readText() } ?: ""

                if (responseCode !in 200..299) {
                    Log.e(TAG, "Initiate Payment failed ($responseCode): $response")
                    return@withContext null
                }

                JSONObject(response).optString("pidx").takeIf { it.isNotBlank() }
            } catch (e: Exception) {
                Log.e(TAG, "Initiate Payment request error", e)
                null
            }
        }
    }

    override fun buildConfig(pidx: String): KhaltiPayConfig {
        return KhaltiPayConfig(
            publicKey = KhaltiConfig.PUBLIC_KEY,
            pidx = pidx,
            openInKhalti = false,
            environment = if (KhaltiConfig.IS_PRODUCTION) Environment.PROD else Environment.TEST
        )
    }
}
