package com.simcoder.uber.agora.message.data.source

import android.content.Context
import android.util.Log
import com.simcoder.uber.BuildConfig
import com.simcoder.uber.agora.io.agora.rtm.RtmTokenBuilder
import io.agora.rtm.*
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ChatLogin  constructor(context: Context) : RtmClientListener {

    private val mChatManager: ChatManager =
        ChatManager(context)
    private lateinit var mRtmClient: RtmClient

    val connectionPublisher: BehaviorSubject<Int> =
        BehaviorSubject.create()

    val userStatusPublisher: BehaviorSubject<MutableMap<String, Int>> =
        BehaviorSubject.create()

    fun login(userId: String, rtmToken: String ): Single<Boolean> {
        Timber.d("ChatRoom ChatLogin login   $userId")
        return mChatManager.init(this).flatMap {
            mRtmClient = it
           // Single.just(true)
            logoutRTM()
        }.flatMap {
            loginRTM(userID = userId, rtmToken = rtmToken)/*"006ca044a98edf54a4ab0412d92778ab10cIADTUWhhhDfrCamfWU251hRteeuDNDIoktgrCHVK6vKmZ20o2PoAAAAAEAA8W7JH4+MPYgEA6APj4w9i*/
        }
    }



    fun logout(): Single<Boolean> {
        return logoutRTM()
            .doFinally { mChatManager.unregisterListener() }
    }



    private fun loginRTM(userID: String, rtmToken: String): Single<Boolean> {
        Timber.d("ChatRoom ChatLogin loginRTM   $userID    ")
        return Single.create<Boolean> { emitter ->
            mRtmClient.login(rtmToken, userID, object : ResultCallback<Void?> {
                override fun onSuccess(responseInfo: Void?) {
                    emitter.onSuccess(true)
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    Timber.d("ChatRoom ChatLogin loginRTM   ${errorInfo.errorDescription}")
                    emitter.onError(Throwable(errorInfo.errorDescription))
                }
            })
        }
    }

    private fun logoutRTM(): Single<Boolean> {
        return Single.fromCallable<Boolean> {
            mRtmClient.logout(null)
            Timber.d("ChatRoom logoutRTM")
            true
        }.timeout(2, TimeUnit.SECONDS)
            .onErrorReturn {
                Timber.d("ChatRoom logoutRTM  Error -> $it")
                false
            }
    }


    fun createChannel(roomName: String, rtmChannelListener: RtmChannelListener): RtmChannel? {
        return try {

        mRtmClient.createChannel(roomName, rtmChannelListener)
        }catch (ex :Exception){
            Log.e("Logiiiiiin channel", ex.toString())
            null
        }
    }

    fun createMessage(): RtmMessage {
        return mRtmClient.createMessage()
    }

    fun observeOnUserStatus(educatorId: String) =
        mRtmClient.subscribePeersOnlineStatus(mutableSetOf(educatorId), null)

    fun isUserConnected(userId: String): Single<Boolean> {
        return Single.create<Boolean> { emmiter ->
            mRtmClient.queryPeersOnlineStatus(mutableSetOf(userId), object :
                ResultCallback<MutableMap<String, Boolean>> {
                override fun onSuccess(p0: MutableMap<String, Boolean>?) {
                    val isOnline = p0?.get(userId) ?: false
                    emmiter.onSuccess(isOnline)
                }

                override fun onFailure(p0: ErrorInfo?) {
                    emmiter.onSuccess(false)
                }
            })
        }
    }



    override fun onConnectionStateChanged(state: Int, reason: Int) {
        Timber.d("ChatRoom onConnectionStateChanged  state $state  -  reason   $reason")
        when (state) {
            RtmStatusCode.ConnectionState.CONNECTION_STATE_CONNECTED,
            RtmStatusCode.ConnectionState.CONNECTION_STATE_DISCONNECTED,
            RtmStatusCode.ConnectionState.CONNECTION_STATE_RECONNECTING,
            RtmStatusCode.ConnectionState.CONNECTION_STATE_ABORTED -> {
                connectionPublisher.onNext(state)
            }
        }

    }

    override fun onMessageReceived(message: RtmMessage, peerId: String) {
        //val content = message.text
        Timber.d("ChatRoom ChatLogin onMessageReceived  ${message.text}  -    $peerId")
    }

    override fun onImageMessageReceivedFromPeer(p0: RtmImageMessage?, p1: String?) {

    }

    override fun onFileMessageReceivedFromPeer(p0: RtmFileMessage?, p1: String?) {
    }

    override fun onMediaUploadingProgress(p0: RtmMediaOperationProgress?, p1: Long) {
    }

    override fun onMediaDownloadingProgress(p0: RtmMediaOperationProgress?, p1: Long) {
    }

    override fun onTokenExpired() {
        Timber.d("ChatRoom ChatLogin onTokenExpired  ")
    }

    override fun onPeersOnlineStatusChanged(p0: MutableMap<String, Int>?) {
        p0?.let { userStatusPublisher.onNext(it) }
        Timber.d("ChatRoom ChatLogin onPeersOnlineStatusChanged  ")
    }


}
