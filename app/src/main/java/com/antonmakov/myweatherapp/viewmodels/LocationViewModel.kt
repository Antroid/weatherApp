package com.antonmakov.myweatherapp.viewmodels

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonmakov.myweatherapp.managers.SharedPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.suspendCoroutine


class LocationViewModel: ViewModel() {

    private val addressLiveData: MutableLiveData<Address> = MutableLiveData()
    private var address: Address? = null

    private val gpsLocationAddressLiveData: MutableLiveData<Address> = MutableLiveData()
    private var gpsLocationAddress: Address? = null

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + job)

    companion object{
        private const val SP_CITY_KEY = "SP_CITY_KEY"
        private const val SP_STATE_KEY = "SP_STATE_KEY"
    }

    @Suppress("DEPRECATION")
    private fun Geocoder.getAddress(
        latitude: Double,
        longitude: Double,
        address: (Address?) -> Unit
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getFromLocation(latitude, longitude, 1) { address(it.firstOrNull()) }
            return
        }

        try {
            address(getFromLocation(latitude, longitude, 1)?.firstOrNull())
        } catch(e: Exception) {
            //will catch if there is an internet problem
            address(null)
        }
    }

    fun loadAddress(lat: Double, long: Double, context: Context){
        Geocoder(context, Locale.getDefault())
            .getAddress(lat, long) { address: Address? ->
                if (address != null) {
                    val state = address.adminArea
                    val city = address.locality ?: address.subLocality
                    gpsLocationAddress = address
                    gpsLocationAddressLiveData.postValue(gpsLocationAddress)
                    loadAddress(city, state, context)
                }
            }
    }

    fun getGPSAddress() = gpsLocationAddressLiveData

    fun loadAddress(city: String, state: String, context: Context) {
        coroutineScope.launch {
            SharedPreferencesManager.saveString(SP_STATE_KEY, state)
            SharedPreferencesManager.saveString(SP_CITY_KEY, city)
            Geocoder(context).getAddress("$city, $state")
        }
    }

    fun getSavedCity(): String?{
        return SharedPreferencesManager.getString(SP_CITY_KEY, null)
    }

    fun getSavedState(): String?{
        return SharedPreferencesManager.getString(SP_STATE_KEY, null)
    }

    private suspend fun Geocoder.getAddress(
        locationName: String
    ): Address? = withContext(Dispatchers.IO) {
        suspendCoroutine { cont ->
            @Suppress("DEPRECATION")
            address = getFromLocationName(locationName, 1)?.firstOrNull()
            addressLiveData.postValue(address)
        }
    }



    fun getAddress(): LiveData<Address> = addressLiveData
}