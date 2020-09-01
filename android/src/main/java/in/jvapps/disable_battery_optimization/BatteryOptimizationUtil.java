package in.jvapps.disable_battery_optimization;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.annotation.Nullable;

import com.thelittlefireman.appkillermanager.managers.KillerManager;
import com.thelittlefireman.appkillermanager.ui.DialogKillerManagerBuilder;

import java.util.ArrayList;
import java.util.List;


public class BatteryOptimizationUtil {

    public static void showBatteryOptimizationDialog(
            final Context context,
            final KillerManager.Actions action,
            final String titleMessage,
            final String contentMessage,
            @Nullable final OnBatteryOptimizationAccepted positiveCallback,
            @Nullable final OnBatteryOptimizationCanceled negativeCallback) {

        new DialogKillerManagerBuilder()
                .setContext(context)
                .setDontShowAgain(false)
                .setTitleMessage(titleMessage)
                .setContentMessage(contentMessage)
                .setPositiveMessage("Ok")
                .setNegativeMessage("Will Give Later")
                .setOnPositiveCallback((View view) -> {
                    if (positiveCallback != null)
                        positiveCallback.onBatteryOptimizationAccepted();
                })
                .setOnNegativeCallback((View view) -> {
                    if (negativeCallback != null)
                        negativeCallback.onBatteryOptimizationCanceled();
                })
                .setAction(action)
                .show();
    }

    public static boolean isBatteryOptimizationAvailable(final Context context) {
        return getResolvableComponentName(context) != null;
    }

    @Nullable
    private static ComponentName getResolvableComponentName(final Context context) {
        for (ComponentName componentName : getComponentNames()) {
            final Intent intent = new Intent();
            intent.setComponent(componentName);
            if (context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null)
                return componentName;
        }
        return null;
    }

    private static List<ComponentName> getComponentNames() {
        final List<ComponentName> names = new ArrayList<>();
        names.add(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
        names.add(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
        names.add(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
        names.add(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"));
        names.add(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity"));
        names.add(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
        names.add(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity"));
        names.add(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity"));
        names.add(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
        names.add(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"));
        names.add(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
        names.add(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity"));
        names.add(new ComponentName("com.htc.pitroad.landingpage.activity.LandingPageActivity", "com.htc.pitroad.landingpage.activity.LandingPageActivity"));
        names.add(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"));

        return names;
    }

    public interface OnBatteryOptimizationAccepted {
        void onBatteryOptimizationAccepted();
    }

    public interface OnBatteryOptimizationCanceled {
        void onBatteryOptimizationCanceled();
    }

}