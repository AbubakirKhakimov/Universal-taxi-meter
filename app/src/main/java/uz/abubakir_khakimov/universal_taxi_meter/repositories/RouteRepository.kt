package uz.abubakir_khakimov.universal_taxi_meter.repositories

import uz.abubakir_khakimov.universal_taxi_meter.dao.RouteDao
import uz.abubakir_khakimov.universal_taxi_meter.models.Route

class RouteRepository(private val routeDao: RouteDao) {

    val getAllRoutes = routeDao.getAllRoutes()

    suspend fun insertRoute(route: Route){
        routeDao.insertRoute(route)
    }

    suspend fun clearDatabase(){
        routeDao.clearDatabase()
    }

}