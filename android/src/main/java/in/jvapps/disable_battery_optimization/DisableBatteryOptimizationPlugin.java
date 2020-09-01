package in.jvapps.disable_battery_optimization;

import android.content.Context;

import androidx.annotation.NonNull;

import com.thelittlefireman.appkillermanager.managers.KillerManager;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * DisableBatteryOptimizationPlugin
 */
public class DisableBatteryOptimizationPlugin implements FlutterPlugin, MethodCallHandler {

    private Context mContext;
    private boolean isManAutoStartAccepted = false;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        mContext = flutterPluginBinding.getApplicationContext();
        final MethodChannel channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "disable_battery_optimization");
        channel.setMethodCallHandler(new DisableBatteryOptimizationPlugin());
    }

    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "disable_battery_optimization");
        channel.setMethodCallHandler(new DisableBatteryOptimizationPlugin());
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "enableAutoStart":
                BatteryOptimizationUtil.
            default:
                result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    }

    private void showAutoStartEnabler(String title, String body) {
        BatteryOptimizationUtil.showBatteryOptimizationDialog(
                mContext,
                KillerManager.Actions.ACTION_AUTOSTART,
                title,
                body,
                () -> setManAutoStart(true),
                () -> setManAutoStart(false)
        );
    }

    private void showBatteryOptimizationDisabler(String title, String body) {
        BatteryOptimizationUtil.showBatteryOptimizationDialog(
                mContext,
                KillerManager.Actions.ACTION_POWERSAVING,
                title,
                body,
                () -> {
                    setManBatteryOptimization(true);
                    requestAutoStartEnabler();
                },
                () -> {
                    setManBatteryOptimization(false);
                    requestAutoStartEnabler();
                }
        );
    }

    private void requestAutoStartEnabler(String title, String message) {
        if (!isManAutoStartAccepted) {
            BatteryOptimizationUtil.showBatteryOptimizationDialog(
                    mContext,
                    KillerManager.Actions.ACTION_AUTOSTART,
                    () -> setManAutoStart(true),
                    () -> setManAutoStart(false)
            );
        }
    }

    public void setManBatteryOptimization(boolean val) {
        PrefUtils.saveToPrefs(mContext, PrefKeys.IS_MAN_BATTERY_OPTIMIZATION_ACCEPTED, val);
    }

    public boolean getManBatteryOptimization() {
        return (boolean) PrefUtils.getFromPrefs(mContext, PrefKeys.IS_MAN_BATTERY_OPTIMIZATION_ACCEPTED, false);
    }

    public void setManAutoStart(boolean val) {
        PrefUtils.saveToPrefs(mContext, PrefKeys.IS_MAN_AUTO_START_ACCEPTED, val);
    }

    public boolean getManAutoStart() {
        return (boolean) PrefUtils.getFromPrefs(mContext, PrefKeys.IS_MAN_AUTO_START_ACCEPTED, false);
    }
}
