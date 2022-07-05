package uz.abubakir_khakimov.universal_taxi_meter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.abubakir_khakimov.universal_taxi_meter.dao.RouteDao
import uz.abubakir_khakimov.universal_taxi_meter.models.Route

@Database(entities = [Route::class], version = 1)
abstract class RouteDatabase: RoomDatabase() {

    abstract fun routeDao(): RouteDao

    companion object{
        private var instance: RouteDatabase? = null

        @Synchronized
        fun getInstance(context: Context): RouteDatabase{
            if (instance == null){
                instance = Room.databaseBuilder(context, RouteDatabase::class.java, "route_database")
                    .build()
            }

            return instance!!
        }
    }

}