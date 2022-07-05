package uz.abubakir_khakimov.universal_taxi_meter.models.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.abubakir_khakimov.universal_taxi_meter.database.RouteDatabase
import uz.abubakir_khakimov.universal_taxi_meter.models.Route
import uz.abubakir_khakimov.universal_taxi_meter.repositories.RouteRepository

class RouteViewModel(application: Application): AndroidViewModel(application) {

    private val repository: RouteRepository

    init {
        val routeDao = RouteDatabase.getInstance(application).routeDao()
        repository = RouteRepository(routeDao)
    }

    fun getAllRoutes(): LiveData<List<Route>> = repository.getAllRoutes

    fun insertRoute(route: Route){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertRoute(route)
        }
    }

    fun clearDatabase(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearDatabase()
        }
    }

}