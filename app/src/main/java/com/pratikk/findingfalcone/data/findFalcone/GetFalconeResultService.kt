package com.pratikk.findingfalcone.data.findFalcone

import com.pratikk.findingfalcone.data.core.FalconeTokenHelper
import com.pratikk.findingfalcone.data.core.NetworkModule
import com.pratikk.findingfalcone.data.core.REQ_TYPE
import com.pratikk.findingfalcone.data.core.Request
import com.pratikk.findingfalcone.data.core.model.ApiError
import com.pratikk.findingfalcone.data.core.model.ApiResult
import com.pratikk.findingfalcone.data.core.model.ApiSuccess
import com.pratikk.findingfalcone.data.findFalcone.model.FalconeResponse
import com.pratikk.findingfalcone.data.planets.model.Planet
import com.pratikk.findingfalcone.data.vehicles.model.Vehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class GetFalconeResultService {
    val networkHelper = NetworkModule()
    suspend fun getFalconeResult(
        planets: List<Planet>,
        vehicles: List<Vehicle>,
        token: String,
    ): ApiResult<FalconeResponse> = coroutineScope {
        withContext(Dispatchers.IO) {
            try {
                val reqJson = JSONObject()
                reqJson.put("token", token)
                val planetsJson = JSONArray()
                val vehiclesJson = JSONArray()
                planets.forEach {
                    planetsJson.put(it.name)
                }
                vehicles.forEach {
                    vehiclesJson.put(it.name)
                }
                reqJson.put("planet_names", planetsJson)
                reqJson.put("vehicle_names", vehiclesJson)
                val request = Request<Planet>("find", REQ_TYPE.POST, params = reqJson.toString())
                val response = networkHelper.makeNetworkRequest(request)

                if (response == networkHelper.NETWORK_ERROR)
                    throw Exception(networkHelper.NETWORK_ERROR)
                val jsonObject = JSONObject(response)
                if (jsonObject.has("planet_name")) {
                    val falconeResponse = FalconeResponse(
                        jsonObject.getString("planet_name"),
                        jsonObject.getString("status")
                    )
                    ApiSuccess(falconeResponse)
                } else {
                    ApiSuccess(FalconeResponse(null, jsonObject.getString("status")))
                }
            } catch (e: Exception) {
                ApiError(e.message)
            }
        }
    }
}