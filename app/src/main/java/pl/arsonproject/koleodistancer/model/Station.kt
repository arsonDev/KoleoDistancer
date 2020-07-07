package pl.arsonproject.koleodistancer.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Station(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "name_slug") val name_slug: String,
    @Json(name = "latitude") val latitude: Double?,
    @Json(name = "longitude") val longitude: Double?,
    @Json(name = "hits") val hits: Int?,
    @Json(name = "ibnr") val ibnr: Int?
){
    fun calculateDistance(secondStation : Station) : Double {
        if (latitude != null && longitude != null && secondStation.longitude != null && secondStation.latitude != null) {
            return if (latitude === secondStation.latitude && longitude === secondStation.longitude) {
                0.0
            } else {
                val theta: Double = longitude - secondStation.longitude!!
                var dist =
                    Math.sin(Math.toRadians(latitude!!)) * Math.sin(
                        Math.toRadians(secondStation.latitude!!)
                    ) + Math.cos(Math.toRadians(latitude!!)) * Math.cos(
                        Math.toRadians(
                            secondStation.latitude!!
                        )
                    ) * Math.cos(Math.toRadians(theta))
                dist = Math.acos(dist)
                dist = Math.toDegrees(dist)
                dist *= 60 * 1.1515
                dist *= 1.609344
                dist
            }
        }
        else
            return 0.0
    }
}