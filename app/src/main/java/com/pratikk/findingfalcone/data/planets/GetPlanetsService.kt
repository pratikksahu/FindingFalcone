package com.pratikk.findingfalcone.data.planets

import com.pratikk.findingfalcone.data.core.NetworkModule
import com.pratikk.findingfalcone.data.core.REQ_TYPE
import com.pratikk.findingfalcone.data.core.Request
import com.pratikk.findingfalcone.data.core.model.ApiError
import com.pratikk.findingfalcone.data.core.model.ApiResult
import com.pratikk.findingfalcone.data.core.model.ApiSuccess
import com.pratikk.findingfalcone.data.planets.model.Planet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class GetPlanetsService {
    val networkHelper = NetworkModule()
    suspend fun getPlanets():ApiResult<List<Planet>> = coroutineScope{
        withContext(Dispatchers.IO){
            val request = Request<Planet>("planets",REQ_TYPE.GET)
            val response = networkHelper.makeNetworkRequest(request)
            try {
                if(response == networkHelper.NETWORK_ERROR)
                    throw Exception(networkHelper.NETWORK_ERROR)
                val jsonArray = JSONArray(response)
                val planets = buildList {
                    for(i in 0 until jsonArray.length()){
                        val obj = JSONObject(jsonArray[i].toString())
                        val name = obj.getString("name")
                        val distance = obj.getLong("distance")
                        add(Planet(name = name, distance = distance))
                    }
                }
                ApiSuccess(planets)
            }catch (e:Exception){
                ApiError(e.message)
            }
        }
    }
}