package ru.link.YNarrows;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;

public class HUDActivity extends AppCompatActivity {
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
                                        image_res = R.drawable.nav_finish;
                                        break;
                                    case "Поверните налево":
                                        image_res = R.drawable.nav_left;
                                        break;
                                    case "Поверните направо":
                                        image_res = R.drawable.nav_right;
                                        break;
                                    case "Поверните направо 2-й поворот":
                                        image_res = R.drawable.nav_right_second_turn;
                                        break;
                                    case "Поверните налево 2-й поворот":
                                        image_res = R.drawable.nav_left_second_turn;
                                        break;
                                    case "Езжайте прямо":
                                        image_res = R.drawable.nav_stright;
                                        break;
                                    case "Сверните налево":
                                        image_res = R.drawable.nav_slight_left;
                                        break;
                                    case "Сверните направо":
                                        image_res = R.drawable.nav_slight_right;
                                        break;
                                    case "Поверните левее":
                                        image_res = R.drawable.skin_nav_turn_lefter;
                                        break;
                                    case "Поверните правее":
                                        image_res = R.drawable.skin_nav_turn_righter;
                                        break;
                                    case "Круто поверните налево":
                                        image_res = R.drawable.nav_sharp_left;
                                        break;
                                    case "Круто поверните направо":
                                        image_res = R.drawable.nav_sharp_right;
                                        break;
                                    case "Разворот налево":
                                        image_res = R.drawable.nav_uturn_left;
                                        break;
                                    case "Разворот направо":
                                        image_res = R.drawable.nav_uturn_right;
                                        break;
                                    case "Пересадка на паром":
                                        image_res = R.drawable.nav_turn_to_parom;
                                        break;
                                    case "Съезд с парома":
                                        image_res = R.drawable.nav_stright;
                                        break;
                                    case "Промежуточная точка":
                                        image_res = R.drawable.skin_nav_turn_point;
                                        break;
                                    case "Кольцевое движение":
                                    case "Выезд с кольца":
                                        image_res = R.drawable.nav_roundabout;
                                        break;
                                    case "Кольцевое движение 1-й съезд":
                                    case "Выезд с кольца 1-й съезд":
                                        image_res = R.drawable.nav_roundabout_1_v2;
                                        break;
                                    case "Кольцевое движение 2-й съезд":
                                    case "Выезд с кольца 2-й съезд":
                                        image_res = R.drawable.nav_roundabout_2_v2;
                                        break;
                                    case "Кольцевое движение 3-й съезд":
                                    case "Выезд с кольца 3-й съезд":
                                        image_res = R.drawable.nav_roundabout_3_v2;
                                        break;
                                    case "Кольцевое движение 4-й съезд":
                                    case "Выезд с кольца 4-й съезд":
                                        image_res = R.drawable.nav_roundabout_4_v2;
                                        break;
                                    case "Кольцевое движение 5-й съезд":
                                    case "Выезд с кольца 5-й съезд":
                                        image_res = R.drawable.nav_roundabout_5;
                                        break;
                                    case "Кольцевое движение 6-й съезд":
                                    case "Выезд с кольца 6-й съезд":
                                        image_res = R.drawable.nav_roundabout_6;
                                        break;
                                    case "Кольцевое движение 7-й съезд":
                                    case "Выезд с кольца 7-й съезд":
                                        image_res = R.drawable.nav_roundabout_7;
                                        break;
                                    case "Кольцевое движение 8-й съезд":
                                    case "Выезд с кольца 8-й съезд":
                                        image_res = R.drawable.nav_roundabout_8;
                                        break;
                                    case "Кольцевое движение 9-й съезд":
                                    case "Выезд с кольца 9-й съезд":
                                        image_res = R.drawable.nav_roundabout_9;
                                        break;
                                    case "Кольцевое движение 10-й съезд":
                                    case "Выезд с кольца 10-й съезд":
                                        image_res = R.drawable.nav_roundabout_10;
                                        break;
                                    default:
                                        break;
                                }
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
                                interceptedNotificationTXTView4.setText(street);
                            }
                            break;


                        case NAVIGATION_UPDATE_MAINLIGHT_ON_OF:
                            String Mainlight = (String) extras.get("mainlight_on_off");
                            if (Objects.equals(Mainlight, "8")) {
                                interceptedNotificationImageView3.setVisibility(INVISIBLE);
                                interceptedNotificationTXTView5.setVisibility(INVISIBLE);
                            }
                            else{
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
                            break;

                        case ACTION_NAVIGATION_OTHER_APPS:
                            HandleOtherApps(context, intent);
                            break;

                        case CLOSE_HUD_ACTION:
                            finish();
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

        setContentView(R.layout.activity_hudactivity);

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
                    //NotificationText = (NotificationText.contains(EnterKey)) ? NotificationText.substring(NotificationText.indexOf(EnterKey)+1) : NotificationText;
                    NotificationText = "";
                }
            }

            if((!Objects.equals(Title, TitlePrev1)) || (!Objects.equals(NotificationText, NotificationTextPrev1))) {

                interceptedNotificationImageView2.setImageIcon(AppIcon2);
                interceptedNotificationTXTView3.setText(Title);
                interceptedNotificationTXTView3.setSelected(true);
                if ((!Objects.equals(NotificationText, NotificationTextPrev1)) || (Objects.equals(GISTimer, null))) {
                    interceptedNotificationTXTView4.setText(NotificationText);
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
            }
        }.start();
    }
}