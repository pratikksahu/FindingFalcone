package com.pratikk.findingfalcone.data.vehicles

import com.pratikk.findingfalcone.data.core.NetworkModule
import com.pratikk.findingfalcone.data.core.REQ_TYPE
import com.pratikk.findingfalcone.data.core.Request
import com.pratikk.findingfalcone.data.core.model.ApiError
import com.pratikk.findingfalcone.data.core.model.ApiResult
import com.pratikk.findingfalcone.data.core.model.ApiSuccess
import com.pratikk.findingfalcone.data.vehicles.model.Vehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class GetVehiclesRepository {
    val networkHelper = NetworkModule()
    suspend fun getVehicles(): ApiResult<List<Vehicle>> = coroutineScope {
        withContext(Dispatchers.IO) {
            val request = Request<Vehicle>("vehicles", REQ_TYPE.GET)
            val response = networkHelper.makeNetworkRequest(request)
            try {
                if(response == networkHelper.NETWORK_ERROR)
                    throw Exception(networkHelper.NETWORK_ERROR)
                val jsonArray = JSONArray(response)
                val vehicles = buildList {
                    for (i in 0 until jsonArray.length()) {
                        val obj = JSONObject(jsonArray[i].toString())
                        val name = obj.getString("name")
                        val totalNo = obj.getInt("total_no")
                        val maxDistance = obj.getLong("max_distance")
                        val speed = obj.getInt("speed")
                        add(
                            Vehicle(
                                name = name,
                                totalNo = totalNo,
                                maxDistance = maxDistance,
                                speed = speed,
                            )
                        )
                    }
                }
                ApiSuccess(vehicles)
            } catch (e: Exception) {
                ApiError(e.message)
            }
        }
    }
}