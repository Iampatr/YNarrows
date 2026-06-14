package ru.link.YNarrows;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

public class HUDLauncherActivity extends Activity {

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Intent intent = getIntent();
            int displayId = intent.getIntExtra("displayId", 0);
            int left = intent.getIntExtra("left", 1);
            int top = intent.getIntExtra("top", 1);
            int right = intent.getIntExtra("right", 1000);
            int bottom = intent.getIntExtra("bottom", 700);
            int windowingMode = intent.getIntExtra("windowingMode", 5);

            Intent hudIntent = new Intent("android.intent.action.MAIN");
            hudIntent.setClassName("ru.link.YNarrows", "ru.link.YNarrows.HUDActivity");
            hudIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

            Rect rect = new Rect(left, top, right, bottom);
            Bundle bundle = ActivityOptions.makeBasic()
                    .setLaunchDisplayId(displayId)
                    .setLaunchBounds(rect)
                    .toBundle();
            bundle.putInt("android.activity.windowingMode", windowingMode);

            startActivity(hudIntent, bundle);
        } catch (Exception ignored) {
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        finishAndRemoveTask();
    }
}
