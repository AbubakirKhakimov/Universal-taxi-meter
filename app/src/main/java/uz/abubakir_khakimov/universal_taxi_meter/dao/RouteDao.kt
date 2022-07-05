package uz.abubakir_khakimov.universal_taxi_meter.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.abubakir_khakimov.universal_taxi_meter.models.Route

@Dao
interface RouteDao {

    @Query("SELECT * FROM route_table ORDER BY completionTime DESC")
    fun getAllRoutes(): LiveData<List<Route>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRoute(route: Route)

    @Query("DELETE FROM route_table")
    fun clearDatabase()

}