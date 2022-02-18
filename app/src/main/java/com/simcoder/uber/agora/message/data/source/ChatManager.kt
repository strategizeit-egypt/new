package com.simcoder.uber.agora.message.data.source

import android.content.Context
import com.simcoder.uber.BuildConfig
import io.agora.rtm.*
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class ChatManager @Inject constructor(private val mContext: Context) {


    private var mListener: RtmClientListener? = null

    fun init(listener: RtmClientListener?): Single<RtmClient> = Single.fromCallable {
        mListener = listener
        try {
            RtmClient.createInstance(
                mContext,
                BuildConfig.AGORA_APP_ID,
                object : RtmClientListener {
                    override fun onConnectionStateChanged(state: Int, reason: Int) {
                        mListener?.onConnectionStateChanged(state, reason)
                        Timber.d("ChatRoom   ChatManager onConnectionStateChanged    $state   - $reason")
                    }

                    override fun onMessageReceived(
                        rtmMessage: RtmMessage,
                        peerId: String
                    ) {
                        mListener?.onMessageReceived(rtmMessage, peerId)
                        Timber.d("ChatRoom  ChatManager  onMessageReceived    $rtmMessage")
                    }

                    override fun onImageMessageReceivedFromPeer(
                        p0: RtmImageMessage?,
                        p1: String?
                    ) {
                    }

                    override fun onFileMessageReceivedFromPeer(p0: RtmFileMessage?, p1: String?) {}

                    override fun onMediaUploadingProgress(
                        p0: RtmMediaOperationProgress?,
                        p1: Long
                    ) {
                    }

                    override fun onMediaDownloadingProgress(
                        p0: RtmMediaOperationProgress?,
                        p1: Long
                    ) {
                    }

                    override fun onTokenExpired() {
                        mListener?.onTokenExpired()
                        Timber.d("ChatRoom  ChatManager  onTokenExpired  ")
                    }

                    override fun onPeersOnlineStatusChanged(p0: MutableMap<String, Int>?) {
                        mListener?.onPeersOnlineStatusChanged(p0)
                        Timber.d("ChatRoom  ChatManager  onPeersOnlineStatusChanged  $p0")
                    }
                })
            // if (BuildConfig.DEBUG) {
            //   rtmClient?.setParameters("{\"rtm.Timber_filter\": 65535}")
            // }
        } catch (e: Exception) {
            Timber.e(e)
            throw RuntimeException("NEED TO check rtm sdk init fatal error\n$e")
        }
    }

    fun unregisterListener() {
        mListener = null
    }

}
