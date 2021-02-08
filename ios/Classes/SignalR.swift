//
//  SignalR.swift
//  signalR
//
//  Created by Ayon Das on 23/07/20.
//

import Foundation

enum CallMethod : String {
  case connectToServer, reconnect, stop, invokeServerMethod, listenToHubMethod, connectToServer1, reconnect1, stop1, invokeServerMethod1, listenToHubMethod1
}

class SignalRWrapper {

  static let instance = SignalRWrapper()
  static let instance1 = SignalRWrapper()
  private var hub: Hub!
   private var hub1: Hub!
  private var connection: SignalR1!
  private var connection1: SignalR1!

  func connectToServer(baseUrl: String, hubName: String, transport: Int, queryString : String, headers: [String: String], hubMethods: [String], result: @escaping FlutterResult) {
    connection = SignalR(baseUrl)

    if !queryString.isEmpty {
      let qs = queryString.components(separatedBy: "=")
      connection.queryString = [qs[0]:qs[1]]
    }

    if transport == 1 {
      connection.transport = Transport.serverSentEvents
    } else if transport == 2 {
      connection.transport = Transport.longPolling
    }

    if headers.count > 0 {
      connection.headers = headers
    }

    hub = connection.createHubProxy(hubName)

    hubMethods.forEach { (methodName) in
      hub.on(methodName) { (args) in
        SwiftSignalRFlutterPlugin.channel.invokeMethod("NewMessage", arguments: [methodName, args?[0]])
      }
    }

    connection.starting = { [weak self] in
      print("SignalR Connecting. Current Status: \(String(describing: self?.connection.state.stringValue))")
      SwiftSignalRFlutterPlugin.channel.invokeMethod("ConnectionStatus", arguments: self?.connection.state.stringValue)
    }

    connection.reconnecting = { [weak self] in
      print("SignalR Reconnecting. Current Status: \(String(describing: self?.connection.state.stringValue))")
      SwiftSignalRFlutterPlugin.channel.invokeMethod("ConnectionStatus", arguments: self?.connection.state.stringValue)
    }

    connection.connected = { [weak self] in
      print("SignalR Connected. Connection ID: \(String(describing: self?.connection.connectionID))")
      SwiftSignalRFlutterPlugin.channel.invokeMethod("ConnectionStatus", arguments: self?.connection.state.stringValue)
    }

    connection.reconnected = { [weak self] in
      print("SignalR Reconnected...")
      print("Connection ID: \(String(describing: self?.connection.connectionID))")
      SwiftSignalRFlutterPlugin.channel.invokeMethod("ConnectionStatus", arguments: self?.connection.state.stringValue)
    }

    connection.disconnected = { [weak self] in
      print("SignalR Disconnected...")
      SwiftSignalRFlutterPlugin.channel.invokeMethod("ConnectionStatus", arguments: self?.connection.state.stringValue)
    }

    connection.connectionSlow = {
      print("Connection slow...")
      SwiftSignalRFlutterPlugin.channel.invokeMethod("ConnectionStatus", arguments: "Slow")
    }

    connection.error = { [weak self] error in
      print("Error: \(String(describing: error))")
      SwiftSignalRFlutterPlugin.channel.invokeMethod("ConnectionStatus", arguments: self?.connection.state.stringValue)

      if let source = error?["source"] as? String, source == "TimeoutException" {
        print("Connection timed out. Restarting...")
        self?.connection.start()
      }
    }

    connection.start()
    result(true)
  }

  func connectToServer1(baseUrl: String, hubName: String, transport: Int, queryString : String, headers: [String: String], hubMethods: [String], result: @escaping FlutterResult) {
      connection1 = SignalR1(baseUrl)

      if !queryString.isEmpty {
        let qs = queryString.components(separatedBy: "=")
        connection1.queryString = [qs[0]:qs[1]]
      }

      if transport == 1 {
        connection1.transport = Transport.serverSentEvents
      } else if transport == 2 {
        connection1.transport = Transport.longPolling
      }

      if headers.count > 0 {
        connection1.headers = headers
      }

      hub1 = connection1.createHubProxy(hubName)

      hubMethods.forEach { (methodName) in
        hub1.on(methodName) { (args) in
          SwiftSignalRFlutterPlugin.channel.invokeMethod("NewMessage", arguments: [methodName, args?[0]])
        }
      }

      connection1.starting = { [weak self] in
        print("SignalR Connecting. Current Status: \(String(describing: self?.connection1.state.stringValue))")
        SwiftSignalRFlutterPlugin.channel.invokeMethod("Connection1Status", arguments: self?.connection1.state.stringValue)
      }

      connection1.reconnecting = { [weak self] in
        print("SignalR Reconnecting. Current Status: \(String(describing: self?.connection1.state.stringValue))")
        SwiftSignalRFlutterPlugin.channel.invokeMethod("Connection1Status", arguments: self?.connection1.state.stringValue)
      }

      connection1.connected = { [weak self] in
        print("SignalR Connected. Connection1 ID: \(String(describing: self?.connection1.connectionID))")
        SwiftSignalRFlutterPlugin.channel.invokeMethod("Connection1Status", arguments: self?.connection1.state.stringValue)
      }

      connection1.reconnected = { [weak self] in
        print("SignalR Reconnected...")
        print("Connection1 ID: \(String(describing: self?.connection1.connectionID))")
        SwiftSignalRFlutterPlugin.channel.invokeMethod("Connection1Status", arguments: self?.connection1.state.stringValue)
      }

      connection1.disconnected = { [weak self] in
        print("SignalR Disconnected...")
        SwiftSignalRFlutterPlugin.channel.invokeMethod("Connection1Status", arguments: self?.connection1.state.stringValue)
      }

      connection1.connectionSlow = {
        print("Connection1 slow...")
        SwiftSignalRFlutterPlugin.channel.invokeMethod("Connection1Status", arguments: "Slow")
      }

      connection1.error = { [weak self] error in
        print("Error: \(String(describing: error))")
        SwiftSignalRFlutterPlugin.channel.invokeMethod("Connection1Status", arguments: self?.connection1.state.stringValue)

        if let source = error?["source"] as? String, source == "TimeoutException" {
          print("Connection1 timed out. Restarting...")
          self?.connection1.start()
        }
      }

      connection1.start()
      result(true)
    }



  func reconnect(result: @escaping FlutterResult) {
    if let connection = self.connection {
      connection.connect()
    } else {
      result(FlutterError(code: "Error", message: "SignalR Connection not found or null", details: "Start SignalR connection first"))
    }
  }
  func reconnect1(result: @escaping FlutterResult) {
    if let connection1 = self.connection {
      connection1.connect()
    } else {
      result(FlutterError(code: "Error", message: "SignalR1 Connection1 not found or null", details: "Start SignalR connection1 first"))
    }
  }

  func stop(result: @escaping FlutterResult) {
    if let connection1 = self.connection {
      connection1.stop()
    } else {
      result(FlutterError(code: "Error", message: "SignalR1 Connection not found or null", details: "Start SignalR connection first"))
    }
  }
  func stop1(result: @escaping FlutterResult) {
      if let connection1 = self.connection {
        connection1.stop()
      } else {
        result(FlutterError(code: "Error", message: "SignalR1 Connection not found or null", details: "Start SignalR connection first"))
      }
  }


  func listenToHubMethod(methodName : String, result: @escaping FlutterResult) {
    if let hub = self.hub {
      hub.on(methodName) { (args) in
        SwiftSignalRFlutterPlugin.channel.invokeMethod("NewMessage", arguments: [methodName, args?[0]])
      }
    } else {
      result(FlutterError(code: "Error", message: "SignalR Connection not found or null", details: "Connect SignalR before listening a Hub method"))
    }
  }
  func listenToHubMethod1(methodName : String, result: @escaping FlutterResult) {
      if let hub1 = self.hub {
        hub1.on(methodName) { (args) in
          SwiftSignalRFlutterPlugin.channel.invokeMethod("NewMessage", arguments: [methodName, args?[0]])
        }
      } else {
        result(FlutterError(code: "Error", message: "SignalR Connection not found or null", details: "Connect SignalR before listening a Hub method"))
      }
    }



  func invokeServerMethod(methodName: String, arguments: [Any]? = nil, result: @escaping FlutterResult) {
    do {
      if let hub = self.hub {
        try hub.invoke(methodName, arguments: arguments, callback: { (res, error) in
          if let error = error {
            result(FlutterError(code: "Error", message: String(describing: error), details: nil))
          } else {
            result(res)
          }
        })
      } else {
        throw NSError.init(domain: "NullPointerException", code: 0, userInfo: [NSLocalizedDescriptionKey : "Hub1 is null. Initiate a connection first."])
      }
    } catch {
      result(FlutterError.init(code: "Error", message: error.localizedDescription, details: nil))
    }
  }
  func invokeServerMethod1(methodName: String, arguments: [Any]? = nil, result: @escaping FlutterResult) {
      do {
        if let hub1 = self.hub {
          try hub1.invoke(methodName, arguments: arguments, callback: { (res, error) in
            if let error = error {
              result(FlutterError(code: "Error", message: String(describing: error), details: nil))
            } else {
              result(res)
            }
          })
        } else {
          throw NSError.init(domain: "NullPointerException", code: 0, userInfo: [NSLocalizedDescriptionKey : "Hub1 is null. Initiate a connection first."])
        }
      } catch {
        result(FlutterError.init(code: "Error", message: error.localizedDescription, details: nil))
      }
    }


}

