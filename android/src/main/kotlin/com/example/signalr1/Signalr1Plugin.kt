package com.example.signalr1

import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

/** Signalr1Plugin */
class Signalr1Plugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "SignalR")
    channel.setMethodCallHandler(this)
    channel1 = MethodChannel(flutterPluginBinding.binaryMessenger, "SignalR1")
    channel1.setMethodCallHandler(this)
  }

  companion object {
    lateinit var channel: MethodChannel
    lateinit var channel1: MethodChannel

    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "signalR")
      channel.setMethodCallHandler(Signalr1Plugin())
      val channel1 = MethodChannel(registrar.messenger(), "signalR1")
      channel1.setMethodCallHandler(Signalr1Plugin())
    }
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      CallMethod.ConnectToServer.value -> {
        val arguments = call.arguments as Map<*, *>
        @Suppress("UNCHECKED_CAST")
        SignalR.connectToServer(
                arguments["baseUrl"] as String,
                arguments["hubName"] as String,
                arguments["queryString"] as String,
                arguments["headers"] as? Map<String, String> ?: emptyMap(),
                arguments["transport"] as Int,
                arguments["hubMethods"] as? List<String> ?: emptyList(),
                result)
      }
      CallMethod.Reconnect.value -> {
        SignalR.reconnect(result)
      }
      CallMethod.Stop.value -> {
        SignalR.stop(result)
      }
      CallMethod.ListenToHubMethod.value -> {
        if (call.arguments is String) {
          val methodName = call.arguments as String
          SignalR.listenToHubMethod(methodName, result)
        } else {
          result.error("Error", "Cast to String Failed", "")
        }
      }
      CallMethod.InvokeServerMethod.value -> {
        val arguments = call.arguments as Map<*, *>
        @Suppress("UNCHECKED_CAST")
        SignalR.invokeServerMethod(arguments["methodName"] as String, arguments["arguments"] as? List<Any>
                ?: emptyList(), result)
      }

      CallMethod1.ConnectToServer1.value -> {
        val arguments = call.arguments as Map<*, *>
        @Suppress("UNCHECKED_CAST")
        SignalR1.connectToServer1(
                arguments["baseUrl"] as String,
                arguments["hubName"] as String,
                arguments["queryString"] as String,
                arguments["headers"] as? Map<String, String> ?: emptyMap(),
                arguments["transport"] as Int,
                arguments["hubMethods"] as? List<String> ?: emptyList(),
                result)
      }
      CallMethod1.Reconnect1.value -> {
        SignalR1.reconnect1(result)
      }
      CallMethod1.Stop1.value -> {
        SignalR1.stop1(result)
      }
      CallMethod1.ListenToHubMethod1.value -> {
        if (call.arguments is String) {
          val methodName = call.arguments as String
          SignalR1.listenToHubMethod1(methodName, result)
        } else {
          result.error("Error", "Cast to String Failed", "")
        }
      }
      CallMethod1.InvokeServerMethod1.value -> {
        val arguments = call.arguments as Map<*, *>
        @Suppress("UNCHECKED_CAST")
        SignalR1.invokeServerMethod1(arguments["methodName"] as String, arguments["arguments"] as? List<Any>
                ?: emptyList(), result)
      }

      else -> {
        result.notImplemented()
      }
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    channel1.setMethodCallHandler(null)
  }
}
