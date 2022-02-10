import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
//import 'package:disable_battery_optimization/disable_battery_optimization.dart';

void main() {
  const MethodChannel channel = MethodChannel('disable_battery_optimization');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  /*test('getPlatformVersion', () async {
    expect(await DisableBatteryOptimization.platformVersion, '42');
  });*/
}
