package pl.arsonproject.koleodistancer.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.arsonproject.koleodistancer.model.Station
import pl.arsonproject.koleodistancer.repository.StationApiRepository
import pl.arsonproject.koleodistancer.utils.round

@RequiresApi(Build.VERSION_CODES.O)
class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val _stationList = MutableLiveData<List<Station>>()
    private val _loading = MutableLiveData<Boolean>()
    private val _distance = MutableLiveData<Double>(0.0)
    private val _errorMessage = MutableLiveData<String>()
    private val _firstError = MutableLiveData<String?>(null)
    private val _secondError = MutableLiveData<String?>(null)

    val firstStation = MutableLiveData<String>()
    val secondStation = MutableLiveData<String>()


    val listStation: LiveData<List<Station>>
        get() = _stationList

    val loading: LiveData<Boolean>
        get() = _loading

    val distance: LiveData<Double>
        get() = _distance

    val errorMessage: LiveData<String>
        get() = _errorMessage

    val firstError: LiveData<String?>
        get() = _firstError

    val secondError: LiveData<String?>
        get() = _secondError

    init {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                _loading.postValue(true)
                val repos = StationApiRepository(application.applicationContext)
                _stationList.postValue(repos.getStationList())
                _loading.postValue(false)
            }
        }
    }

    fun calculate() {
        _loading.value = true
        viewModelScope.launch {
            try {
                _firstError.postValue(null)
                _secondError.postValue(null)
                if (_stationList.value?.count()!! > 0) {
                    val firstStation =
                        _stationList.value?.filter { x -> x.name.equals(firstStation.value) }
                            ?.distinct()?.firstOrNull()
                    if (firstStation == null) {
                        _firstError.postValue("Wybierz stację")
                        return@launch
                    }

                    val secondStation =
                        _stationList.value?.filter { x -> x.name.equals(secondStation.value) }
                            ?.distinct()?.firstOrNull()

                    if (secondStation == null) {
                        _secondError.postValue("Wybierz stację")
                        return@launch
                    }

                    if (firstStation === secondStation){
                        _errorMessage.postValue("Stacje muszą byc różne")
                        _distance.postValue(0.0)
                        return@launch
                    }

                    firstStation?.let { first ->
                        secondStation?.let { sec ->
                            _distance.postValue(first.calculateDistance(sec).round(2))
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Błąd podczas obliczania dystansu ${e.message}")
            }

        }
        _loading.value = false
    }
}