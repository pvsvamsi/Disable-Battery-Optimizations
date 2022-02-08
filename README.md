# disable_battery_optimization

Flutter plugin to check and disable battery optimizations. Also shows custom steps to disable the **manufacturer specific** optimizations on devices like mi, xiaomi, samsung, oppo, huawei, oneplus etc


------------

### Auto start
```
bool isAutoStartEnabled = await DisableBatteryOptimization.isAutoStartEnabled;
```
This will return true, for the devices which doesn't support auto start configuration as well for the devices on which, user has clicked on positive action for the auto start configuration dialog.

```
await DisableBatteryOptimization.showEnableAutoStartSettings("Enable Auto Start", "Follow the steps and enable the auto start of this app");
```
This will show a dialog with steps to enable auto start permission on the current device (if it's available)

**Note:** We cannot determine if the user has actually enabled the auto start permission or not. We can only show a dialog with the steps to enable.

------------

###  Android Battery Optimization
```
bool isBatteryOptimizationDisabled = await DisableBatteryOptimization.isBatteryOptimizationDisabled;
```
This will return true, only if the user has disabled the 'native' battery optimization for this app.

```
await DisableBatteryOptimization.showDisableBatteryOptimizationSettings();
```
This will ask for the permission to disable battery optimization for the app.

------------

### Manufacturer specific Battery Optimization
```
bool isManBatteryOptimizationDisabled = await DisableBatteryOptimization.isManufacturerBatteryOptimizationDisabled;
```
This will return true, for the devices which doesn't support manufacturer specific battery optimization configuration as well for the devices on which, user has clicked on positive action for the manufacturer specific battery optimization configuration dialog.

```
await DisableBatteryOptimization.showDisableManufacturerBatteryOptimizationSettings("Your device has additional battery optimization", "Follow the steps and disable the optimizations to allow smooth functioning of this app");
```
This will show a dialog with steps to disable the manufacturer specific battery optimization on the current device (if it's available)

**Note:** We cannot determine if the user has actually disabled the manufacturer specific battery optimization or not. We can only show a dialog with the steps to disable it.

------------

There are other helper methods available to check if all the permissions are enabled or disabled.

```
await DisableBatteryOptimization.isAllBatteryOptimizationDisabled
await DisableBatteryOptimization.showDisableAllOptimizationsSettings
```