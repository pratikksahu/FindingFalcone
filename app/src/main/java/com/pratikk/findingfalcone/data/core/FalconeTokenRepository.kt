package com.pratikk.findingfalcone.data.core

import com.pratikk.findingfalcone.data.core.model.ApiError
import com.pratikk.findingfalcone.data.core.model.ApiResult
import com.pratikk.findingfalcone.data.core.model.ApiSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONObject

class FalconeTokenRepository constructor(val networkHelper:NetworkModule = NetworkModule()) {
    suspend fun getToken(): ApiResult<String> = coroutineScope{
        withContext(Dispatchers.IO) {
            val req = Request<String>(path = "token", type = REQ_TYPE.POST)
            val response = networkHelper.makeNetworkRequest(req)
            try {
                if(response == networkHelper.NETWORK_ERROR)
                    throw Exception(networkHelper.NETWORK_ERROR)
                val jsonObject = JSONObject(response)
                val token = jsonObject.getString("token")
                ApiSuccess(token)
            }catch (e:Exception){
                ApiError(e.message)
            }
        }
    }
}