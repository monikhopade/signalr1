package com.example.signalr1

import android.os.Looper
import io.flutter.plugin.common.MethodChannel.Result
import microsoft.aspnet.signalr.client.*
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent
import microsoft.aspnet.signalr.client.hubs.HubConnection
import microsoft.aspnet.signalr.client.hubs.HubProxy
import microsoft.aspnet.signalr.client.transport.LongPollingTransport
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport

enum class CallMethod1(val value: String) {
    ConnectToServer1("connectToServer1"),
    Reconnect1("reconnect1"),
    Stop1("stop1"),
    ListenToHubMethod1("listenToHubMethod1"),
    InvokeServerMethod1("invokeServerMethod1")
}

object SignalR1 {
    private lateinit var connection1: HubConnection
    private lateinit var hub1: HubProxy

    fun connectToServer(url: String, hubName: String, queryString: String, headers: Map<String, String>, transport: Int, hubMethods: List<String>, result: Result) {
        try {
            connection1 = if (queryString.isEmpty()) {
                HubConnection(url)
            } else {
                HubConnection(url, queryString, true, Logger { _: String, _: LogLevel ->
                })
            }

            if (headers.isNotEmpty()) {
                val cred = Credentials() { request ->
                    request.headers = headers
                }
                connection1.credentials = cred
            }
            hub1 = connection1.createHubProxy(hubName)

            hubMethods.forEach { methodName ->
                hub1.on(methodName, { res ->
                    android.os.Handler(Looper.getMainLooper()).post {
                        Signalr1Plugin.channel1.invokeMethod("NewMessage", listOf(methodName, res))
                    }
                }, Any::class.java)
            }

            connection1.connected {
                android.os.Handler(Looper.getMainLooper()).post {
                    Signalr1Plugin.channel1.invokeMethod("Connection1Status", connection1.state.name)
                }
            }

            connection1.reconnected {
                android.os.Handler(Looper.getMainLooper()).post {
                    Signalr1Plugin.channel1.invokeMethod("Connection1Status", connection1.state.name)
                }
            }

            connection1.reconnecting {
                android.os.Handler(Looper.getMainLooper()).post {
                    Signalr1Plugin.channel1.invokeMethod("Connection1Status", connection1.state.name)
                }
            }

            connection1.closed {
                android.os.Handler(Looper.getMainLooper()).post {
                    Signalr1Plugin.channel1.invokeMethod("ConnectionStatus", connection1.state.name)
                }
            }

            connection1.connectionSlow {
                android.os.Handler(Looper.getMainLooper()).post {
                    Signalr1Plugin.channel1.invokeMethod("Connection1Status", "Slow")
                }
            }

            connection1.error { handler ->
                android.os.Handler(Looper.getMainLooper()).post {
                    Signalr1Plugin.channel1.invokeMethod("Connection1Status", handler.localizedMessage)
                }
            }

            when (transport) {
                1 -> connection1.start(ServerSentEventsTransport(connection1.logger))
                2 -> connection1.start(LongPollingTransport(connection1.logger))
                else -> {
                    connection1.start()
                }
            }

            result.success(true)
        } catch (ex: Exception) {
            result.error("Error", ex.localizedMessage, null)
        }
    }

    fun connectToServer1(url: String, hubName: String, queryString: String, headers: Map<String, String>, transport: Int, hubMethods: List<String>, result: Result) {
        try {
            connection1 = if (queryString.isEmpty()) {
                HubConnection(url)
            } else {
                HubConnection(url, queryString, true, Logger { _: String, _: LogLevel ->
                })
            }

            if (headers.isNotEmpty()) {
                val cred = Credentials() { request ->
                    request.headers = headers
                }
                connection1.credentials = cred
            }
            hub1 = connection1.createHubProxy(hubName)

            hubMethods.forEach { methodName ->
                hub1.on(methodName, { res ->
                    android.os.Handler(Looper.getMainLooper()).post {
                        Signalr1Plugin.channel1.invokeMethod("NewMessage", listOf(methodName, res))
                    }
                }, Any::class.java)
            }

            connection1.connected {
                android.os.Handler(Looper.getMainLooper()).post {
                    Signalr1Plugin.channel1.invokeMethod("Connection1Status", connection1.state.name)
                }
            }

            connection1.reconnected {
                android.os.Handler(Looper.getMainLooper()).post {
                    Signalr1Plugin.channel1.invokeMethod("Connection1Status", connection1.state.name)
                }
            }

            connection1.reconnecting {
                android.os.Handler(Looper.getMainLooper()).post {
                    Signalr1Plugin.channel1.invokeMethod("Connection1Status", connection1.state.name)
                }
            }

            connection1.closed {
                android.os.Handler(Looper.getMainLooper()).post {
                    Signalr1Plugin.channel1.invokeMethod("Connection1Status", connection1.state.name)
                }
            }

            connection1.connectionSlow {
                android.os.Handler(Looper.getMainLooper()).post {
                    Signalr1Plugin.channel1.invokeMethod("ConnectionStatus", "Slow")
                }
            }

            connection1.error { handler ->
                android.os.Handler(Looper.getMainLooper()).post {
                    Signalr1Plugin.channel1.invokeMethod("Connection1Status", handler.localizedMessage)
                }
            }

            when (transport) {
                1 -> connection1.start(ServerSentEventsTransport(connection1.logger))
                2 -> connection1.start(LongPollingTransport(connection1.logger))
                else -> {
                    connection1.start()
                }
            }

            result.success(true)
        } catch (ex: Exception) {
            result.error("Error", ex.localizedMessage, null)
        }
    }

    fun reconnect1(result: Result) {
        try {
            connection1.start()
        } catch (ex: Exception) {
            result.error(ex.localizedMessage, ex.stackTrace.toString(), null)
        }
    }

    fun stop1(result: Result) {
        try {
            connection1.stop()
        } catch (ex: Exception) {
            result.error(ex.localizedMessage, ex.stackTrace.toString(), null)
        }
    }

    fun listenToHubMethod1(methodName: String, result: Result) {
        try {
            hub1.on(methodName, { res ->
                android.os.Handler(Looper.getMainLooper()).post {
                    Signalr1Plugin.channel1.invokeMethod("NewMessage", listOf(methodName, res))
                }
            }, Any::class.java)
        } catch (ex: Exception) {
            result.error("Error", ex.localizedMessage, null)
        }
    }

    fun invokeServerMethod1(methodName: String, args: List<Any>, result: Result) {
        try {
            val res: SignalRFuture<Any> = hub1.invoke(Any::class.java, methodName, *args.toTypedArray())

            res.done { msg: Any? ->
                android.os.Handler(Looper.getMainLooper()).post {
                    result.success(msg)
                }
            }

            res.onError { throwable ->
                android.os.Handler(Looper.getMainLooper()).post {
                    result.error("Error", throwable.localizedMessage, null)
                }
            }
        } catch (ex: Exception) {
            result.error("Error", ex.localizedMessage, null)
        }
    }
}