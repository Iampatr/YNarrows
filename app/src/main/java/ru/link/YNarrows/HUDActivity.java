package ru.link.YNarrows;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import ru.link.YNarrows.utils.ExeCommands;

public class HUDActivity extends AppCompatActivity {
    public static final String PREF_SHOW_BORDERS = "show_borders";
    private static long lastRestoreAttempt = 0;

    @SuppressLint("WrongConstant")
    public static void checkAndRestore(Context context) {

        //Не надо часто проверять. Иногда)))
        long now = System.currentTimeMillis();
        if (now - lastRestoreAttempt < 5000) return;
        lastRestoreAttempt = now;

        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean hud_system_running = prefs.getBoolean("hud_system_running", false);

        if (hud_system_running)    {
            String result = new ExeCommands().run("am stack list", 5000).getResult();
            if (!result.contains("ru.link.YNarrows/ru.link.YNarrows.HUDActivity")) {
                try {
                    Intent intent = new Intent(context, HUDActivity.class);
                    intent.setFlags(268439552);
                    Rect rect = new Rect(0, 0, 1920, 1080);
                    Bundle bundle = ActivityOptions.makeBasic().setLaunchBounds(rect).toBundle();
                    bundle.putInt("android.activity.windowingMode", 5);
                    context.getApplicationContext().startActivity(intent, bundle);

                    String result2 = new ExeCommands().run("am stack list", 5000).getResult();
                    String[] chunks = result2.split("Stack id=");
                    for (String chunk : chunks) {
                        if (chunk.contains("ru.link.YNarrows/ru.link.YNarrows.HUDActivity")) {
                            int spaceIdx = chunk.indexOf(' ');
                            String stackId = spaceIdx > 0 ? chunk.substring(0, spaceIdx) : chunk;
                            new ExeCommands().run("am display move-stack " + stackId + " 2", 5000);
                            break;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    private SharedPreferences prefs;
    private FrameLayout fragmentBorderArrow;
    private FrameLayout fragmentBorderSpeed;
    private FrameLayout fragmentBorderStreet;
    private Handler borderTimer = new Handler();
    private Runnable revertBordersRunnable;
    public static String TitlePrev1 = null;
    public static Boolean ArrowTimerIsStarted = false;
    public static Boolean GISTimerIsStarted = false;
    public static Boolean LightsTimerIsStarted = false;
    public static CountDownTimer ArrowTimer;
    public static CountDownTimer GISTimer;
    public static CountDownTimer LightsTimer;
    public static Icon AppIcon2;
    public static String NotificationTextPrev1 = null;
    private ImageView interceptedNotificationImageView2;
    private ImageView interceptedNotificationImageView3;
    private TextView interceptedNotificationTXTView2;
    private TextView interceptedNotificationTXTView3;
    private TextView interceptedNotificationTXTView4;
    private TextView interceptedNotificationTXTView5;
    public static int image_res = 0;
    private ImageChangeBroadcastReceiver imageChangeBroadcastReceiver;

    private static final String ACTION_NAVIGATION_MANEUVER = "YandexNavi.NAVIGATION_UPDATE_MANEUVER";
    private static final String ACTION_NAVIGATION_DISTANCE = "YandexNavi.NAVIGATION_UPDATE_DISTANCE";
    private static final String ACTION_NAVIGATION_STREET = "YandexNavi.NAVIGATION_UPDATE_STREET";
    private static final String ACTION_NAVIGATION_SPEEDLIMIT = "YandexNavi.NAVIGATION_SPEEDLIMIT";
    private static final String ACTION_NAVIGATION_ETA = "YandexNavi.NAVIGATION_ETA";
    private static final String ACTION_NAVIGATION_RESET = "YandexNavi.NAVIGATION_RESET";


    //YandexNavi Lights
    private static final String NAVIGATION_UPDATE_MAINLIGHT_ON_OF = "YandexNavi.NAVIGATION_UPDATE_MAINLIGHT_ON_OF";
    private static final String ACTION_NAVIGATION_LIGHTS_SEC = "YandexNavi.NAVIGATION_UPDATE_LIGHTS_SEC";
    private static final String ACTION_NAVIGATION_LIGHTS_COLOR = "YandexNavi.NAVIGATION_UPDATE_LIGHTS_COLOR";


    private static final String ACTION_NAVIGATION_OTHER_APPS = "other_apps";
    public static final String CLOSE_HUD_ACTION = "ru.link.YNarrows.CLOSE_HUD";
    public static final String UPDATE_BORDERS_ACTION = "ru.link.YNarrows.UPDATE_BORDERS";
    public static final String MOVE_FRAGMENT_ACTION = "ru.link.YNarrows.MOVE_FRAGMENT";
    public static final String UPDATE_HEIGHT_ACTION = "ru.link.YNarrows.UPDATE_HEIGHT";
    public static final String EXTRA_FRAGMENT = "fragment";
    public static final String EXTRA_DX = "dx";
    public static final String EXTRA_DY = "dy";
    public static final String FRAGMENT_ARROW = "arrow";
    public static final String FRAGMENT_SPEED = "speed";
    public static final String FRAGMENT_STREET = "street";
    private static final int STEP = 5;
    private static final int CONTAINER_W = 700;
    private static final int CONTAINER_H = 187;
    private int arrowX, arrowY;
    private int speedX, speedY;
    private int streetX, streetY;
    private int streetH;
    private boolean isAdjusting = false;


    public class ImageChangeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                Intrinsics.checkNotNullParameter(context, "context");
                Intrinsics.checkNotNullParameter(intent, "intent");
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    switch (Objects.requireNonNull(intent.getAction())) {
                        case ACTION_NAVIGATION_MANEUVER:
                            Intrinsics.checkNotNull(extras);
                            String maneuver = (String) Objects.requireNonNull(extras).get("maneuver");
                            //Toast.makeText(getApplicationContext(), maneuver, Toast.LENGTH_SHORT).show();

                            if (!(Objects.equals(maneuver, "______isPutted___withNotification___"))) {//То значит, что  с экрана прилетело, а не с уведомления
                                image_res = 0;
                                switch (Objects.requireNonNull(maneuver)) {
                                    case "Конец маршрута":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_finish;
                                        break;
                                    case "Поверните налево":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_left;
                                        break;
                                    case "Поверните направо":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_right;
                                        break;
                                    case "Поверните направо 2-й поворот":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_right_second_turn;
                                        break;
                                    case "Поверните налево 2-й поворот":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_left_second_turn;
                                        break;
                                    case "Езжайте прямо":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_stright;
                                        break;
                                    case "Сверните налево":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_slight_left;
                                        break;
                                    case "Сверните направо":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_slight_right;
                                        break;
                                    case "Поверните левее":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.skin_nav_turn_lefter;
                                        break;
                                    case "Поверните правее":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.skin_nav_turn_righter;
                                        break;
                                    case "Круто поверните налево":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_sharp_left;
                                        break;
                                    case "Круто поверните направо":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_sharp_right;
                                        break;
                                    case "Разворот налево":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_uturn_left;
                                        break;
                                    case "Разворот направо":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_uturn_right;
                                        break;
                                    case "Пересадка на паром":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_turn_to_parom;
                                        break;
                                    case "Съезд с парома":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_stright;
                                        break;
                                    case "Промежуточная точка":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.skin_nav_turn_point;
                                        break;
                                    case "Кольцевое движение":
                                    case "Выезд с кольца":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_roundabout;
                                        break;
                                    case "Кольцевое движение 1-й съезд":
                                    case "Выезд с кольца 1-й съезд":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_roundabout_1_v2;
                                        break;
                                    case "Кольцевое движение 2-й съезд":
                                    case "Выезд с кольца 2-й съезд":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_roundabout_2_v2;
                                        break;
                                    case "Кольцевое движение 3-й съезд":
                                    case "Выезд с кольца 3-й съезд":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_roundabout_3_v2;
                                        break;
                                    case "Кольцевое движение 4-й съезд":
                                    case "Выезд с кольца 4-й съезд":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_roundabout_4_v2;
                                        break;
                                    case "Кольцевое движение 5-й съезд":
                                    case "Выезд с кольца 5-й съезд":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_roundabout_5;
                                        break;
                                    case "Кольцевое движение 6-й съезд":
                                    case "Выезд с кольца 6-й съезд":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_roundabout_6;
                                        break;
                                    case "Кольцевое движение 7-й съезд":
                                    case "Выезд с кольца 7-й съезд":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_roundabout_7;
                                        break;
                                    case "Кольцевое движение 8-й съезд":
                                    case "Выезд с кольца 8-й съезд":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_roundabout_8;
                                        break;
                                    case "Кольцевое движение 9-й съезд":
                                    case "Выезд с кольца 9-й съезд":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_roundabout_9;
                                        break;
                                    case "Кольцевое движение 10-й съезд":
                                    case "Выезд с кольца 10-й съезд":
                                        fragmentBorderArrow.setVisibility(VISIBLE);
                                        image_res = R.drawable.nav_roundabout_10;
                                        break;
                                    default:
                                        if (!isAdjusting) fragmentBorderArrow.setVisibility(INVISIBLE);
                                        break;
                                }
                            }
                            if (!(image_res == 0))  {
                                fragmentBorderArrow.setVisibility(VISIBLE);
                            }
                            interceptedNotificationImageView2.setImageResource(image_res);

                            break;

                        case ACTION_NAVIGATION_DISTANCE:
                            Object distance = extras.get("distance");
                            if (distance != null) {
                                interceptedNotificationTXTView3.setText((CharSequence) distance);
                            }

                            if (ArrowTimerIsStarted ){
                                ArrowTimer.cancel();
                                ArrowTimer = null;
                            }
                            StartTimerArrowNotification(context);
                            break;


                        case ACTION_NAVIGATION_STREET:
                            String street = (String) extras.get("street");
                            if (street != null) {
                                if (street.equals(" ")) {
                                    if (!isAdjusting) fragmentBorderStreet.setVisibility(INVISIBLE);
                                }
                                else {
                                    fragmentBorderStreet.setVisibility(VISIBLE);
                                }
                                interceptedNotificationTXTView4.setText(street);
                            }
                            else {
                                if (!isAdjusting) fragmentBorderStreet.setVisibility(INVISIBLE);
                            }
                            break;


                        case NAVIGATION_UPDATE_MAINLIGHT_ON_OF:
                            String Mainlight = (String) extras.get("mainlight_on_off");
                            if (Objects.equals(Mainlight, "8")) {
                                if (!isAdjusting) fragmentBorderSpeed.setVisibility(INVISIBLE);
                                interceptedNotificationImageView3.setVisibility(INVISIBLE);
                                interceptedNotificationTXTView5.setVisibility(INVISIBLE);
                            }
                            else{
                                fragmentBorderSpeed.setVisibility(VISIBLE);
                                interceptedNotificationImageView3.setVisibility(VISIBLE);
                                interceptedNotificationTXTView5.setVisibility(VISIBLE);
                            }

                            if (LightsTimerIsStarted ){
                                LightsTimer.cancel();
                                LightsTimer = null;
                            }
                            StartTimerLightsNotification(context);
                            break;

                        case ACTION_NAVIGATION_LIGHTS_SEC:
                            String sec = (String) extras.get("sec");
                            if (sec != null) {
                                interceptedNotificationTXTView5.setVisibility(VISIBLE);
                                interceptedNotificationTXTView5.setText (sec);
                            }
                            break;

                        case ACTION_NAVIGATION_LIGHTS_COLOR:
                            String signal = (String) extras.get("signal");
                            fragmentBorderSpeed.setVisibility(VISIBLE);
                            interceptedNotificationImageView3.setVisibility(VISIBLE);
                            if (Objects.equals(signal, "green")) {
                                interceptedNotificationImageView3.setImageResource(R.drawable.green);
                            } else if (Objects.equals(signal, "red")) {
                                interceptedNotificationImageView3.setImageResource(R.drawable.red);
                            } else if (Objects.equals(signal, "yellow")) {
                                interceptedNotificationImageView3.setImageResource(R.drawable.yallow);
                            }
                            break;


                        case ACTION_NAVIGATION_SPEEDLIMIT:
                            Object speedlimit = extras.get("speedlimit");
                            //interceptedNotificationTXTView5.setText((CharSequence) speedlimit);
                            break;

                        case ACTION_NAVIGATION_ETA:
                            Object ETA = extras.get("ETA");
                            //interceptedNotificationTXTView5.setText((CharSequence) ETA);
                            break;

                        case ACTION_NAVIGATION_RESET:
                            interceptedNotificationImageView2.setImageIcon(null);
                            interceptedNotificationImageView3.setImageIcon(null);
                            interceptedNotificationTXTView3.setText(null);
                            interceptedNotificationTXTView4.setText(null);
                            interceptedNotificationTXTView5.setText(null);
                            image_res = 0;
                            if (!isAdjusting) fragmentBorderArrow.setVisibility(INVISIBLE);
                            if (!isAdjusting) fragmentBorderSpeed.setVisibility(INVISIBLE);
                            if (!isAdjusting) fragmentBorderStreet.setVisibility(INVISIBLE);
                            break;

                        case ACTION_NAVIGATION_OTHER_APPS:
                            HandleOtherApps(context, intent);
                            break;

                        case CLOSE_HUD_ACTION:
                            String result = new ExeCommands().run("am stack list", 5000).getResult();
                            String[] chunks = result.split("Stack id=");
                            for (String chunk : chunks) {
                                if (chunk.contains("ru.link.YNarrows/ru.link.YNarrows.HUDActivity")) {
                                    int spaceIdx = chunk.indexOf(' ');
                                    String stackId = spaceIdx > 0 ? chunk.substring(0, spaceIdx) : chunk;
                                    new ExeCommands().run("am stack remove " + stackId, 5000);
                                    break;
                                }
                            }
                            finish();
                            break;

                        case UPDATE_BORDERS_ACTION:
                            boolean show = extras.getBoolean(PREF_SHOW_BORDERS, false);
                            int res = show ? R.drawable.inner_fragment_border : R.drawable.inner_fragment_border_transparent;
                            fragmentBorderArrow.setBackgroundResource(res);
                            fragmentBorderSpeed.setBackgroundResource(res);
                            fragmentBorderStreet.setBackgroundResource(res);
                            break;

                        case MOVE_FRAGMENT_ACTION:
                            String frag = extras.getString(EXTRA_FRAGMENT, "");
                            int dx = extras.getInt(EXTRA_DX, 0);
                            int dy = extras.getInt(EXTRA_DY, 0);
                            moveFragment(frag, dx, dy);
                            showBordersTemporarily();
                            break;

                        case UPDATE_HEIGHT_ACTION:
                            int delta = extras.getInt("delta", 0);
                            streetH = Math.max(30, Math.min(streetH + delta, CONTAINER_H));
                            if (streetY + streetH > CONTAINER_H) {
                                streetY = CONTAINER_H - streetH;
                            }
                            applyFragmentPosition(fragmentBorderStreet, streetX, streetY, 240, streetH);
                            prefs.edit().putInt("pos_street_h", streetH).putInt("pos_street_y", streetY).apply();
                            showBordersTemporarily();
                            break;
                    }

                }
            } catch (Exception ignored) {
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String access = Settings.Secure.getString(getContentResolver(), "enabled_accessibility_services");
        if (access != null && !access.contains("ru.link.YNarrows/ru.link.YNarrows.NodeInfoForNavi")) {
            access = access.isEmpty()
                    ? "ru.link.YNarrows/ru.link.YNarrows.NodeInfoForNavi"
                    : access + ":ru.link.YNarrows/ru.link.YNarrows.NodeInfoForNavi";
            Settings.Secure.putString(getContentResolver(), "enabled_accessibility_services", access);
        }

        setContentView(R.layout.activity_hudactivity);

        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        fragmentBorderArrow = findViewById(R.id.fragment_border_arrow);
        fragmentBorderSpeed = findViewById(R.id.fragment_border_speed);
        fragmentBorderStreet = findViewById(R.id.fragment_border_street);

        boolean showBorders = prefs.getBoolean(PREF_SHOW_BORDERS, false);
        int borderRes = showBorders ? R.drawable.inner_fragment_border : R.drawable.inner_fragment_border_transparent;
        fragmentBorderArrow.setBackgroundResource(borderRes);
        fragmentBorderSpeed.setBackgroundResource(borderRes);
        fragmentBorderStreet.setBackgroundResource(borderRes);

        arrowX = prefs.getInt("pos_arrow_x", 2);
        arrowY = prefs.getInt("pos_arrow_y", 2);
        speedX = prefs.getInt("pos_speed_x", 122);
        speedY = prefs.getInt("pos_speed_y", 2);
        streetX = prefs.getInt("pos_street_x", 2);
        streetY = prefs.getInt("pos_street_y", 112);
        streetH = prefs.getInt("pos_street_h", 72);
        applyFragmentPosition(fragmentBorderArrow, arrowX, arrowY, 120, 110);
        applyFragmentPosition(fragmentBorderSpeed, speedX, speedY, 80, 110);
        applyFragmentPosition(fragmentBorderStreet, streetX, streetY, 240, streetH);

        fragmentBorderArrow.setVisibility(INVISIBLE);
        fragmentBorderSpeed.setVisibility(INVISIBLE);
        fragmentBorderStreet.setVisibility(INVISIBLE);

        interceptedNotificationImageView2 = this.findViewById(R.id.intercepted_notification_arrow);
        interceptedNotificationImageView3 = this.findViewById(R.id.intercepted_notification_speed);
        interceptedNotificationTXTView3 = this.findViewById(R.id.notification_text_dist);
        interceptedNotificationTXTView4 = this.findViewById(R.id.notification_text_street);
        interceptedNotificationTXTView5 = this.findViewById(R.id.notification_text_camerad);


        imageChangeBroadcastReceiver = new ImageChangeBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NAVIGATION_MANEUVER);
        intentFilter.addAction(ACTION_NAVIGATION_DISTANCE);
        intentFilter.addAction(ACTION_NAVIGATION_STREET);
        intentFilter.addAction(ACTION_NAVIGATION_RESET);
        intentFilter.addAction(ACTION_NAVIGATION_ETA);
        intentFilter.addAction(NAVIGATION_UPDATE_MAINLIGHT_ON_OF);
        intentFilter.addAction(ACTION_NAVIGATION_LIGHTS_SEC);
        intentFilter.addAction(ACTION_NAVIGATION_LIGHTS_COLOR);
        intentFilter.addAction(ACTION_NAVIGATION_OTHER_APPS);
        intentFilter.addAction(CLOSE_HUD_ACTION);
        intentFilter.addAction(UPDATE_BORDERS_ACTION);
        intentFilter.addAction(MOVE_FRAGMENT_ACTION);
        intentFilter.addAction(UPDATE_HEIGHT_ACTION);

        registerReceiver(imageChangeBroadcastReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(imageChangeBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void HandleOtherApps(Context context, Intent intent) {

        int receivedNotificationCode = intent.getIntExtra("Notification Code",-1);

        Bundle extras = intent.getExtras();
        assert extras != null;

        String Title = extras.getString("Title");
        String NotificationText = extras.getString("Text");

        if(Objects.equals(receivedNotificationCode, 2)) {

            if(!Objects.equals(NotificationText, null)) {
                String EnterKey = System.lineSeparator();
                if (NotificationText.contains("—")) {
                    Title = NotificationText.substring(0, NotificationText.indexOf("—"));
                    NotificationText = NotificationText.substring(NotificationText.indexOf("—")+1, NotificationText.indexOf(EnterKey));
                }
                else {
                    Title = (NotificationText.contains(EnterKey)) ? NotificationText.substring(0, NotificationText.indexOf(EnterKey)) : NotificationText;
                    NotificationText = "";
                }
            }

            if((!Objects.equals(Title, TitlePrev1)) || (!Objects.equals(NotificationText, NotificationTextPrev1))) {


                fragmentBorderArrow.setVisibility(VISIBLE);
                interceptedNotificationImageView2.setImageIcon(AppIcon2);
                interceptedNotificationTXTView3.setText(Title);

                if ((!Objects.equals(NotificationText, NotificationTextPrev1)) || (Objects.equals(GISTimer, null))) {//улица
                    interceptedNotificationTXTView4.setText(NotificationText);
                    if (Objects.equals(NotificationText, "")) {
                        if (!isAdjusting) fragmentBorderStreet.setVisibility(INVISIBLE);
                    }
                    else {
                        fragmentBorderStreet.setVisibility(VISIBLE);
                    }
                }
                if(GISTimer != null) {
                    GISTimer.cancel();
                    GISTimer = null;
                    GISTimerIsStarted = false;
                }
                if(Objects.equals(GISTimerIsStarted, false)) {
                    StartTimerGISNotification (context);
                }
            }

            NotificationTextPrev1 = NotificationText;
            TitlePrev1 = Title;
        }
    }

    private void StartTimerArrowNotification(Context context) {
        ArrowTimerIsStarted = true;
        ArrowTimer = new CountDownTimer(5 * 1000, 3500) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                ArrowTimerIsStarted = false;
                interceptedNotificationImageView2.setImageIcon(null);
                interceptedNotificationImageView3.setImageIcon(null);
                interceptedNotificationTXTView3.setText(null);
                interceptedNotificationTXTView4.setText(null);
                interceptedNotificationTXTView5.setText(null);
                image_res = 0;
                if (!isAdjusting) fragmentBorderArrow.setVisibility(INVISIBLE);
                if (!isAdjusting) fragmentBorderSpeed.setVisibility(INVISIBLE);
                if (!isAdjusting) fragmentBorderStreet.setVisibility(INVISIBLE);


            }
        }.start();
    }

    private void StartTimerGISNotification(Context context) {
        GISTimerIsStarted = true;
        GISTimer = new CountDownTimer(60 * 1000, 3500) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                GISTimerIsStarted = false;
                interceptedNotificationImageView2.setImageIcon(null);
                interceptedNotificationTXTView3.setText("");
                interceptedNotificationTXTView4.setText("");
                if (!isAdjusting) fragmentBorderArrow.setVisibility(INVISIBLE);
                if (!isAdjusting) fragmentBorderStreet.setVisibility(INVISIBLE);
            }
        }.start();
    }

    private void StartTimerLightsNotification(Context context) {
        LightsTimerIsStarted = true;
        LightsTimer = new CountDownTimer(5 * 1000, 3500) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                LightsTimerIsStarted = false;
                interceptedNotificationImageView3.setImageIcon(null);
                interceptedNotificationTXTView5.setText(null);
                if (!isAdjusting) fragmentBorderSpeed.setVisibility(INVISIBLE);
            }
        }.start();
    }

    private void applyFragmentPosition(FrameLayout fragment, int x, int y, int w, int h) {
        int maxX = CONTAINER_W - w;
        int maxY = CONTAINER_H - h;
        x = Math.max(0, Math.min(x, maxX));
        y = Math.max(0, Math.min(y, maxY));
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fragment.getLayoutParams();
        lp.leftMargin = x;
        lp.topMargin = y;
        lp.width = w;
        lp.height = h;
        fragment.setLayoutParams(lp);
    }

    private void showBordersTemporarily() {
        borderTimer.removeCallbacks(revertBordersRunnable);
        isAdjusting = true;
        fragmentBorderArrow.setVisibility(VISIBLE);
        fragmentBorderSpeed.setVisibility(VISIBLE);
        fragmentBorderStreet.setVisibility(VISIBLE);
        int res = R.drawable.inner_fragment_border;
        fragmentBorderArrow.setBackgroundResource(res);
        fragmentBorderSpeed.setBackgroundResource(res);
        fragmentBorderStreet.setBackgroundResource(res);
        revertBordersRunnable = () -> {
            isAdjusting = false;
            boolean show = prefs.getBoolean(PREF_SHOW_BORDERS, false);
            int borderRes = show ? R.drawable.inner_fragment_border : R.drawable.inner_fragment_border_transparent;
            fragmentBorderArrow.setBackgroundResource(borderRes);
            fragmentBorderSpeed.setBackgroundResource(borderRes);
            fragmentBorderStreet.setBackgroundResource(borderRes);
        };
        borderTimer.postDelayed(revertBordersRunnable, 3000);
    }

    private void moveFragment(String tag, int dx, int dy) {
        switch (tag) {
            case FRAGMENT_ARROW: {
                int maxX = CONTAINER_W - 120, maxY = CONTAINER_H - 110;
                arrowX = Math.max(0, Math.min(arrowX + dx, maxX));
                arrowY = Math.max(0, Math.min(arrowY + dy, maxY));
                applyFragmentPosition(fragmentBorderArrow, arrowX, arrowY, 120, 110);
                prefs.edit().putInt("pos_arrow_x", arrowX).putInt("pos_arrow_y", arrowY).apply();
                break;
            }
            case FRAGMENT_SPEED: {
                int maxX = CONTAINER_W - 80, maxY = CONTAINER_H - 110;
                speedX = Math.max(0, Math.min(speedX + dx, maxX));
                speedY = Math.max(0, Math.min(speedY + dy, maxY));
                applyFragmentPosition(fragmentBorderSpeed, speedX, speedY, 80, 110);
                prefs.edit().putInt("pos_speed_x", speedX).putInt("pos_speed_y", speedY).apply();
                break;
            }
            case FRAGMENT_STREET: {
                int maxX = CONTAINER_W - 240, maxY = CONTAINER_H - streetH;
                streetX = Math.max(0, Math.min(streetX + dx, maxX));
                streetY = Math.max(0, Math.min(streetY + dy, maxY));
                applyFragmentPosition(fragmentBorderStreet, streetX, streetY, 240, streetH);
                prefs.edit().putInt("pos_street_x", streetX).putInt("pos_street_y", streetY).apply();
                break;
            }
        }
    }
} 
