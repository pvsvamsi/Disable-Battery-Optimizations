import Flutter
import UIKit

public class DisableBatteryOptimizationPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(
      name: "in.jvapps.disable_battery_optimization",
      binaryMessenger: registrar.messenger()
    )
    let instance = DisableBatteryOptimizationPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "isBatteryOptimizationDisabled",
         "isAutoStartEnabled",
         "isManBatteryOptimizationDisabled",
         "isAllOptimizationsDisabled":
      // Battery optimization restrictions/OEM killer management are Android-specific.
      // On iOS, return true as these optimizations are not applicable/disabled.
      result(true)
    case "showEnableAutoStart",
         "showDisableManBatteryOptimization",
         "showDisableBatteryOptimization",
         "disableAllOptimizations":
      result(true)
    default:
      result(FlutterMethodNotImplemented)
    }
  }
}
