package ru.link.YNarrows;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import ru.link.YNarrows.utils.ExeCommands;

public class SettingsActivity extends AppCompatActivity {
    private String strResult = "";
    private SharedPreferences prefs;
    private static final String PREF_AUTO_START = "auto_start";
    private static final String PREF_HUD_RUNNING = "hud_system_running";
    private Button arrowLeft, arrowUp, arrowDown, arrowRight;
    private Button speedLeft, speedUp, speedDown, speedRight;
    private Button streetLeft, streetUp, streetDown, streetRight;
    private Button streetHeightUp, streetHeightDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("settings", MODE_PRIVATE);

        TextView statusText = findViewById(R.id.status_text);
        Button actionButton = findViewById(R.id.action_button);
        Button closeHudButton = findViewById(R.id.close_hud_button);
        CheckBox autoStartCheckbox = findViewById(R.id.auto_start_checkbox);
        CheckBox showBordersCheckbox = findViewById(R.id.show_borders_checkbox);
        CheckBox hideStreetCheckbox = findViewById(R.id.hide_street_checkbox);

        autoStartCheckbox.setChecked(prefs.getBoolean(PREF_AUTO_START, true));
        autoStartCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(PREF_AUTO_START, isChecked).commit());

        showBordersCheckbox.setChecked(!prefs.getBoolean(HUDActivity.PREF_SHOW_BORDERS, false));
        showBordersCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(HUDActivity.PREF_SHOW_BORDERS, !isChecked).commit();
            Intent intent = new Intent(HUDActivity.UPDATE_BORDERS_ACTION);
            intent.putExtra(HUDActivity.PREF_SHOW_BORDERS, !isChecked);
            intent.setPackage(getPackageName());
            sendBroadcast(intent);
        });

        hideStreetCheckbox.setChecked(prefs.getBoolean(HUDActivity.PREF_HIDE_STREET, false));
        hideStreetCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(HUDActivity.PREF_HIDE_STREET, isChecked).commit();
            Intent intent = new Intent(HUDActivity.UPDATE_HIDE_STREET_ACTION);
            intent.putExtra(HUDActivity.PREF_HIDE_STREET, isChecked);
            intent.setPackage(getPackageName());
            sendBroadcast(intent);
        });

        closeHudButton.setOnClickListener(v -> {
            prefs.edit().putBoolean(PREF_HUD_RUNNING, false).commit();
            Intent intent = new Intent(HUDActivity.CLOSE_HUD_ACTION);
            intent.putExtra("close", true);
            intent.setPackage(getPackageName());
            sendBroadcast(intent);
            setMoveButtonsEnabled(false);
        });

        arrowLeft = findViewById(R.id.arrow_left);
        arrowUp = findViewById(R.id.arrow_up);
        arrowDown = findViewById(R.id.arrow_down);
        arrowRight = findViewById(R.id.arrow_right);
        speedLeft = findViewById(R.id.speed_left);
        speedUp = findViewById(R.id.speed_up);
        speedDown = findViewById(R.id.speed_down);
        speedRight = findViewById(R.id.speed_right);
        streetLeft = findViewById(R.id.street_left);
        streetUp = findViewById(R.id.street_up);
        streetDown = findViewById(R.id.street_down);
        streetRight = findViewById(R.id.street_right);
        streetHeightUp = findViewById(R.id.street_height_up);
        streetHeightDown = findViewById(R.id.street_height_down);
        TextView arrowPos = findViewById(R.id.arrow_pos);
        TextView speedPos = findViewById(R.id.speed_pos);
        TextView streetPos = findViewById(R.id.street_pos);

        updatePositionLabels(arrowPos, speedPos, streetPos);

        arrowLeft.setOnClickListener(v -> { sendMove(HUDActivity.FRAGMENT_ARROW, -5, 0); updatePositionLabels(arrowPos, speedPos, streetPos); });
        arrowUp.setOnClickListener(v -> { sendMove(HUDActivity.FRAGMENT_ARROW, 0, -5); updatePositionLabels(arrowPos, speedPos, streetPos); });
        arrowDown.setOnClickListener(v -> { sendMove(HUDActivity.FRAGMENT_ARROW, 0, 5); updatePositionLabels(arrowPos, speedPos, streetPos); });
        arrowRight.setOnClickListener(v -> { sendMove(HUDActivity.FRAGMENT_ARROW, 5, 0); updatePositionLabels(arrowPos, speedPos, streetPos); });
        speedLeft.setOnClickListener(v -> { sendMove(HUDActivity.FRAGMENT_SPEED, -5, 0); updatePositionLabels(arrowPos, speedPos, streetPos); });
        speedUp.setOnClickListener(v -> { sendMove(HUDActivity.FRAGMENT_SPEED, 0, -5); updatePositionLabels(arrowPos, speedPos, streetPos); });
        speedDown.setOnClickListener(v -> { sendMove(HUDActivity.FRAGMENT_SPEED, 0, 5); updatePositionLabels(arrowPos, speedPos, streetPos); });
        speedRight.setOnClickListener(v -> { sendMove(HUDActivity.FRAGMENT_SPEED, 5, 0); updatePositionLabels(arrowPos, speedPos, streetPos); });
        streetLeft.setOnClickListener(v -> { sendMove(HUDActivity.FRAGMENT_STREET, -5, 0); updatePositionLabels(arrowPos, speedPos, streetPos); });
        streetUp.setOnClickListener(v -> { sendMove(HUDActivity.FRAGMENT_STREET, 0, -5); updatePositionLabels(arrowPos, speedPos, streetPos); });
        streetDown.setOnClickListener(v -> { sendMove(HUDActivity.FRAGMENT_STREET, 0, 5); updatePositionLabels(arrowPos, speedPos, streetPos); });
        streetRight.setOnClickListener(v -> { sendMove(HUDActivity.FRAGMENT_STREET, 5, 0); updatePositionLabels(arrowPos, speedPos, streetPos); });
        streetHeightUp.setOnClickListener(v -> { sendHeight(5); updatePositionLabels(arrowPos, speedPos, streetPos); });
        streetHeightDown.setOnClickListener(v -> { sendHeight(-5); updatePositionLabels(arrowPos, speedPos, streetPos); });

        checkHudRunning();


        actionButton.setText("Старт");
        actionButton.setOnClickListener(v -> startHUD());
        if (isNotificationListenerEnabled()) {
            statusText.setText("Статус: права Notification Listener предоставлены");
        } else {
            statusText.setText("Статус: требуется включить Notification Listener (нажмите для перехода)");
            statusText.setTextColor(0xFFFF0000);
            statusText.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkHudRunning();
    }

    private boolean isNotificationListenerEnabled() {
        String enabledListeners = Settings.Secure.getString(
                getContentResolver(), "enabled_notification_listeners");
        return enabledListeners != null && enabledListeners.contains(getPackageName());
    }

    private void checkHudRunning() {
        boolean running = prefs.getBoolean(PREF_HUD_RUNNING, false);
        setMoveButtonsEnabled(running);
    }

    @SuppressLint("WrongConstant")
    private void startHUD() {
        Intent intent4 = new Intent(this, HUDActivity.class);
        intent4.setFlags(268439552);
        Rect rect = new Rect(0, 0, 1920, 1080);
        Bundle bundle = ActivityOptions.makeBasic().setLaunchBounds(rect).toBundle();
        bundle.putInt("android.activity.windowingMode", 5);
        this.getApplicationContext().startActivity(intent4, bundle);
        FindMyStack();
        if (!strResult.isEmpty()) {
            new ExeCommands().run("am display move-stack " + strResult + " 2", 10000);
            prefs.edit().putBoolean(PREF_HUD_RUNNING, true).commit();
            setMoveButtonsEnabled(true);
        }
    }

    private void FindMyStack() {
        String result = new ExeCommands().run("am stack list", 10000).getResult();
        this.strResult = "";
        String[] chunks = result.split("Stack id=");

        for (String chunk : chunks) {
            if (chunk.contains("ru.link.YNarrows/ru.link.YNarrows.HUDActivity")) {
                int spaceIdx = chunk.indexOf(' ');
                this.strResult = spaceIdx > 0 ? chunk.substring(0, spaceIdx) : chunk;
                break;
            }
        }
    }

    private void setMoveButtonsEnabled(boolean enabled) {
        arrowLeft.setEnabled(enabled);
        arrowUp.setEnabled(enabled);
        arrowDown.setEnabled(enabled);
        arrowRight.setEnabled(enabled);
        speedLeft.setEnabled(enabled);
        speedUp.setEnabled(enabled);
        speedDown.setEnabled(enabled);
        speedRight.setEnabled(enabled);
        streetLeft.setEnabled(enabled);
        streetUp.setEnabled(enabled);
        streetDown.setEnabled(enabled);
        streetRight.setEnabled(enabled);
        streetHeightUp.setEnabled(enabled);
        streetHeightDown.setEnabled(enabled);
    }

    private void sendMove(String fragment, int dx, int dy) {
        Intent intent = new Intent(HUDActivity.MOVE_FRAGMENT_ACTION);
        intent.putExtra(HUDActivity.EXTRA_FRAGMENT, fragment);
        intent.putExtra(HUDActivity.EXTRA_DX, dx);
        intent.putExtra(HUDActivity.EXTRA_DY, dy);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
    }

    private void sendHeight(int delta) {
        Intent intent = new Intent(HUDActivity.UPDATE_HEIGHT_ACTION);
        intent.putExtra("delta", delta);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
    }

    private void updatePositionLabels(TextView arrowPos, TextView speedPos, TextView streetPos) {
        int ax = prefs.getInt("pos_arrow_x", 2);
        int ay = prefs.getInt("pos_arrow_y", 2);
        int sx = prefs.getInt("pos_speed_x", 122);
        int sy = prefs.getInt("pos_speed_y", 2);
        int stx = prefs.getInt("pos_street_x", 2);
        int sty = prefs.getInt("pos_street_y", 112);
        int sth = prefs.getInt("pos_street_h", 72);
        arrowPos.setText("x:" + ax + " y:" + ay);
        speedPos.setText("x:" + sx + " y:" + sy);
        streetPos.setText("x:" + stx + " y:" + sty + " h:" + sth);
    }
}
  
