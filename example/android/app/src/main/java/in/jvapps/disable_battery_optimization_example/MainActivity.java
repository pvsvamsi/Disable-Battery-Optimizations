package in.jvapps.disable_battery_optimization_example;

import android.os.Bundle;

import androidx.annotation.NonNull;

import in.jvapps.disable_battery_optimization.DisableBatteryOptimizationPlugin;
//import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugins.GeneratedPluginRegistrant;

import io.flutter.app.FlutterActivity;

public class MainActivity extends FlutterActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);
  }



}
