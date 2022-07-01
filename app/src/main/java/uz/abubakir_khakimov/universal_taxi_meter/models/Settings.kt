package uz.abubakir_khakimov.universal_taxi_meter.models

data class Settings(
    var currency: String,
    var distanceMinimalka: Double,
    var priceMinimalka: Double,
    var pricePerKm: Double
)
