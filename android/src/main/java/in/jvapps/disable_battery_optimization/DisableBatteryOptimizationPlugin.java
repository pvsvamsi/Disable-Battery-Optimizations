package in.jvapps.disable_battery_optimization;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.thelittlefireman.appkillermanager.managers.KillerManager;

import java.util.List;

import in.jvapps.disable_battery_optimization.utils.BatteryOptimizationUtil;
import in.jvapps.disable_battery_optimization.utils.PrefKeys;
import in.jvapps.disable_battery_optimization.utils.PrefUtils;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/**
 * DisableBatteryOptimizationPlugin
 */
public class DisableBatteryOptimizationPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler {

    private Context mContext;
    private Activity mActivity;

    // These are null when not using v2 embedding.
    private MethodChannel channel;

    private static final int REQUEST_DISABLE_BATTERY_OPTIMIZATIONS = 2244;
    private final String TAG = "DisableOptimization";
    private static final String CHANNEL_NAME = "in.jvapps.disable_battery_optimization";

    private String autoStartTitle;
    private String autoStartMessage;
    private String manBatteryTitle;
    private String manBatteryMessage;

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "showEnableAutoStart":
                try {
                    List arguments = (List) call.arguments;
                    if (arguments != null) {
                        autoStartTitle = String.valueOf(arguments.get(0));
                        autoStartMessage = String.valueOf(arguments.get(1));
                        showAutoStartEnabler(null, null);
                        result.success(true);
                    } else {
                        Log.e(TAG, "Unable to request enableAutoStart. Arguments are null");
                        result.success(false);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Exception in showEnableAutoStart. " + ex.toString());
                    result.success(false);
                }
                break;
            case "showDisableManBatteryOptimization":
                try {
                    List arguments = (List) call.arguments;
                    if (arguments != null) {
                        manBatteryTitle = String.valueOf(arguments.get(0));
                        manBatteryMessage = String.valueOf(arguments.get(1));
                        showManBatteryOptimizationDisabler(false);
                        result.success(true);
                    } else {
                        Log.e(TAG, "Unable to request disable manufacturer battery optimization. Arguments are null");
                        result.success(false);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Exception in showDisableManBatteryOptimization. " + ex.toString());
                    result.success(false);
                }
                break;
            case "showDisableBatteryOptimization":
                try {
                    showIgnoreBatteryPermissions();
                    result.success(true);
                } catch (Exception ex) {
                    Log.e(TAG, "Exception in showDisableBatteryOptimization. " + ex.toString());
                    result.success(false);
                }
                break;
            case "disableAllOptimizations":
                try {
                    List arguments = (List) call.arguments;
                    if (arguments != null) {
                        autoStartTitle = String.valueOf(arguments.get(0));
                        autoStartMessage = String.valueOf(arguments.get(1));
                        manBatteryTitle = String.valueOf(arguments.get(2));
                        manBatteryMessage = String.valueOf(arguments.get(3));
                        handleIgnoreAllBatteryPermission();
                        result.success(true);
                    } else {
                        Log.e(TAG, "Unable to request disable all optimizations. Arguments are null");
                        result.success(false);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Exception in disableAllOptimizations. " + ex.toString());
                    result.success(false);
                }
                break;
            case "isAutoStartEnabled":
                result.success(getManAutoStart());
                break;
            case "isBatteryOptimizationDisabled":
                result.success(BatteryOptimizationUtil.isIgnoringBatteryOptimizations(mContext));
                break;
            case "isManBatteryOptimizationDisabled":
                result.success(getManBatteryOptimization());
                break;
            case "isAllOptimizationsDisabled":
                result.success(getManAutoStart() && BatteryOptimizationUtil.isIgnoringBatteryOptimizations(mContext) && getManBatteryOptimization());
                break;
            default:
                result.notImplemented();
        }
    }

    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        channel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL_NAME);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        mActivity = binding.getActivity();
        mContext = mActivity.getApplicationContext();
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        mActivity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        mActivity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        mActivity = null;
        channel.setMethodCallHandler(null);
    }

    private void showAutoStartEnabler(@Nullable final BatteryOptimizationUtil.OnBatteryOptimizationAccepted positiveCallback,
                                      @Nullable final BatteryOptimizationUtil.OnBatteryOptimizationCanceled negativeCallback) {
        BatteryOptimizationUtil.showBatteryOptimizationDialog(
                mContext,
                KillerManager.Actions.ACTION_AUTOSTART,
                autoStartTitle,
                autoStartMessage,
                (positiveCallback == null) ? () -> setManAutoStart(true) : positiveCallback,
                (negativeCallback == null) ? () -> setManAutoStart(false) : negativeCallback
        );
    }

    private void showManBatteryOptimizationDisabler(boolean isRequestNativeBatteryOptimizationDisabler) {
        BatteryOptimizationUtil.showBatteryOptimizationDialog(
                mContext,
                KillerManager.Actions.ACTION_POWERSAVING,
                manBatteryTitle,
                manBatteryMessage,
                () -> {
                    setManBatteryOptimization(true);
                    if (isRequestNativeBatteryOptimizationDisabler) {
                        showIgnoreBatteryPermissions();
                    }
                },
                () -> {
                    setManBatteryOptimization(false);
                    if (isRequestNativeBatteryOptimizationDisabler) {
                        showIgnoreBatteryPermissions();
                    }
                }
        );
    }

    private void showIgnoreBatteryPermissions() {
        if (!BatteryOptimizationUtil.isIgnoringBatteryOptimizations(mContext)) {
            final Intent ignoreBatteryOptimizationsIntent = BatteryOptimizationUtil.getIgnoreBatteryOptimizationsIntent(mContext);
            if (ignoreBatteryOptimizationsIntent != null) {
                mContext.startActivity(ignoreBatteryOptimizationsIntent);
            } else {
                Log.i(TAG, "Can't ignore the battery optimization as the intent is null");
            }
        } else {
            Log.i(TAG, "Battery optimization is already disabled");
        }
    }

    private void handleIgnoreAllBatteryPermission() {
        boolean isManBatteryOptimizationDisabled = getManBatteryOptimization();
        if (!getManAutoStart()) {
            showAutoStartEnabler(() -> {
                setManAutoStart(true);
                if (!isManBatteryOptimizationDisabled)
                    showManBatteryOptimizationDisabler(true);
                else
                    showIgnoreBatteryPermissions();
            }, () -> {
                setManAutoStart(false);
                if (!isManBatteryOptimizationDisabled)
                    showManBatteryOptimizationDisabler(true);
                else
                    showIgnoreBatteryPermissions();
            });
        } else {
            if (!isManBatteryOptimizationDisabled)
                showManBatteryOptimizationDisabler(true);
            else
                showIgnoreBatteryPermissions();
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
