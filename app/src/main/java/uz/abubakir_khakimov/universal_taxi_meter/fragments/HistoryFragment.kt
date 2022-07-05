package uz.abubakir_khakimov.universal_taxi_meter.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.abubakir_khakimov.universal_taxi_meter.adapters.RouteHistoryAdapter
import uz.abubakir_khakimov.universal_taxi_meter.databinding.ClearHistoryDialogLayoutBinding
import uz.abubakir_khakimov.universal_taxi_meter.databinding.FragmentHistoryBinding
import uz.abubakir_khakimov.universal_taxi_meter.models.Route
import uz.abubakir_khakimov.universal_taxi_meter.models.Settings
import uz.abubakir_khakimov.universal_taxi_meter.models.viewModels.RouteViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var routeHistoryAdapter: RouteHistoryAdapter
    private lateinit var viewModel: RouteViewModel

    private val itemsList = ArrayList<Any>()
    private val datePositionsList = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[RouteViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        routeHistoryAdapter = RouteHistoryAdapter(itemsList, datePositionsList)
        binding.historyRv.adapter = routeHistoryAdapter

        viewModel.getAllRoutes().observe(viewLifecycleOwner){
            lifecycleScope.launch(Dispatchers.Default){
                generateDate(it)
            }
        }

        binding.backStack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.clear.setOnClickListener {
            showClearHistoryDialog()
        }

//        viewModel.insertRoute(Route(0, "UZS", 15000.0, 16.0, 1234567894, Date().time))
//        viewModel.insertRoute(Route(0, "UZS", 15000.0, 16.0, 1234567894, Date().time))
//        viewModel.insertRoute(Route(0, "UZS", 15000.0, 16.0, 1234567894, Date().time))
//        viewModel.insertRoute(Route(0, "UZS", 15000.0, 16.0, 1234567894, Date().time))
//        viewModel.insertRoute(Route(0, "UZS", 15000.0, 16.0, 1234567894, Date().time))
//        viewModel.insertRoute(Route(0, "UZS", 15000.0, 16.0, 1234567894, Date().time - 86400000))
//        viewModel.insertRoute(Route(0, "UZS", 15000.0, 16.0, 1234567894, Date().time - 86400000))
//        viewModel.insertRoute(Route(0, "UZS", 15000.0, 16.0, 1234567894, Date().time - 86400000))
//        viewModel.insertRoute(Route(0, "UZS", 15000.0, 16.0, 1234567894, Date().time - 86400000 - 86400000))
//        viewModel.insertRoute(Route(0, "UZS", 15000.0, 16.0, 1234567894, Date().time - 86400000 - 86400000))
//        viewModel.insertRoute(Route(0, "UZS", 15000.0, 16.0, 1234567894, Date().time - 86400000 - 86400000))
//        viewModel.insertRoute(Route(0, "UZS", 15000.0, 16.0, 1234567894, Date().time - 86400000 - 86400000 - 86400000))
//        viewModel.insertRoute(Route(0, "UZS", 15000.0, 16.0, 1234567894, Date().time - 86400000 - 86400000 - 86400000))
//        viewModel.insertRoute(Route(0, "UZS", 15000.0, 16.0, 1234567894, Date().time - 86400000 - 86400000 - 86400000 - 86400000))

    }

    private suspend fun generateDate(routesList: List<Route>) {
        itemsList.clear()
        datePositionsList.clear()

        var dailyAmount = 0.0
        var monthlyAmount = 0.0
        val beginningMonth = getBeginningMonth()

        val todayDate = getStringDate(Date().time)
        val tomorrowDate = getStringDate(Date().time - 86400000)

        routesList.forEachIndexed { index, route ->

            when(val itemDate = getStringDate(route.completionTime)){
                todayDate -> {
                    if (!itemsList.contains("Bugun")) {
                        itemsList.add("Bugun")
                        datePositionsList.add(itemsList.size - 1)
                    }

                    dailyAmount += route.price
                }
                tomorrowDate -> {
                    if (!itemsList.contains("Kecha")) {
                        itemsList.add("Kecha")
                        datePositionsList.add(itemsList.size - 1)
                    }
                }
                else -> {
                    if (!itemsList.contains(itemDate)) {
                        itemsList.add(itemDate)
                        datePositionsList.add(itemsList.size - 1)
                    }
                }
            }

            if (route.completionTime >= beginningMonth){
                monthlyAmount += route.price
            }
            itemsList.add(route)
        }

        withContext(Dispatchers.Main){
            routeHistoryAdapter.notifyDataSetChanged()
            amountUIController(dailyAmount, monthlyAmount, routesList[0].currency)
        }
    }

    private fun getBeginningMonth(): Long{
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return calendar.timeInMillis
    }

    private fun amountUIController(dailyAmount: Double, monthlyAmount: Double, currency: String){
        binding.daily.text = "${getDecimalFormat(dailyAmount)} $currency"
        binding.monthly.text = "${getDecimalFormat(monthlyAmount)} $currency"
    }

    private fun showClearHistoryDialog(){
        val customDialog = AlertDialog.Builder(requireActivity()).create()
        val dialogBinding = ClearHistoryDialogLayoutBinding.inflate(layoutInflater)
        customDialog.setView(dialogBinding.root)

        dialogBinding.no.setOnClickListener {
            customDialog.dismiss()
        }

        dialogBinding.yes.setOnClickListener {
            viewModel.clearDatabase()
            Toast.makeText(requireActivity(), "History cleared successfully!", Toast.LENGTH_SHORT).show()
            customDialog.dismiss()
        }

        customDialog.show()
    }

    private fun getStringDate(it: Long): String{
        return SimpleDateFormat("dd.MM.yyyy").format(Date(it))
    }

    private fun getDecimalFormat(it: Double): String{
        return DecimalFormat("#.##").format(it)
    }

}