package com.simcoder.uber.agora.driver.data

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import com.simcoder.uber.*
import com.simcoder.uber.agora.driver.GpsTracker11
import com.simcoder.uber.agora.driver.di.AppModule
import com.simcoder.uber.agora.message.ChatRoom
import com.simcoder.uber.agora.message.data.model.MessageAction
import com.simcoder.uber.agora.message.data.model.MessageModel
import com.simcoder.uber.agora.message.data.source.ChatLogin
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class DriverRepo  constructor(
    private val chatLogin: ChatLogin,
    private val chatRoom: ChatRoom,
    private val gps: GpsTracker11
) {
    val mapsApiServices = AppModule.provideMapApi()
    val gson = Gson()

 /*   private val chatLogin by lazy { ChatLogin(TerliveApplication.instance!!) }
    private val chatRoom by lazy { ChatRoom(chatLogin, TerliveApplication.instance!!) }*/

    fun connectWithCurrentTrip(userID: String/*connectTripParam: ConnectTripParam*/, token: String): Single<Boolean> {
     //  return Single.just(true)
        return chatLogin.login(userID, token).flatMap {
            chatRoom.createChannel(/*connectTripParam.tripId*/ "MeshwarTest")
        }
    }

    fun send (msg:String):Single<Boolean>{
Timber.d("mmmmmm $msg")
       return chatRoom.sendMessage(msg)
    }
    fun startObservingLocation()/*:Flowable<Long> */{
 /*       Timber.d("GGGGGGGertIn")
        return  Flowable.interval(0L, 10, TimeUnit.SECONDS)
            .doOnNext {
                Timber.d("GGGGGGGertInNext")

            }*/

        gps.location
        gps.locationObserver = {
            chatRoom.sendMessage(it.toString())
                .subscribeOn(Schedulers.io())
                .subscribe({}, {})
        }
    }

   /* fun observeOnBusLocation(): Flowable<String> {
    //    return Flowable.just("true")
        return chatRoom.hasMessage().map {
            Timber.d("currrrrrMsg $it")
            it.message
        }
    }*/


    fun observeOnBusLocation(): Flowable<MessageModel> {
       // return Flowable.just(BusLocationParam(0.0,0.0))
               return chatRoom.hasMessage().map { it.message.toObject<MessageModel>() }
    }

    fun observeOnEndTrip(): Flowable<Boolean> {
     //   return Flowable.just(true)
        return chatRoom.hasMessage().map { it.message.toObject<MessageModel>() }
            .filter {
                it.action == MessageAction.END_TRIP.action
            }.map {
                it.data == "true"
            }
    }



    fun getBusConnectionStatus(driverID: String): Flowable<Boolean> {
    //    return Flowable.just(true)
        return chatRoom.getUserStatus(driverID).map { it == ONLINE }
    }

    fun observeOnConnection() = // Flowable.just(0)
        chatLogin.connectionPublisher.toFlowable(BackpressureStrategy.LATEST)

    fun endObservations(): Single<Boolean> {
        gps.stopUsingGps()
       // return Single.just(true)
        return chatRoom.leaveChannel().flatMap {
            chatLogin.logout()
        }
    }



    fun getPointsList(key: String, location: LatLng, destination: LatLng): Single<MutableList<LatLng>> {

        return mapsApiServices.getPointsList(
            url + location.latitude + "," + location.longitude +
                    "&destination=" + destination.latitude + "," + destination.longitude +
                    "&key=$key"
        )
            .doOnError {
                Timber.d(it)
            }
            .map { response ->
                val directionResults = gson.fromJson(response, DirectionResults::class.java)
                val routeList = ArrayList<LatLng>()
                if (directionResults.routes?.isNotEmpty()!!) {

                    val routeA = directionResults.routes[0]
                    Log.i("zacharia", "Legs length : " + routeA.legs!!.size)
                    if (routeA.legs.isNotEmpty()) {
                        val steps = routeA.legs[0].steps
                        steps?.map {
                            addStepToRoutList(routeList, it)
                        }
                    }
                }
                routeList
            }
    }

    private fun addStepToRoutList(routeList: ArrayList<LatLng>, it: Steps) {
        val decodeList: ArrayList<LatLng>
        var locationResponse: LocationResponse? = it.start_location
        val polyline: String? = it.polyline!!.points
        routeList.add(LatLng(locationResponse!!.lat, locationResponse.lng))
        decodeList = PolyUtil.decode(polyline) as ArrayList<LatLng>
        routeList.addAll(decodeList)
        locationResponse = it.end_location
        routeList.add(LatLng(locationResponse!!.lat, locationResponse.lng))
    }


    companion object {
        private const val ONLINE = 0
        private const val UN_REACHABLE = 1
        private const val OFFLINE = 2


        private const val DISCONNECTED = 1
        private const val CONNECTED = 3
        private const val RECONNECTING = 4
        private const val ABORTED = 5
        private const val url = "https://maps.googleapis.com/maps/api/directions/json?origin="

    }

}

