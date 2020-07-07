package pl.arsonproject.koleodistancer.repository

import kotlinx.coroutines.Deferred
import pl.arsonproject.koleodistancer.model.Station
import retrofit2.Response
import retrofit2.http.GET

interface StationApi {
    @GET("v1/stations.json")
    fun getStationListAsync() : Deferred<Response<List<Station>>>
}