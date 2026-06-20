package ru.link.YNarrows;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import kotlin.jvm.internal.Intrinsics;
import ru.link.YNarrows.utils.ExeCommands;

public final class DataReceiver extends BroadcastReceiver {

    public static String PackageToClose = "";

    public void onReceive(Context context, Intent intent) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(intent, "intent");

        try {
             if (Intrinsics.areEqual("android.intent.action.BOOT_COMPLETED", intent.getAction()) ||
                    Intrinsics.areEqual("android.intent.action.QUICKBOOT_POWERON", intent.getAction())  ||
                    Intrinsics.areEqual("android.intent.action.LOCKED_BOOT_COMPLETED", intent.getAction()) ) {


                //Toast.makeText(context, "Интент"+ intent.getAction(), Toast.LENGTH_SHORT).show();

                new ExeCommands().run("pm grant ru.link.YNarrows android.permission.BIND_ACCESSIBILITY_SERVICE", 10000).getResult();
                new ExeCommands().run("pm grant ru.link.YNarrows android.permission.BIND_NOTIFICATION_LISTENER_SERVICE", 10000).getResult();
                new ExeCommands().run("pm grant ru.link.YNarrows android.permission.WRITE_SECURE_SETTINGS", 10000).getResult();
                new ExeCommands().run("pm grant ru.link.YNarrows android.permission.DISABLE_HIDDEN_API_CHECKS", 10000).getResult();
                new ExeCommands().run("pm grant ru.link.YNarrows android.permission.SYSTEM_ALERT_WINDOW", 10000).getResult();
                new ExeCommands().run("pm grant ru.link.YNarrows android.permission.MANAGE_ACTIVITY_STACKS", 10000).getResult();
                new ExeCommands().run("pm grant ru.link.YNarrows android.permission.INTERNAL_SYSTEM_WINDOW", 10000).getResult();

                String access = Settings.Secure.getString(context.getContentResolver(),"enabled_accessibility_services");
                if (!access.contains("ru.link.YNarrows/ru.link.YNarrows.NodeInfoForNavi")) {
                    if (!access.isEmpty()) {
                        access = access+":ru.link.YNarrows/ru.link.YNarrows.NodeInfoForNavi";
                    }
                    else {
                        access = "ru.link.YNarrows/ru.link.YNarrows.NodeInfoForNavi";
                    }
                    Settings.Secure.putString(context.getContentResolver(), "enabled_accessibility_services", access);
                    //Toast.makeText(context, access, Toast.LENGTH_SHORT).show();
                }


                 SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
                 boolean autoStart = prefs.getBoolean("auto_start", true);
                 Log.d("DataReceiver", "auto_start=" + autoStart + " action=" + intent.getAction());

                 prefs.edit().putBoolean("hud_system_running", false).commit();

                 if (!autoStart) {
                     return;
                 }

                 prefs.edit().putBoolean("hud_system_running", false).commit();
                 StartHUDInfo(context);


            }

        } catch (Exception ignored) {
            Log.d("DataReceiver: ", "Предоставьте приложению права accessability иным возможным способом");
        }


    }


    private static void ClosePackage(Context context) {
        try {
            Object systemService2 = context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            Intrinsics.checkNotNull(systemService2, "null cannot be cast to non-null type android.app.ActivityManager");
            ActivityManager activityManager2 = (ActivityManager) systemService2;
            Method declaredMethod2 = activityManager2.getClass().getDeclaredMethod("forceStopPackage", String.class);
            Intrinsics.checkNotNullExpressionValue(declaredMethod2, "getDeclaredMethod(...)");
            declaredMethod2.setAccessible(true);
            declaredMethod2.invoke(activityManager2, PackageToClose);
            Thread.sleep(200L);
            PackageToClose = "";
        } catch (Exception ignored) {
        }
    }

    @SuppressLint("WrongConstant")
    private static void StartHUDInfo(Context context) {

        try {

            Intent intent4 = new Intent(context, HUDActivity.class);
            intent4.setFlags(268439552);
            Rect rect = new Rect(0, 0, 1920, 1080);
            Bundle bundle  = ActivityOptions.makeBasic().setLaunchDisplayId(2).setLaunchBounds(rect).toBundle();
            bundle.putInt("android.activity.windowingMode", 5);
            context.getApplicationContext().startActivity(intent4, bundle);
            SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
            prefs.edit().putBoolean("hud_system_running", true).commit();

        } catch (Exception ignored) {
        }
    }

}

