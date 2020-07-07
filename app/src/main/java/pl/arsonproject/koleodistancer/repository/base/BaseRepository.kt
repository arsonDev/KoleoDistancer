package pl.arsonproject.koleodistancer.repository.base

import android.util.Log
import retrofit2.Response
import java.io.IOException

open class BaseRepository {

    suspend fun <T : Any> safeApiCall(call : suspend() -> Response<T>, errorMessage : String) : T? {
        val result : Data<T> = safeApiResult(call,errorMessage)
        var data : T? = null

        when(result){
            is Data.Success ->
                data = result.data
            is Data.Error ->
                Log.d("DataRepository", "$errorMessage & Exception - ${result.exception}")
        }

        return data
    }

    private suspend fun <T: Any> safeApiResult(call: suspend ()-> Response<T>, errorMessage: String) : Data<T>{
        val response = call.invoke()
        if(response.isSuccessful) return Data.Success(response.body()!!)

        return Data.Error(IOException("Error Occurred during getting safe Api result, Custom ERROR - $errorMessage"))
    }
}