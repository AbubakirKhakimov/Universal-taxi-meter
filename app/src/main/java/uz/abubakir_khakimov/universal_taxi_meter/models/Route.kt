package uz.abubakir_khakimov.universal_taxi_meter.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_table")
data class Route(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var currency: String,
    val price: Double,
    val distance: Double,
    val time: Long, // time A to B
    val completionTime: Long
)
