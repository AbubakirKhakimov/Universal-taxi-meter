package uz.abubakir_khakimov.universal_taxi_meter.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

interface RotationVectorManagerCallBack{
    fun rotationChanged(orientation: Float)
}

class RotationVectorManager(private val context: Context, private val rotationVectorManagerCallBack: RotationVectorManagerCallBack) {

    var orientation = 0.0f

    fun startSensor(){
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        val sListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val rotationMatrix = FloatArray(16)
                SensorManager.getRotationMatrixFromVector(
                    rotationMatrix, event!!.values
                )
                val remappedRotationMatrix = FloatArray(16)
                SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z,
                    remappedRotationMatrix
                )

                val orientations = FloatArray(3)
                SensorManager.getOrientation(remappedRotationMatrix, orientations)
                orientation = Math.toDegrees(orientations[2].toDouble()).toFloat()
                rotationVectorManagerCallBack.rotationChanged(orientation)
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(sListener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

}