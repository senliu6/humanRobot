package com.shciri.rosapp.server

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.logging.HttpLoggingInterceptor
import java.security.KeyStore
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * 功能：
 * @author ：liudz
 * 日期：2024年03月15日
 */
object WebSocketManager{
    private var webSocket: WebSocket? = null
    private const val NORMAL_CLOSURE_STATUS = 1000

    fun connect(url: String, listener: WebSocketListener) {
        val trustManager =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManager.init(null as KeyStore?)
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManager.trustManagers, SecureRandom())


        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .sslSocketFactory(
                sslContext.socketFactory,
                trustManager.trustManagers[0] as X509TrustManager
            )
            .build()

        val request = Request.Builder()
            .url(url)
            .method("GET", null) // 设置请求方式为 GET，可以根据需要修改
            .build()

        webSocket = okHttpClient.newWebSocket(request, listener)
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun close() {
        webSocket?.close(NORMAL_CLOSURE_STATUS, "bay")
    }
}