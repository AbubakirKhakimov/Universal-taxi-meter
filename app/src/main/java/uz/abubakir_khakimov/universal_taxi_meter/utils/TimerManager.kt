package uz.abubakir_khakimov.universal_taxi_meter.utils

import kotlinx.coroutines.*

interface TimerManagerCallBack{
    fun onTick(time: Long)
}

class TimerManager(private val timerManagerCallBack: TimerManagerCallBack) {

    private var timerScope: Job? = null
    var time = -21600000L

    fun startTimer(){
        timerScope = GlobalScope.launch {
            withContext(Dispatchers.Default) {
                while (true) {
                    delay(1000)
                    time += 1000
                    withContext(Dispatchers.Main){
                        timerManagerCallBack.onTick(time)
                    }
                }
            }
        }
    }

    fun stopTimer(){
        timerScope?.cancel()
        timerScope = null
        time = -21600000L
    }

}