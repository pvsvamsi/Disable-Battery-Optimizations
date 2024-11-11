package in.jvapps.disable_battery_optimization;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import in.jvapps.disable_battery_optimization.managers.KillerManager;
import in.jvapps.disable_battery_optimization.managers.DevicesManager;

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
    private final String TAG = "BO:DisableOptimization";
    private static final String CHANNEL_NAME = "in.jvapps.disable_battery_optimization";

    private String autoStartTitle;
    private String autoStartMessage;
    private String manBatteryTitle;
    private String manBatteryMessage;


    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    public static void registerWith(PluginRegistry.Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL_NAME);
        channel.setMethodCallHandler(new DisableBatteryOptimizationPlugin(registrar.activity(), registrar.activeContext()));
    }

    private DisableBatteryOptimizationPlugin(Activity activity, Context context) {
        if (activity != null)
            mActivity = activity;
        if (context != null)
            mContext = context;
    }

    /**
     * Default constructor for DisableBatteryOptimizationPlugin.
     *
     * <p>Use this constructor when adding this plugin to an app with v2 embedding.
     */
    public DisableBatteryOptimizationPlugin() {
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "callAutoStart":
                KillerManager.doActionAutoStart(mContext);
                break;
            case "callDisableBatteryOptimization":
                KillerManager.doActionPowerSaving(mContext);
                break;
            case "showEnableAutoStart":
                try {
                    List arguments = (List) call.arguments;
                    if (arguments != null) {
                        autoStartTitle = String.valueOf(arguments.get(0));
                        autoStartMessage = String.valueOf(arguments.get(1));
                        showAutoStartEnabler(() -> {
                            setManAutoStart(true);
                            result.success("enabled");
                        }, () -> {
                            setManAutoStart(false);
                            result.success("disabled");
                        }, () -> {
                            result.success("notavailable");
                        });
                    } else {
                        Log.e(TAG, "Unable to request enableAutoStart. Arguments are null");
                        result.error("E404", "Unable to request enableAutoStart. Arguments are null", null);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Exception in showEnableAutoStart. " + ex.toString());
                    result.error("E100", "Exception in showEnableAutoStart", ex);
                }
                break;
            case "showDisableManBatteryOptimization":
                try {
                    List arguments = (List) call.arguments;
                    if (arguments != null) {
                        manBatteryTitle = String.valueOf(arguments.get(0));
                        manBatteryMessage = String.valueOf(arguments.get(1));
                        showManBatteryOptimizationDisabler(false, () -> {
                            result.success("enabled");
                        }, () -> {
                            result.success("disabled");
                        }, () -> {
                            result.success("notavailable");
                        });
                    } else {
                        Log.e(TAG, "Unable to request disable manufacturer battery optimization. Arguments are null");
                        result.error("E404", "Unable to request disable manufacturer battery optimization. Arguments are null", null);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Exception in showDisableManBatteryOptimization. " + ex.toString());
                    result.error("E100", "Exception during process", ex);
                }
                break;
            case "showDisableBatteryOptimization":
                try {
                    List arguments = (List) call.arguments;
                    if(arguments != null) {
                        autoStartTitle = String.valueOf(arguments.get(0));
                        autoStartMessage = String.valueOf(arguments.get(1));
                        showIgnoreBatteryPermissions(() -> {
                            result.success("enabled");
                        }, () -> {
                            result.success("disabled");
                        }, () -> {
                            result.success("notavailable");
                        });
                    } else {
                        Log.e(TAG, "Unable to request disable battery optimization. Arguments are null");
                        result.error("E404", "Unable to request disable battery optimization. Arguments are null", null);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Exception in showDisableBatteryOptimization. " + ex.toString());
                    result.error("E100", "Exception during process", ex);
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
                        handleIgnoreAllBatteryPermission(() -> {
                            result.success("enabled");
                        }, () -> {
                            result.success("disabled");
                        }, () -> {
                            result.success("notavailable");
                        });
                    } else {
                        Log.e(TAG, "Unable to request disable all optimizations. Arguments are null");
                        result.error("E404", "Unable to request disable all optimizations. Arguments are null", null);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Exception in disableAllOptimizations. " + ex.toString());
                    result.error("E100", "Error during processing", ex);
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
        mContext = binding.getApplicationContext();
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

    private void showAutoStartEnabler(@NonNull final BatteryOptimizationUtil.OnBatteryOptimizationAccepted positiveCallback,
                                      @NonNull final BatteryOptimizationUtil.OnBatteryOptimizationCanceled negativeCallback,
                                      @NonNull final BatteryOptimizationUtil.OnBatteryOptimizationNotAvailable notAvailableCallback) {
        BatteryOptimizationUtil.showBatteryOptimizationDialog(
                mActivity,
                KillerManager.Actions.ACTION_AUTOSTART,
                autoStartTitle,
                autoStartMessage,
                positiveCallback,
                negativeCallback,
                notAvailableCallback
        );
    }

    private void showManBatteryOptimizationDisabler(
        boolean isRequestNativeBatteryOptimizationDisabler, 
        @NonNull final BatteryOptimizationUtil.OnBatteryOptimizationAccepted positiveCallback,
        @NonNull final BatteryOptimizationUtil.OnBatteryOptimizationCanceled negativeCallback,
        @NonNull final BatteryOptimizationUtil.OnBatteryOptimizationNotAvailable notAvailableCallback) {
        BatteryOptimizationUtil.showBatteryOptimizationDialog(
                mActivity,
                KillerManager.Actions.ACTION_POWERSAVING,
                manBatteryTitle,
                manBatteryMessage,
                () -> {
                    setManBatteryOptimization(true);
                    if (isRequestNativeBatteryOptimizationDisabler) {
                        showIgnoreBatteryPermissions(positiveCallback, negativeCallback, notAvailableCallback);
                    } else {
                        notAvailableCallback.OnBatteryOptimizationNotAvailable();
                        
                    }
                },
                () -> {
                    if (isRequestNativeBatteryOptimizationDisabler) {
                        showIgnoreBatteryPermissions(positiveCallback, negativeCallback, notAvailableCallback);
                    } else {
                        notAvailableCallback.OnBatteryOptimizationNotAvailable();
                    }
                },
                notAvailableCallback
        );
    }

    private void showIgnoreBatteryPermissions(
            @NonNull final BatteryOptimizationUtil.OnBatteryOptimizationAccepted positiveCallback,
            @NonNull final BatteryOptimizationUtil.OnBatteryOptimizationCanceled negativeCallback,
            @NonNull final BatteryOptimizationUtil.OnBatteryOptimizationNotAvailable notAvailableCallback) {
        if (!BatteryOptimizationUtil.isIgnoringBatteryOptimizations(mContext)) {

            BatteryOptimizationUtil.showBatteryOptimizationDialog(
                mActivity,
                KillerManager.Actions.ACTION_AUTOSTART,
                autoStartTitle,
                autoStartMessage,
                () -> {
                    final Intent ignoreBatteryOptimizationsIntent = BatteryOptimizationUtil.getIgnoreBatteryOptimizationsIntent(mContext);
                    if (ignoreBatteryOptimizationsIntent != null) {
                        mContext.startActivity(ignoreBatteryOptimizationsIntent);
                        positiveCallback.onBatteryOptimizationAccepted();
                    } else {
                        Log.i(TAG, "Can't ignore the battery optimization as the intent is null");
                        negativeCallback.onBatteryOptimizationCanceled();
                    }
                },
                negativeCallback,
                notAvailableCallback
            );
            
        } else {
            Log.i(TAG, "Battery optimization is already disabled");
            positiveCallback.onBatteryOptimizationAccepted();
        }
    }

    private void handleIgnoreAllBatteryPermission(
            @NonNull final BatteryOptimizationUtil.OnBatteryOptimizationAccepted positiveCallback,
            @NonNull final BatteryOptimizationUtil.OnBatteryOptimizationCanceled negativeCallback,
            @NonNull final BatteryOptimizationUtil.OnBatteryOptimizationNotAvailable notAvailableCallback) {
        boolean isManBatteryOptimizationDisabled = getManBatteryOptimization();
        if (!getManAutoStart()) {
            showAutoStartEnabler(() -> {
                setManAutoStart(true);
                if (!isManBatteryOptimizationDisabled)
                    showManBatteryOptimizationDisabler(true, positiveCallback, negativeCallback, notAvailableCallback);
                else
                    showIgnoreBatteryPermissions(positiveCallback, negativeCallback, notAvailableCallback);
            }, () -> {
                if (!isManBatteryOptimizationDisabled)
                    showManBatteryOptimizationDisabler(true, positiveCallback, negativeCallback, notAvailableCallback);
                else
                    showIgnoreBatteryPermissions(positiveCallback, negativeCallback, notAvailableCallback);
            },
            () -> {}
            );
        } else {
            if (!isManBatteryOptimizationDisabled)
                showManBatteryOptimizationDisabler(true, positiveCallback, negativeCallback, notAvailableCallback);
            else
                showIgnoreBatteryPermissions(positiveCallback, negativeCallback, notAvailableCallback);
        }
    }

    public void setManBatteryOptimization(boolean val) {
        PrefUtils.saveToPrefs(mContext, PrefKeys.IS_MAN_BATTERY_OPTIMIZATION_ACCEPTED, val);
    }

    public boolean getManBatteryOptimization() {
        boolean isManBatteryAvailable = KillerManager.isActionAvailable(mContext, KillerManager.Actions.ACTION_POWERSAVING);
        return !isManBatteryAvailable;
    }

    public void setManAutoStart(boolean val) {
        PrefUtils.saveToPrefs(mContext, PrefKeys.IS_MAN_AUTO_START_ACCEPTED, val);
    }

    public boolean getManAutoStart() {
        if(DevicesManager.getDevice() == null) { 
            return true;
        }
        boolean isAutoStartAvailable = KillerManager.isActionAvailable(mContext, KillerManager.Actions.ACTION_AUTOSTART);
        return !isAutoStartAvailable;
    }
}
