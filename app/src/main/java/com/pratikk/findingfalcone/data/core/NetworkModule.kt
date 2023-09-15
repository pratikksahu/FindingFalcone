package com.pratikk.findingfalcone.data.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.pratikk.findingfalcone.data.core.model.ApiError
import com.pratikk.findingfalcone.data.core.model.ApiResult
import com.pratikk.findingfalcone.data.core.model.ApiSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException

enum class REQ_TYPE {
    POST, GET
}
data class Request<T : Any>(
    val path: String,
    val type: REQ_TYPE,
    val params: String? = null
)

class NetworkModule {
    val BASE_URL = "https://findfalcone.geektrust.com/"
    val NETWORK_ERROR = "No Internet Connection"
    suspend fun <T:Any> makeNetworkRequest(request: Request<T>): String = coroutineScope {
        withContext(Dispatchers.IO) {
            var result = ""
            var connection: HttpURLConnection? = null
            try {
                val url = URL(BASE_URL + request.path)
                Log.d("NETWORK",url.path)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = request.type.name
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.apply {
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("Content-Type", "application/json");
                }
                if (!request.params.isNullOrEmpty()) {
                    val os = connection.outputStream
                    val writer = BufferedWriter(OutputStreamWriter(os))
                    writer.write(request.params)
                    writer.flush()
                    writer.close()
                    os.flush()
                    os.close()
                }
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                    result = stringBuilder.toString()
                    Log.d("NETWORK_RESP",result)
                } else {
                    // Handle error cases here
                }
            }
            catch (e:UnknownHostException){
                return@withContext NETWORK_ERROR
            }
            catch (e: Exception) {
                e.printStackTrace()
                // Handle exceptions here
            } finally {
                connection?.disconnect()
            }
            return@withContext result
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            return true
        }

        // For Android 10 and above, use NetworkCapabilities
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }
}