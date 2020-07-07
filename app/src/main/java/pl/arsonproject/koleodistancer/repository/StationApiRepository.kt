package pl.arsonproject.koleodistancer.repository

import ApiFactory
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import pl.arsonproject.koleodistancer.model.Station
import pl.arsonproject.koleodistancer.repository.base.BaseRepository
import java.io.File
import java.lang.reflect.Type
import java.time.LocalDateTime
import kotlin.time.Duration


class StationApiRepository(private val context: Context) :
    BaseRepository() {

    private val FILE_NAME = "Locations.json"
    private val LAST_DOWNLOAD = "lastDownload"
    private val api: StationApi by lazy {
        ApiFactory.stationApi
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getStationList(): List<Station> {
        var result: List<Station> = listOf()
        var file = File("${context.cacheDir.absolutePath}/$FILE_NAME")
        val sharedPref = context.getSharedPreferences(LAST_DOWNLOAD, 0)
        var lastDownload = sharedPref.getString(LAST_DOWNLOAD, null)

        val moshi = Moshi.Builder().build()
        val listMyData: Type = Types.newParameterizedType(
            MutableList::class.java,
            Station::class.java
        )
        val jsonAdapter: JsonAdapter<List<Station>> = moshi.adapter(listMyData)

        if (lastDownload.isNullOrEmpty()) {
            val stationResponse = safeApiCall(
                call = {
                    api.getStationListAsync().await()
                },
                errorMessage = "Błąd pobrania listy stacji"
            )
            stationResponse?.let {
                file.bufferedWriter().use { out -> out.write(jsonAdapter.toJson(it)) }
                sharedPref.edit {
                    putString(LAST_DOWNLOAD, LocalDateTime.now().toString())
                    commit()
                }
            }
        } else {
            var date = LocalDateTime.parse(lastDownload)
            val duration = java.time.Duration.between(date,LocalDateTime.now())

            if (file.exists() && duration.toHours() <= 24) {
                result = jsonAdapter.fromJson(file.bufferedReader().use { it.readText() })!!
            } else {
                safeApiCall(
                    call = {
                        api.getStationListAsync().await()
                    },
                    errorMessage = "Błąd pobrania listy stacji"
                )?.let {
                    result = it
                    file.bufferedWriter().use { out -> out.write(jsonAdapter.toJson(it)) }
                    sharedPref.edit {
                        putString(LAST_DOWNLOAD, LocalDateTime.now().toString())
                        commit()
                    }
                }
            }
        }

        return result
    }
}