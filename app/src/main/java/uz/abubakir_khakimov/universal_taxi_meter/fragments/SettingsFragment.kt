package uz.abubakir_khakimov.universal_taxi_meter.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.orhanobut.hawk.Hawk
import uz.abubakir_khakimov.universal_taxi_meter.R
import uz.abubakir_khakimov.universal_taxi_meter.databinding.FragmentSettingsBinding
import uz.abubakir_khakimov.universal_taxi_meter.models.Settings
import java.text.DecimalFormat

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var currencyAdapter: ArrayAdapter<String>
    private lateinit var settings: Settings

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

        currencyAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, arrayListOf("USD", "RUB", "UZS"))
        binding.currency.setAdapter(currencyAdapter)

        binding.currency.setOnItemClickListener { parent, view, position, id ->
            settings.currency = (view as TextView).text.toString()
        }

        binding.backStack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.save.setOnClickListener {
            settings.distanceMinimalka = binding.distanceMinimalka.text.toString().toDouble()
            settings.priceMinimalka = binding.priceMinimalka.text.toString().toDouble()
            settings.pricePerKm = binding.pricePerKm.text.toString().toDouble()
            Hawk.put("appSettings", settings)
            Toast.makeText(requireActivity(), "Successfully saved!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

    }

    private fun initUI(){
        settings = Hawk.get("appSettings", Settings("UZS", 2.0, 3000.0, 2000.0))
        binding.currency.setText(settings.currency, false)
        binding.distanceMinimalka.setText(settings.distanceMinimalka.toString())
        binding.priceMinimalka.setText(settings.priceMinimalka.toString())
        binding.pricePerKm.setText(settings.pricePerKm.toString())
    }

}