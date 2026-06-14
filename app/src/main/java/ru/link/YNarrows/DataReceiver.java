package ru.link.YNarrows;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import kotlin.jvm.internal.Intrinsics;

public final class DataReceiver extends BroadcastReceiver {

    public static String PackageToClose = "";

    public void onReceive(Context context, Intent intent) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(intent, "intent");
        Toast.makeText(context, "ТУТ", Toast.LENGTH_SHORT).show();

        try {
            if (Intrinsics.areEqual("android.intent.action.BOOT_COMPLETED", intent.getAction()) ||
                    Intrinsics.areEqual("android.intent.action.QUICKBOOT_POWERON", intent.getAction())  ||
                    Intrinsics.areEqual("android.intent.action.LOCKED_BOOT_COMPLETED", intent.getAction()) ) {
                Toast.makeText(context, "ТУТ", Toast.LENGTH_SHORT).show();

                String access = Settings.Secure.getString(context.getContentResolver(),"enabled_accessibility_services");
                if (!access.contains("ru.link.YNarrows/ru.link.YNarrows.NodeInfoForNavi") && !access.isEmpty())
                {
                    access = access+":ru.link.YNarrows/ru.link.YNarrows.NodeInfoForNavi";
                    Settings.Secure.putString(context.getContentResolver(), "enabled_accessibility_services", access);
                    Toast.makeText(context, access, Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception ignored) {
            Log.d("DataReceiver: ", "Предоставьте приложению права accessability иным возможным способом");
        }


    StartHUDInfo(context);
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
            intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            Rect rect = new Rect(0, 721, 1920, 1080);
            Bundle bundle  = ActivityOptions.makeBasic().setLaunchDisplayId(2).setLaunchBounds(rect).toBundle();
            bundle.putInt("android.activity.windowingMode", 5);
            context.getApplicationContext().startActivity(intent4, bundle);

        } catch (Exception ignored) {
        }
    }

}

