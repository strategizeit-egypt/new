package com.simcoder.uber.agora.driver.presentation.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.simcoder.uber.*
import com.simcoder.uber.agora.driver.data.DriverRepo
import com.simcoder.uber.agora.driver.di.AppModule
import com.simcoder.uber.agora.message.data.model.MessageAction
import com.simcoder.uber.agora.message.data.model.MessageModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class DriverHomeViewModel : BaseViewModel() {

    val repo: DriverRepo = AppModule.provideDriverRepoImp()

    @SuppressLint("StaticFieldLeak")
    private val gps = AppModule.getGps()
    var token: String? = null

    //}
    val endingEvent by lazy { MutableLiveData<Boolean>() }

    /*    val path = mutableListOf<LatLng>().apply {
            add(LatLng(27.962065, 34.362704))
            add(LatLng(27.967305,  34.362942))
            add(LatLng(27.985617,  34.390607))
            add(LatLng(27.989030,  34.395927))
            add(LatLng(28.000747,  34.408668))
            add(LatLng(28.006564, 34.412656))
            add(LatLng(28.009040, 34.417683))
            add(LatLng(28.008582, 34.427860))
        }*/
    val toastEvent = MutableLiveData<String>()
    val currentLocationUpdates = MutableLiveData<BusLocationParam>()
    val tripLocation = MutableLiveData<Pair<BusLocationParam, BusLocationParam>>()


    fun loginAndSendLocation(token: String) {
        repo.connectWithCurrentTrip("driver", token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retry(3)
            .subscribe({
                sendBusLocation()
                observeOnBusStatus()
                toastEvent.value = "Login success"
                /*         Toast.makeText(MeshwarApplication.instance!!, "Login success", Toast.LENGTH_LONG)
                             .show()*/

            }, {}).addTo(compositeDisposable)
    }


    fun loginAndObserveONLocation(token: String) {
        repo.connectWithCurrentTrip("passenger", token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retry(3)
            .subscribe({
                toastEvent.value = "Login Success"
                //    Toast.makeText(MeshwarApplication.instance!!, "Login success", Toast.LENGTH_LONG).show()
                observeBusLocation()
                observeOnBusConnection()

            }, {}).addTo(compositeDisposable)
    }

    private fun observeBusLocation() {
        repo.observeOnBusLocation()
            .retry()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

            .subscribe({
                if (endingEvent.value != true) {
                    if (it.action == MessageAction.LOCATION.action)
                        currentLocationUpdates.value = it.data.toObject<BusLocationParam>()
                    else if (it.action == MessageAction.START_TRIP.action) {
                    /*    tripLocation.value = it.data.toObject<List<BusLocationParam>>().let {
                            it
                            Pair(BusLocationParam(it.first().lat, it.first().lng), BusLocationParam(it[1].lat, it[1].lng))
                        }*/
                        toastEvent.value = "Trip Started"
                    } else if (it.action == MessageAction.END_TRIP.action) {
                        toastEvent.value = "Trip Ended"
                        endingEvent.value = true
                    }

                }
                //     Toast.makeText(MeshwarApplication.instance!!, it, Toast.LENGTH_LONG).show()
            }, {
                toastEvent.value = "observeBusLocation error"
                Log.e("errrrrrr", it.toString())
                /*           Toast.makeText(
                               MeshwarApplication.instance!!,
                               "observeBusLocation error",
                               Toast.LENGTH_LONG
                           ).show()*/
            })
            .addTo(compositeDisposable)
    }

    private fun sendBusLocation() {
        Timber.d("GGGGGGGertIn11")
        gps.location
        gps.locationPublisher.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (endingEvent.value != true) {
                    currentLocationUpdates.value = BusLocationParam(it.latitude, it.longitude)
                    sendM(
                        MessageModel(
                            MessageAction.LOCATION.action,
                            BusLocationParam(it.latitude, it.longitude).toStringData()
                        )
                            .toStringData()
                    )
                }
            }, {})
            .addTo(compositeDisposable)
/*        repo.startObservingLocation().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("GGGGGGGertIn11Subs")

                sendM(path[it.toInt()].toString())
         *//*       repo.send("path[it.toInt()].toString()")
                    .subscribeOn(Schedulers.io())
                    .subscribe({}, {
                        Timber.d("dddd $it")
                    })*//*

            }, {})
            .addTo(compositeDisposable)*/
    }

    fun sendM(msg: String) {
        if (endingEvent.value != true) {
            repo.send(msg)
                .subscribeOn(Schedulers.io())
                .subscribe({}, {
                    Timber.d("dddd $it")
                })
                .addTo(compositeDisposable)
        }
    }

    fun observeOnBusStatus() {
        repo.observeOnConnection()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (endingEvent.value != true) {
                    when (it) {
                        ABORTED -> {
                            toastEvent.value = "Aborted"
                            //Toast.makeText(MeshwarApplication.instance!!, "aborted", Toast.LENGTH_LONG).show()
                            endTrip()
                        }
                        CONNECTED ->
                            toastEvent.value = "Connected"

                        //  Toast.makeText(MeshwarApplication.instance!!, "connected", Toast.LENGTH_LONG).show()
                        DISCONNECTED ->
                            toastEvent.value = "Disconected"

                        //    Toast.makeText(MeshwarApplication.instance!!, "Disconnected", Toast.LENGTH_LONG).show()
                        RECONNECTING ->
                            toastEvent.value = "Reconected"

                        //  Toast.makeText(MeshwarApplication.instance!!, "Reconnected", Toast.LENGTH_LONG).show()
                    }
                }

            }, {})
            .addTo(compositeDisposable)
    }

    fun observeOnBusConnection() {
        repo.getBusConnectionStatus("2882341275")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (endingEvent.value != true) {
                    if (!it)
                        toastEvent.value = "no connection Bus"
                    //      Toast.makeText(MeshwarApplication.instance!!, "no connection Bus", Toast.LENGTH_LONG).show()
                    else
                        toastEvent.value = "no connected  Bus"
                    //      Toast.makeText(MeshwarApplication.instance!!, "connected Bus", Toast.LENGTH_LONG).show()
                }
            }, {})
            .addTo(compositeDisposable)
    }

    fun endTrip() {
        endingEvent.value = true
/*        repo.endObservations()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                endingEvent.value = true
                       },{})
            .addTo(compositeDisposable)*/
    }

    val routeRes = MutableLiveData<MutableList<LatLng>>()
    fun getRouteBetweenToPoints(key: String, location: LatLng, destination: LatLng) {
        repo.getPointsList(key, location, destination)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                routeRes.value = it
            }, {
                Timber.d(it)
            })
            .addTo(compositeDisposable)
    }

    companion object {
        private const val ONLINE = 0
        private const val UN_REACHABLE = 1
        private const val OFFLINE = 2


        private const val DISCONNECTED = 1
        private const val CONNECTED = 3
        private const val RECONNECTING = 4
        private const val ABORTED = 5
    }


}