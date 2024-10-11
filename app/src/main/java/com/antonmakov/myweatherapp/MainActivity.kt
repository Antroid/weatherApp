package com.antonmakov.myweatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.antonmakov.myweatherapp.fragments.WeatherFragment
import com.antonmakov.myweatherapp.viewmodels.LocationViewModel
import com.antonmakov.myweatherapp.viewmodels.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.snackbar.Snackbar
import java.util.Locale


class MainActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {

    var locationViewModel: LocationViewModel? = null
    var weatherViewModel: WeatherViewModel? = null

    companion object{
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]


        supportFragmentManager.addOnBackStackChangedListener(this)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, WeatherFragment())
            .commit()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Consts.LOCATION_PERMISSION_REQUEST -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    requestCurrentLocation()
                    // permission was granted
                    // here request to get location
                } else {
                    val snackbar = Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.location_permission_denied),
                        Snackbar.LENGTH_LONG
                    ).setAction(getString(R.string.settings)) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + applicationInfo.packageName)
                            )
                        )
                    }
                    snackbar.show()
                }
                return
            }
        }
    }



    private fun requestCurrentLocation() {
        // Request permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

                override fun isCancellationRequested() = false
            })
                .addOnSuccessListener { location: Location? ->
                    if (location == null)
                        Toast.makeText(this, getString(R.string.cannot_get_location), Toast.LENGTH_SHORT).show()
                    else {
                        val lat = location.latitude
                        val lon = location.longitude
                        locationViewModel?.loadAddress(lat,lon, this)
                    }

                }

        }
    }

    override fun onStop() {
        super.onStop()
        // Cancels location request (if in flight).
        cancellationTokenSource.cancel()
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    private var cancellationTokenSource = CancellationTokenSource()

    override fun onBackStackChanged() {
        supportActionBar?.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 0)
    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return true
    }

}
