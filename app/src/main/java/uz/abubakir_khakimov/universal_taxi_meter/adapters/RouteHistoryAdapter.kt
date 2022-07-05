package uz.abubakir_khakimov.universal_taxi_meter.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.abubakir_khakimov.universal_taxi_meter.databinding.HistoryDateItemLayoutBinding
import uz.abubakir_khakimov.universal_taxi_meter.databinding.HistoryRouteItemLayoutBinding
import uz.abubakir_khakimov.universal_taxi_meter.models.Route
import java.lang.ClassCastException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RouteHistoryAdapter(private val itemsList: ArrayList<Any>, private val datePositionsList: ArrayList<Int>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class RouteHolder(val binding: HistoryRouteItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun onBind(route: Route){
            binding.price.text = "${getDecimalFormat(route.price)} ${route.currency}"
            binding.distance.text = "${getDecimalFormat(route.distance)} km"
            binding.time.text = getStringDate(route.time, "HH:mm:ss")
            binding.completionTime.text = getStringDate(route.completionTime, "HH:mm")
        }
    }

    inner class DateHolder(val binding: HistoryDateItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun onBind(date: String){
            binding.date.text = date
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (datePositionsList.contains(position)){
            1
        }else{
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0){
            RouteHolder(HistoryRouteItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }else{
            DateHolder(HistoryDateItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            if (getItemViewType(position) == 0){
                (holder as RouteHolder).onBind(itemsList[position] as Route)
            }else{
                (holder as DateHolder).onBind(itemsList[position] as String)
            }
        }catch (e: ClassCastException){
            Log.d("testList", itemsList[position].toString())
        }
    }

    override fun getItemCount(): Int = itemsList.size

    private fun getStringDate(it: Long, pattern: String): String{
        return SimpleDateFormat(pattern).format(Date(it))
    }

    private fun getDecimalFormat(it: Double): String{
        return DecimalFormat("#.##").format(it)
    }

}