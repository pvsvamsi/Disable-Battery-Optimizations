import 'package:flutter/material.dart';
import 'package:disable_battery_optimization/disable_battery_optimization.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Disable Battery Optimizations Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              MaterialButton(
                  child: Text("Is Auto Start Enabled"),
                  onPressed: () async {
                    bool isAutoStartEnabled =
                        await DisableBatteryOptimization.isAutoStartEnabled ?? false;
                    print(
                        "Auto start is ${isAutoStartEnabled ? "Enabled" : "Disabled"}");
                  }),
              MaterialButton(
                  child: Text("Is Battery optimization disabled"),
                  onPressed: () async {
                    bool isBatteryOptimizationDisabled =
                        await DisableBatteryOptimization.isBatteryOptimizationDisabled ?? false;
                    print(
                        "Battery optimization is ${!isBatteryOptimizationDisabled ? "Enabled" : "Disabled"}");
                  }),
              MaterialButton(
                  child: Text("Is Manufacturer Battery optimization disabled"),
                  onPressed: () async {
                    bool isManBatteryOptimizationDisabled =
                        await DisableBatteryOptimization.isManufacturerBatteryOptimizationDisabled ?? false;
                    print(
                        "Manufacturer Battery optimization is ${!isManBatteryOptimizationDisabled ? "Enabled" : "Disabled"}");
                  }),
              MaterialButton(
                  child: Text("Are All Battery optimizations disabled"),
                  onPressed: () async {
                    bool isAllBatteryOptimizationDisabled =
                        await DisableBatteryOptimization.isAllBatteryOptimizationDisabled ?? false;
                    print(
                        "All Battery optimizations are disabled ${isAllBatteryOptimizationDisabled ? "True" : "False"}");
                  }),
              MaterialButton(
                  child: Text("Enable Auto Start"),
                  onPressed: () {
                    DisableBatteryOptimization.showEnableAutoStartSettings(
                        "Enable Auto Start",
                        "Follow the steps and enable the auto start of this app");
                  }),
              MaterialButton(
                  child: Text("Disable Battery Optimizations"),
                  onPressed: () async {
                    DisableBatteryOptimization
                        .showDisableBatteryOptimizationSettings();
                    await DisableBatteryOptimization.showDisableBatteryOptimizationSettings();
                  }),
              MaterialButton(
                  child: Text("Disable Manufacturer Battery Optimizations"),
                  onPressed: () async {
                    await DisableBatteryOptimization
                        .showDisableManufacturerBatteryOptimizationSettings(
                            "Your device has additional battery optimization",
                            "Follow the steps and disable the optimizations to allow smooth functioning of this app");
                  }),
              MaterialButton(
                  child: Text("Disable all Optimizations"),
                  onPressed: () async {
                    DisableBatteryOptimization.showDisableAllOptimizationsSettings(
                    await DisableBatteryOptimization.showDisableAllOptimizationsSettings(
                        "Enable Auto Start",
                        "Follow the steps and enable the auto start of this app",
                        "Your device has additional battery optimization",
                        "Follow the steps and disable the optimizations to allow smooth functioning of this app");
                  })
            ],
          ),
        ),
      ),
    );
  }
}
