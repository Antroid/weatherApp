package com.antonmakov.myweatherapp.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.antonmakov.myweatherapp.Consts
import com.antonmakov.myweatherapp.R
import com.antonmakov.myweatherapp.viewmodels.LocationViewModel
import com.antonmakov.myweatherapp.viewmodels.WeatherViewModel


class WeatherFragment : Fragment() {
    var searchButton: Button? = null
    var state: AutoCompleteTextView? = null
    var city: EditText? = null
    var locationViewModel: LocationViewModel? = null
    var weatherViewModel: WeatherViewModel? = null
    var weatherImage: ImageView? = null
    var locationTemperature: TextView? = null
    var mainDesc: TextView? = null
    var moreDesc: TextView? = null
    var errorDesc: TextView? = null
    var locationBtn: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        val view = inflater.inflate(R.layout.weather_fragment, container, false)
        state = view.findViewById(R.id.state)
        city = view.findViewById(R.id.city)
        searchButton = view.findViewById(R.id.search_location)

        weatherImage = view.findViewById(R.id.weather_icon)
        locationTemperature = view.findViewById(R.id.location_temperature)
        mainDesc = view.findViewById(R.id.main_desc)
        moreDesc = view.findViewById(R.id.more_desc)
        errorDesc = view.findViewById(R.id.error_desc)
        locationBtn = view.findViewById(R.id.location_btn)

        locationBtn?.setOnClickListener{

            activity?.requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), Consts.LOCATION_PERMISSION_REQUEST)
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.states)
        )
        state?.setAdapter(adapter)

        searchButton?.setOnClickListener {
            val cityStr = city?.text.toString()
            val stateStr = state?.text.toString()
            locationViewModel?.loadAddress(cityStr, stateStr, requireActivity())
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationViewModel = ViewModelProvider(requireActivity())[LocationViewModel::class.java]
        weatherViewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]

        locationViewModel?.getAddress()?.observe(viewLifecycleOwner) {
            weatherViewModel?.loadWeather(it?.latitude, it?.longitude, requireContext())
        }

        weatherViewModel?.getWeather()?.observe(viewLifecycleOwner) { weather ->
            weather?.let {
                locationTemperature?.text = getString(R.string.temp_format, Math.round(it.main.temp).toString())
                if(it.weather.isNotEmpty()) {
                    it.weather[0].apply {
                        weatherViewModel?.loadWeatherImage(icon)
                        mainDesc?.text = main
                        moreDesc?.text = description
                    }
                }
            }
            if(weather == null) {
                Toast.makeText(requireContext(), getString(R.string.please_check_location_and_try_again), Toast.LENGTH_LONG).show()
            }
        }

        locationViewModel?.getGPSAddress()?.observe(viewLifecycleOwner){
            val gpsState = it.adminArea
            val gpsCity = it.locality ?: it.subLocality
            city?.setText(gpsCity)
            state?.setText(gpsState)

        }

        weatherViewModel?.getWeatherIcon()?.observe(viewLifecycleOwner) {
            weatherImage?.setImageBitmap(it)
        }

        val savedCity = locationViewModel?.getSavedCity()
        val savedState = locationViewModel?.getSavedState()

        if(savedState != null && savedCity != null) {
            city?.setText(savedCity)
            state?.setText(savedState)
            locationViewModel?.loadAddress(savedCity, savedState, requireContext())
        }


    }





}