import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import pl.arsonproject.koleodistancer.repository.StationApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiFactory {
    private val koleoClient = OkHttpClient().newBuilder()
        .build()

    private fun retrofit() = Retrofit.Builder()
        .client(koleoClient)
        .baseUrl("https://koleo.pl/api/android/")
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val stationApi : StationApi = retrofit().create(StationApi::class.java)
}