package bel.ink.bel.belhunt.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import bel.ink.bel.belhunt.App
import bel.ink.bel.belhunt.R
import bel.ink.bel.belhunt.ui.adapters.WeatherListAdapter
import bel.ink.bel.belhunt.viewmodels.WeatherViewModel
import bel.ink.bel.belhunt.weatherPackage.entities.ForecastList
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_start.*
import java.lang.Exception


class WeatherActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherViewModel

    private lateinit var currentLocation: Location
    private var isRequestingLocationUpdates: Boolean = true


    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationContext)
    }
    private val locationRequest: LocationRequest by lazy {
        LocationRequest()
                .setInterval(30000L)
                .setFastestInterval(10000L)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }
    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                for (location in locationResult!!.locations) {
                    progressRegisterWeather.visibility = View.INVISIBLE
                    Toast.makeText(applicationContext, "location updated: " + location.longitude.toString() + "  " + location.latitude.toString(), Toast.LENGTH_SHORT).show()
                    viewModel.setCoordinatesDay(5, lat = location.latitude.toInt(), lonq = location.longitude.toInt())
                    coordinatesTextView.text = "${resources.getString(R.string.weatherByGPS)} ${location.latitude.toString()}- ${location.longitude.toString()} "
                    currentLocation = location

                }
            }
        }
    }

    private val locationManager by lazy { this.getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    private val locationListener by lazy {
        object : android.location.LocationListener {
            override fun onLocationChanged(location: Location) {
                updateLocation(location)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

            }

            override fun onProviderEnabled(provider: String?) {

            }

            override fun onProviderDisabled(provider: String?) {

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        /*requestPermissions()*/

        App.component.inject(this)
        getLastKnowsLocation()

        checkLocationSystemDeviseSettings()

        viewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)


        val forecastList = viewModel.getLive()
        forecastList?.observe(this, Observer { forecastList ->
            onForecastsLoaded(forecastList)

        })

        listStartActivity1.layoutManager = LinearLayoutManager(this)

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10F, locationListener)


    }

    private fun updateLocation(p0: Location) {
        progressRegisterWeather.visibility = View.INVISIBLE
        Toast.makeText(applicationContext, "location updated: " + p0.longitude.toString() + "  " + p0.latitude.toString(), Toast.LENGTH_SHORT).show()
        viewModel.setCoordinatesDay(5, lat = p0.latitude.toInt(), lonq = p0.longitude.toInt())
        coordinatesTextView.text = "${resources.getString(R.string.weatherByGPS)} ${p0.latitude.toString()}- ${p0.longitude.toString()} "
        currentLocation = p0
    }

    fun onForecastsLoaded(forecastList: ForecastList?) {
        if (forecastList != null) {
            listStartActivity1.adapter = WeatherListAdapter(forecastList)
            listStartActivity1.adapter.notifyDataSetChanged()

            listStartActivity1.visibility = View.VISIBLE
            imageGUNS.visibility = View.GONE
        } else {
            listStartActivity1.visibility = View.GONE
            imageGUNS.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
    }

    private fun checkLocationSystemDeviseSettings() {

        val locationSettingsBuilder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .build()

        val client = LocationServices.getSettingsClient(this)
        val taskLocationResponse = client.checkLocationSettings(locationSettingsBuilder)

        taskLocationResponse.addOnSuccessListener {
            coordinatesTextView.text = "Wait, looking for location..."
            progressRegisterWeather.visibility = View.VISIBLE
        }

        taskLocationResponse.addOnFailureListener(this, object : OnFailureListener {
            override fun onFailure(exc: Exception) {
                when ((exc as ApiException).statusCode) {
                    CommonStatusCodes.RESOLUTION_REQUIRED -> {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        val resolvableApiException = exc as ResolvableApiException
                        Toast.makeText(applicationContext, "RESOLUTION_REQUIRED", Toast.LENGTH_SHORT).show()
                        resolvableApiException.startResolutionForResult(this@WeatherActivity, 0)
                        coordinatesTextView.text = "enable GPS is needed"
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        Toast.makeText(applicationContext, "ERROR - SETTINGS_CHANGE_UNAVAILABLE", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        })
    }


    private fun getLastKnowsLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener(this,
                object : OnSuccessListener<Location> {
                    override fun onSuccess(location: Location?) {
                        location?.let { currentLocation = it }
                    }
                })
    }

    override fun onStart() {
        super.onStart()


        if (isRequestingLocationUpdates) {

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        }

    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        outState?.putBoolean("isUpdate", isRequestingLocationUpdates)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    /*  private fun requestPermissions() {

          if ((ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
                          == PackageManager.PERMISSION_GRANTED)) {

          } else {
              ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOC)
          }
      }

      override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
          if (requestCode == REQUEST_FINE_LOC) {
              if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  requestPermissions()
              } else {
                  Toast.makeText(this@WeatherActivity, "PERMISSION REQUIRED!", Toast.LENGTH_LONG).show()
                  finish()
              }
          }
      }*/


}









