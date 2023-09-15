package com.pratikk.findingfalcone.data.core

import com.pratikk.findingfalcone.data.core.model.ApiError
import com.pratikk.findingfalcone.data.core.model.ApiResult
import com.pratikk.findingfalcone.data.core.model.ApiSuccess
import com.pratikk.findingfalcone.data.core.model.onError
import com.pratikk.findingfalcone.data.core.model.onSuccess
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONObject

class FalconeTokenHelper {
    val networkHelper = NetworkModule()
    suspend fun getToken(): ApiResult<String> = coroutineScope{
        withContext(Dispatchers.IO) {
            val req = Request<String>(path = "token", type = REQ_TYPE.POST)
            val result = networkHelper.makeNetworkRequest(req)
            try {
                val jsonObject = JSONObject(result)
                val token = jsonObject.getString("token")
                ApiSuccess(token)
            }catch (e:Exception){
                ApiError(e.message)
            }
        }
    }
}