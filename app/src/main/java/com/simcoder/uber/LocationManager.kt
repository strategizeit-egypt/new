package com.simcoder.uber

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData

class LocationManager(
    val context: Context,
    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
) {
    var locationObserver = MutableLiveData<Location>()

    fun isLocationPermissionGranted(): Boolean {
        return !((ContextCompat.checkSelfPermission(
                    context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED))
    }

    fun askForLocationPermission() {
 /*       ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), Constants.LOCATION_REQUEST_CODE
        )*/
    }

    @SuppressLint("MissingPermission")
    fun getLocationPair(): Location? {
       /// context.isLocationServiceEnabled()
        val providers = locationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l: Location = locationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        locationObserver.value = bestLocation
        Log.e("my llllll", bestLocation.toString())
        return bestLocation
    }
}


/*
    fun showSettingsAlert() {

        val mAlertDialog = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        mAlertDialog.setTitle("Gps Disabled")
        mAlertDialog.setMessage("For a better user experience allow location ?")
        mAlertDialog.setPositiveButton("settings") { dialog, which ->
            val mIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(mIntent)
            dialog.cancel()
        }

        mAlertDialog.setNegativeButton("cancel") { dialog, which ->
            dialog.cancel()
        }

        val mcreateDialog = mAlertDialog.create()
        mcreateDialog.show()
    }}*/
