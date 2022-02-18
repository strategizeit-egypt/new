package com.simcoder.uber.agora.message

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.simcoder.MeshwarApplication
import com.simcoder.uber.agora.message.data.model.SessionMessageModel
import com.simcoder.uber.agora.message.data.source.ChatLogin
import io.agora.rtm.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject
import kotlin.concurrent.thread

class ChatRoom @Inject constructor(private val login: ChatLogin, private val context: Context) {
    private val hasMessage: BehaviorSubject<SessionMessageModel> =
        BehaviorSubject.create()

    private val memberJoinedStatus: BehaviorSubject<Pair<String, Boolean>> =
        BehaviorSubject.create()


    private var mRtmChannel: RtmChannel? = null
    var memberCount = 1
        private set

/**
     * API CALL: get channel member list*/


    fun getMemberList(): Single<List<String>> {
        return Single.create { emitter ->
            if (mRtmChannel == null) {
                emitter.onError(Throwable("mRtmChannel is null "))
            } else {
                mRtmChannel?.getMembers(object : ResultCallback<List<RtmChannelMember?>> {
                    override fun onSuccess(responseInfo: List<RtmChannelMember?>) {
                        val names = mutableListOf<String>()
                        responseInfo.forEach {
                            if (it != null) {
                                Timber.e("ChatRoom   getChannelMemberList forEach   ${it.userId}    ")
                                names.add(it.userId)
                            }
                        }
                        memberCount = responseInfo.size
                        emitter.onSuccess(names)
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        Timber.d("ChatRoom getChannelMemberList onFailure   ${errorInfo.errorDescription}")
                        emitter.onError(Throwable(errorInfo.errorDescription))
                    }
                })
            }
        }

    }

/**
     * API CALL: create and join channel*/


    fun createChannel(roomName: String = "TestMM"): Single<Boolean> {
        Log.e("ChatRoom", "createAndJoinChannel   $roomName")
        val singleSource: Single<Boolean> =  Single.create { emitter ->
            // step 1: create a channel instance
            mRtmChannel = login.createChannel(roomName, MyChannelListener())
            if (mRtmChannel == null) {
                emitter.onError(Throwable("mRtmChannel is null "))
            } else {
                // step 2: join the channel
                mRtmChannel?.join(object : ResultCallback<Void?> {
                    override fun onSuccess(responseInfo: Void?) {
                        emitter.onSuccess(true)
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        Timber.e("ChatRoom   createAndJoinChannel   ${errorInfo.errorDescription}")
                        emitter.onError(Throwable(errorInfo.errorDescription))
                    }
                })
            }
        }

        return singleSource.retry(2) {
            if (mRtmChannel == null) false
            else { it. message == RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_TIMEOUT.toString() }
        }
    }
/*
*
     * API CALL: leave and release channel*/


    fun leaveChannel(): Single<Boolean> = Single.fromCallable {
        thread(true, block = { mRtmChannel?.leave(null) })
        thread(true, block = { mRtmChannel?.release() })
        mRtmChannel = null
        true
    }


/**
     * API CALL: send message to a channel*/


    fun sendMessage(messageData: String): Single<Boolean> {
        val singleSource: Single<Boolean> = Single.create { emitter ->
            // step 1: create a message
            val message = login.createMessage()
            message.text = messageData
            // step 2: send message to channel
        //    Toast.makeText(MeshwarApplication.instance!!, "sent ${message.text}", Toast.LENGTH_LONG ).show()

            Timber.e("ChatRoom    sendChan"+ "$messageData")
            if (mRtmChannel == null) {
                emitter.onError(Throwable("mRtmChannel is null or message is null"))
            } else {
                mRtmChannel?.sendMessage(message, object : ResultCallback<Void?> {
                    override fun onSuccess(aVoid: Void?) {
                        emitter.onSuccess(true)
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        //  when ( errorInfo.errorCode) {
                        //    RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_TIMEOUT, RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_FAILURE
                        // }
                        Timber.e("ChatRoom sendChannelM"+   "${errorInfo.errorDescription}")
                        emitter.onError(Throwable(errorInfo.errorCode.toString()))
                    }
                })
            }
        }

        return singleSource.retry(3) {
            if (mRtmChannel == null) false else it.shouldApplyRetry()

        }
    }

    private fun Throwable.shouldApplyRetry(): Boolean {
     //   return if (context.isNetworkConnected()) {
          return  (message == RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_TIMEOUT.toString() ||
                    message == RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_FAILURE.toString())
      //  } else false

    }

    fun hasMessage(): Flowable<SessionMessageModel> =
        hasMessage.toFlowable(BackpressureStrategy.LATEST)

    fun getMemberJoinedStatus(): Flowable<Pair<String, Boolean>> =
        memberJoinedStatus.toFlowable(BackpressureStrategy.LATEST)

    fun getUserConnectionStatus(): Flowable<Int> =
        login.connectionPublisher.toFlowable(BackpressureStrategy.LATEST)

    fun getUserStatus(userID: String): Flowable<Int> {
        login.observeOnUserStatus(userID)
        return login.userStatusPublisher.toFlowable(BackpressureStrategy.LATEST)
            .filter { it.keys.contains(userID) }
            .map { it.values.first() }
    }

    fun isUserOnline(userId: String): Single<Boolean> {
        return login.isUserConnected(userId)
    }

/**
     * API CALLBACK: rtm channel event listener*/


    private inner class MyChannelListener : RtmChannelListener {
        override fun onMemberCountUpdated(i: Int) {
            Timber.d("ChatRoom onMemberCountU $i")
        }

        override fun onAttributesUpdated(list: List<RtmChannelAttribute>) {
            Timber.e("ChatRoom onAttributes    ${list.toString()}")
        }

        override fun onMessageReceived(message: RtmMessage, fromMember: RtmChannelMember) {
            //  val userInfo = login.decodeUserName(fromMember.userId)
            hasMessage.onNext(
                SessionMessageModel(
                    message = message.text,
                    userId = fromMember.userId
                )
            )
         //   Toast.makeText(MeshwarApplication.instance!!,  message.text, Toast.LENGTH_LONG ).show()
            Timber.e("testMessage"+ message.text +" "+ fromMember.userId)
            Timber.e("ChatRoom onMessageRe"+    "${message.text}  -- ${fromMember.userId}  -- ${fromMember.channelId}")
        }

        override fun onImageMessageReceived(p0: RtmImageMessage?, p1: RtmChannelMember?) {
        }

        override fun onFileMessageReceived(p0: RtmFileMessage?, p1: RtmChannelMember?) {
        }

        override fun onMemberJoined(member: RtmChannelMember) {
            memberCount++
            //     val userName = login.decodeUserName(member.userId)
            memberJoinedStatus.onNext(Pair(member.userId, true))
            Timber.e("ChatRoom onMemberJoined"+   member.userId)
        }

        override fun onMemberLeft(member: RtmChannelMember) {
            memberCount--
            //  val userName = login.decodeUserName(member.userId)
            memberJoinedStatus.onNext(Pair(member.userId, false))
            Timber.e("ChatRoom onMemberLeft" + member.userId)
        }

    }


}
