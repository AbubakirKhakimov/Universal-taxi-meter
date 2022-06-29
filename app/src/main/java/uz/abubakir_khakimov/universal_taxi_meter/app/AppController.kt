package uz.abubakir_khakimov.universal_taxi_meter.app

import android.app.Application
import com.orhanobut.hawk.Hawk

class AppController: Application() {

    override fun onCreate() {
        super.onCreate()
        Hawk.init(this).build()
    }

}