import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:disable_battery_optimization/disable_battery_optimization.dart';

void main() {
  const MethodChannel channel =
      MethodChannel('in.jvapps.disable_battery_optimization');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
      switch (methodCall.method) {
        case 'isAutoStartEnabled':
        case 'isBatteryOptimizationDisabled':
        case 'isManBatteryOptimizationDisabled':
        case 'isAllOptimizationsDisabled':
        case 'showDisableBatteryOptimization':
          return true;
        default:
          return true;
      }
    });
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });

  test('isBatteryOptimizationDisabled returns true', () async {
    expect(await DisableBatteryOptimization.isBatteryOptimizationDisabled, true);
  });

  test('isAutoStartEnabled returns true', () async {
    expect(await DisableBatteryOptimization.isAutoStartEnabled, true);
  });
}

