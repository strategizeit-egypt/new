package com.simcoder.uber.agora.driver

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.IBinder
import com.simcoder.MeshwarApplication
import com.terlive.core.misc.OneParamFunction
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * Gps location tracker class
 * to get users location and other information related to location
 */
class GpsTracker11 : Service(), LocationListener {

    private val mContext: Context = MeshwarApplication.instance!!
    private var callDialog = false
    private var isGpsEnabled = false
    private var isNetworkEnabled = false
    private var canGetLocation = false
    private var mLocation: Location? = null
    private var mLatitude: Double = 0.toDouble()
    private var mLongitude: Double = 0.toDouble()
    private var mLocationManager: android.location.LocationManager? = null
    var locationObserver: OneParamFunction<Location>? = null
    val locationPublisher: BehaviorSubject<Location> = BehaviorSubject.create()
    private val compositeDisposable by lazy { CompositeDisposable() }


    /**
     * @return location
     */
    /*getting status of the gps*//*getting status of network provider*//*no location provider enabled*//*getting location from network provider*//*if gps is enabled then get location using gps*/
    var location: Location? = null
        @SuppressLint("ServiceCast", "MissingPermission")
        get() {

            try {

                mLocationManager =
                    mContext.getSystemService(LOCATION_SERVICE) as android.location.LocationManager?
                isGpsEnabled =
                    mLocationManager!!.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
                isNetworkEnabled =
                    mLocationManager!!.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)

                if (isGpsEnabled || isNetworkEnabled) {

                    this.canGetLocation = true
                    if (isNetworkEnabled) {

                        mLocationManager!!.requestLocationUpdates(
                            android.location.LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_FOR_UPDATE,
                            MIN_DISTANCE_CHANGE_FOR_UPDATE,
                            this@GpsTracker11
                        )

                        if (mLocationManager != null) {

                            mLocation =
                                mLocationManager!!.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)

                            if (mLocation != null) {

                                mLatitude = mLocation!!.latitude

                                mLongitude = mLocation!!.longitude
                                locationObserver?.invoke(mLocation!!)
                            }
                        }
                        if (isGpsEnabled) {

                            if (mLocation == null) {

                                mLocationManager!!.requestLocationUpdates(
                                    android.location.LocationManager.GPS_PROVIDER,
                                    MIN_TIME_FOR_UPDATE,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATE,
                                    this@GpsTracker11
                                )

                                if (mLocationManager != null) {

                                    mLocation =
                                        mLocationManager!!.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)

                                    if (mLocation != null) {
                                        mLatitude = mLocation!!.latitude
                                        mLongitude = mLocation!!.longitude
                                        locationObserver?.invoke(mLocation!!)
                                    }

                                }
                            }

                        }
                    }

                    mLocation?.let {
                        locationObserver?.invoke(mLocation!!)
                    } ?: run {
                        compositeDisposable.add(
                            Observable.timer(1000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                location
                            }, {}))
                    }
                } else {
                    /* if (callDialog)
                         showSettingsAlert(MamohApplication.instance!!)*/
                }


            } catch (e: Exception) {
                //  locationObserver.value = null
                e.printStackTrace()
            }

            return mLocation
        }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    val latitude: Double
        get() {

            if (mLocation != null) {

                mLatitude = mLocation!!.getLatitude()
            }
            return mLatitude
        }


    val longitude: Double
        get() {

            if (mLocation != null) {

                mLongitude = mLocation!!.getLongitude()

            }

            return mLongitude
        }

    init {
        location
    }

    /**
     * call this function to stop using gps in your application
     */
    fun stopUsingGps() {
        if (mLocationManager != null) {
            mLocationManager!!.removeUpdates(this@GpsTracker11)
            this.onDestroy()
        }
    }

    fun canGetLocation(): Boolean {

        return this.canGetLocation
    }


    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {
        this.location = location
        locationPublisher.onNext(location)
//        if (locationObserver.value == null)
        locationObserver?.invoke(location)
    }

    fun getLocationObserver(): Flowable<Location> {
        return locationPublisher.toFlowable(BackpressureStrategy.LATEST)
    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    companion object {

        /**
         * min distance change to get location update
         */
        private val MIN_DISTANCE_CHANGE_FOR_UPDATE: Float = 50f

        /**
         * min time for location update
         * 60000 = 1min
         */
        private val MIN_TIME_FOR_UPDATE: Long = 30000
    }

}