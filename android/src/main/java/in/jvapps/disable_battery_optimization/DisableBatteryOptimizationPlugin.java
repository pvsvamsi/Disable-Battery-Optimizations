package in.jvapps.disable_battery_optimization;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import in.jvapps.disable_battery_optimization.managers.KillerManager;

import java.util.List;

import in.jvapps.disable_battery_optimization.utils.BatteryOptimizationUtil;
import in.jvapps.disable_battery_optimization.utils.BatteryOptimizationUtil.OnBatteryOptimizationDone;
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
public class DisableBatteryOptimizationPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler,
        PluginRegistry.ActivityResultListener{

    private Context mContext;
    private Activity mActivity;
    private BatteryOptimizationUtil.OnBatteryOptimizationDone onBatteryOptimizationDone;

    // These are null when not using v2 embedding.
    private MethodChannel channel;

    private static final int REQUEST_DISABLE_BATTERY_OPTIMIZATIONS = 2244;
    private final String TAG = "BO:DisableOptimization";
    private static final String CHANNEL_NAME = "in.jvapps.disable_battery_optimization";

    private String autoStartTitle;
    private String autoStartMessage;
    private String manBatteryTitle;
    private String manBatteryMessage;


    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent){
            Log.e(TAG, "onActivityResult " + requestCode +" " + resultCode);
            switch (requestCode) {
                case REQUEST_DISABLE_BATTERY_OPTIMIZATIONS:
                    if (onBatteryOptimizationDone != null)
                        onBatteryOptimizationDone.onBatteryOptimizationDone(true);
                    else
                        Log.e(TAG, "no callback for onActivityResult");

                    onBatteryOptimizationDone = null;
            }
            return true;
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
            case "showEnableAutoStart":
                try {
                    List arguments = (List) call.arguments;
                    if (arguments != null) {
                        autoStartTitle = String.valueOf(arguments.get(0));
                        autoStartMessage = String.valueOf(arguments.get(1));
                        showAutoStartEnabler(() -> setManAutoStart(true), () -> setManAutoStart(false));
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
                        showManBatteryOptimizationDisabler(false, (res) -> {
                            result.success(res);
                        });
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
                    showIgnoreBatteryPermissions((res) -> {
                        result.success(res);
                    });
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
                        handleIgnoreAllBatteryPermission((res) -> {
                            result.success(res);
                        });
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
        binding.addActivityResultListener(this);
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
                                      @NonNull final BatteryOptimizationUtil.OnBatteryOptimizationCanceled negativeCallback) {
        BatteryOptimizationUtil.showBatteryOptimizationDialog(
                mActivity,
                KillerManager.Actions.ACTION_AUTOSTART,
                autoStartTitle,
                autoStartMessage,
                positiveCallback,
                negativeCallback
        );
    }

    private void showManBatteryOptimizationDisabler(boolean isRequestNativeBatteryOptimizationDisabler, final BatteryOptimizationUtil.OnBatteryOptimizationDone doneCallback) {
        BatteryOptimizationUtil.showBatteryOptimizationDialog(
                mActivity,
                KillerManager.Actions.ACTION_POWERSAVING,
                manBatteryTitle,
                manBatteryMessage,
                () -> {
                    setManBatteryOptimization(true);
                    if (isRequestNativeBatteryOptimizationDisabler) {
                        showIgnoreBatteryPermissions(doneCallback);
                    } else {
                        doneCallback.onBatteryOptimizationDone(true);
                    }
                },
                () -> {
                    if (isRequestNativeBatteryOptimizationDisabler) {
                        showIgnoreBatteryPermissions(doneCallback);
                    } else {
                        doneCallback.onBatteryOptimizationDone(true);
                    }
                }
        );
    }

    private void showIgnoreBatteryPermissions(@NonNull final BatteryOptimizationUtil.OnBatteryOptimizationDone doneCallback) {
        boolean result = false;
        if (!BatteryOptimizationUtil.isIgnoringBatteryOptimizations(mContext)) {
            final Intent ignoreBatteryOptimizationsIntent = BatteryOptimizationUtil
                    .getIgnoreBatteryOptimizationsIntent(mContext);
            if (ignoreBatteryOptimizationsIntent != null && onBatteryOptimizationDone == null) {
                onBatteryOptimizationDone = doneCallback;
                mActivity.startActivityForResult(ignoreBatteryOptimizationsIntent, REQUEST_DISABLE_BATTERY_OPTIMIZATIONS);
                return;
            } else if ( onBatteryOptimizationDone != null ){
                Log.w(TAG, "Can't ignore the battery optimization as another battery system dialog is already running");
                result = false;
            } else {
                Log.w(TAG, "Can't ignore the battery optimization as the intent is null");
                result = false;
            }
        } else {
            Log.i(TAG, "Battery optimization is already disabled");
            result = true;
        }

        if (doneCallback != null)
            doneCallback.onBatteryOptimizationDone(result);
    }

    private void handleIgnoreAllBatteryPermission(@NonNull final BatteryOptimizationUtil.OnBatteryOptimizationDone doneCallback) {
        boolean isManBatteryOptimizationDisabled = getManBatteryOptimization();
        if (!getManAutoStart()) {
            showAutoStartEnabler(() -> {
                setManAutoStart(true);
                if (!isManBatteryOptimizationDisabled)
                    showManBatteryOptimizationDisabler(true, doneCallback);
                else
                    showIgnoreBatteryPermissions(doneCallback);
            }, () -> {
                if (!isManBatteryOptimizationDisabled)
                    showManBatteryOptimizationDisabler(true, doneCallback);
                else {
                    showIgnoreBatteryPermissions(doneCallback);
                }
            });
        } else {
            if (!isManBatteryOptimizationDisabled)
                showManBatteryOptimizationDisabler(true, doneCallback);
            else
                showIgnoreBatteryPermissions(doneCallback);
        }
    }

    public void setManBatteryOptimization(boolean val) {
        PrefUtils.saveToPrefs(mContext, PrefKeys.IS_MAN_BATTERY_OPTIMIZATION_ACCEPTED, val);
    }

    public boolean getManBatteryOptimization() {
        if (PrefUtils.hasKey(mContext, PrefKeys.IS_MAN_BATTERY_OPTIMIZATION_ACCEPTED)) {
            return (boolean) PrefUtils.getFromPrefs(mContext, PrefKeys.IS_MAN_BATTERY_OPTIMIZATION_ACCEPTED, false);
        } else {
            boolean isManBatteryAvailable = KillerManager.isActionAvailable(mContext, KillerManager.Actions.ACTION_POWERSAVING);
            PrefUtils.saveToPrefs(mContext, PrefKeys.IS_MAN_BATTERY_OPTIMIZATION_ACCEPTED, !isManBatteryAvailable);
            return !isManBatteryAvailable;
        }
    }

    public void setManAutoStart(boolean val) {
        PrefUtils.saveToPrefs(mContext, PrefKeys.IS_MAN_AUTO_START_ACCEPTED, val);
    }

    public boolean getManAutoStart() {
        if (PrefUtils.hasKey(mContext, PrefKeys.IS_MAN_AUTO_START_ACCEPTED)) {
            return (boolean) PrefUtils.getFromPrefs(mContext, PrefKeys.IS_MAN_AUTO_START_ACCEPTED, false);
        } else {
            boolean isAutoStartAvailable = KillerManager.isActionAvailable(mContext, KillerManager.Actions.ACTION_AUTOSTART);
            PrefUtils.saveToPrefs(mContext, PrefKeys.IS_MAN_AUTO_START_ACCEPTED, !isAutoStartAvailable);
            return !isAutoStartAvailable;
        }
    }
}
