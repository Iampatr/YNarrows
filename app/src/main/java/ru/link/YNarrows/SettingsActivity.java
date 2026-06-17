package ru.link.YNarrows;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import ru.link.YNarrows.utils.ExeCommands;

public class SettingsActivity extends AppCompatActivity {
    private String strResult = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView statusText = findViewById(R.id.status_text);
        Button actionButton = findViewById(R.id.action_button);
        Button closeHudButton = findViewById(R.id.close_hud_button);

        closeHudButton.setOnClickListener(v -> {
            Intent intent = new Intent(HUDActivity.CLOSE_HUD_ACTION);
            intent.putExtra("close", true);
            intent.setPackage(getPackageName());
            sendBroadcast(intent);
        });

        if (isNotificationListenerEnabled()) {
            statusText.setText("Статус: права Notification Listener предоставлены");
            actionButton.setText("Запустить HUD на втором дисплее");
            actionButton.setOnClickListener(v -> startHUD());
        } else {
            statusText.setText("Статус: требуется включить Notification Listener");
            actionButton.setText("Открыть настройки");
            actionButton.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
        }
    }

    private boolean isNotificationListenerEnabled() {
        String enabledListeners = Settings.Secure.getString(
                getContentResolver(), "enabled_notification_listeners");
        return enabledListeners != null && enabledListeners.contains(getPackageName());
    }

    @SuppressLint("WrongConstant")
    private void startHUD() {
        Intent intent4 = new Intent(this, HUDActivity.class);
        intent4.setFlags(268439552);
        Rect rect = new Rect(0, 0, 1920, 907);
        Bundle bundle  = ActivityOptions.makeBasic().setLaunchBounds(rect).toBundle();
        bundle.putInt("android.activity.windowingMode", 5);
        this.getApplicationContext().startActivity(intent4, bundle);
        FindMyStack();
        if (!strResult.isEmpty()) {
            String str2 = new ExeCommands().run("am display move-stack " + strResult + " 2", 10000).getResult();
            Toast.makeText(getApplicationContext(), str2, Toast.LENGTH_SHORT).show();
        }
        else {
            String str2 = new ExeCommands().run("am display move-stack " + strResult + " 0", 10000).getResult();
            Toast.makeText(getApplicationContext(), str2, Toast.LENGTH_SHORT).show();
        }


    }

    private void FindMyStack() {
        String result = new ExeCommands().run("am stack list", 10000).getResult();
        //Toast.makeText(this, "Дамп: " + result, Toast.LENGTH_LONG).show();

        this.strResult = "";
        String[] chunks = result.split("Stack id=");
        //Toast.makeText(this, "Чанки: " + Arrays.toString(chunks), Toast.LENGTH_LONG).show();

        for (String chunk : chunks) {
            //Toast.makeText(this, "Чанк: " + chunk, Toast.LENGTH_LONG).show();

            if (chunk.contains("ru.link.YNarrows/ru.link.YNarrows.HUDActivity")) {
                //Toast.makeText(this, "Вот наш чанк: " + chunk, Toast.LENGTH_LONG).show();
                int spaceIdx = chunk.indexOf(' ');
                this.strResult = spaceIdx > 0 ? chunk.substring(0, spaceIdx) : chunk;
                break;
            }
        }
        Toast.makeText(this, "Я в стэке номер: " + this.strResult, Toast.LENGTH_LONG).show();
    }















}
