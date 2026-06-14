package ru.link.YNarrows;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.util.Pair;
import android.widget.RemoteViews;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ru.link.YNarrows.utils.RemoteViewsUtils;

public class NotificationExtractionService extends NotificationListenerService {

    public static boolean ManeuverIdExists = false;
    public static boolean DistanceIdExists = false;
    public static boolean StreetIdExists = false;


    private static final class ApplicationPackageNames {
        public static  final String AnyNAVI_PACK_NAME = "ru.dublgis.dgismobile";
        public static final String YN_PACK_NAME = "ru.yandex.yandexnavi";
    }

    public static final class InterceptedNotificationCode {
        public static final int YN_CODE = 1;
        public static final int AnyNAVI_CODE = 2;
        public static final int OTHER_NOTIFICATIONS_CODE = 3;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn){

        try {
            int notificationCode = matchNotificationCode(sbn);

            switch (notificationCode) {
                case 1:
                    Notification notification = sbn.getNotification();

                    //String kind = "bigContentView";//включает информацию о ETA
                    String kind = "contentView";
                    //String kind = "headsUpContentView";
                    RemoteViews remoteViews = RemoteViewsUtils.extractRemoteViews(notification, kind);

                    if (remoteViews != null) {
                        List<Pair<String, String>> UserContent = RemoteViewsUtils.getRemoteViewActions(getApplicationContext(), remoteViews, 2, sbn.getPackageName());
                        if (UserContent !=null) {

                            Pair[] pairs = UserContent.toArray(new Pair[0]);

                            //Object object2 = RemoteViewsUtils.gBitmapCacheGet(remoteViews);
                            //RemoteViewsUtils.BitmapCacheIntGet(getApplicationContext(), object2);
                            Log.d("Iamkolya: ", Arrays.toString(pairs));

                            //Toast.makeText(getApplicationContext(), "Пары для стрелок: "+ Arrays.toString(pairs), LENGTH_LONG).show();
                            ManeuverIdExists = false;
                            DistanceIdExists = false;
                            StreetIdExists = false;

                            for (int i = 0; i < pairs.length; i++) {
                                Pair<String, String> pair = pairs[i];

                                if (pair != null) {
                                    String ViewID = pair.first;

                                    String prefix = " ";
                                    String value = " ";

                                    String second = pair.second;
                                    if (second !=null && (!second.isEmpty())) {
                                        prefix = second.substring(0, Math.min(8, second.length()));
                                        value = second.length() > 8
                                                ? second.substring(8)
                                                : " ";
                                    }
                                    //Toast.makeText(getApplicationContext(), "Префикс" + prefix, LENGTH_SHORT).show();
                                    //Toast.makeText(getApplicationContext(), "Значение" + value, LENGTH_SHORT).show();

                                    switch (ViewID) {

                                        case "primaryIcon"://Камера

                                            if (prefix.equals("setImRe:")) {
                                                switch (value) {
                                                    case "road_alerts_camera_32":
                                                        HUDActivity.image_res = R.drawable.camera1;
                                                        break;
                                                    case "road_alerts_accident_32":
                                                        HUDActivity.image_res = R.drawable.warnings_dtp;
                                                        break;
                                                    case "road_alerts_road_works_32":
                                                        HUDActivity.image_res = R.drawable.warnings_works;
                                                        break;
                                                    case "road_alerts_other_32":
                                                        HUDActivity.image_res = R.drawable.warnings_other;
                                                        break;
                                                    default:
                                                        HUDActivity.image_res = R.drawable.mts_logo;
                                                        //Toast.makeText(getApplicationContext(), "Нет картинки для маневра" + pair, LENGTH_SHORT).show();
                                                        break;
                                                }
                                                Intent intent10 = new Intent("YandexNavi.NAVIGATION_UPDATE_MANEUVER");
                                                intent10.setPackage("ru.link.YNarrows");
                                                intent10.putExtra("maneuver", "______isPutted___withNotification___");
                                                getApplicationContext().sendBroadcast(intent10);
                                            }
                                            break;

                                        case "primaryIconTinted"://Маневр

                                            if (prefix.equals("setImRe:")) {
                                                ManeuverIdExists = true;
                                                switch (value) {
                                                    case "notification_board_ferry_sdl":
                                                        HUDActivity.image_res = R.drawable.nav_turn_to_parom;
                                                        break;
                                                    case "notification_enter_roundabout_sdl":
                                                        HUDActivity.image_res = R.drawable.nav_roundabout;
                                                        break;
                                                    case "notification_exit_left_sdl":
                                                        HUDActivity.image_res = R.drawable.nav_fork_left;
                                                        break;
                                                    case "notification_exit_right_sdl":
                                                        HUDActivity.image_res = R.drawable.nav_fork_right;
                                                        break;
                                                    case "notification_finish_sdl":
                                                        HUDActivity.image_res = R.drawable.nav_finish;
                                                        break;
                                                    case "notification_fork_left_sdl":
                                                        HUDActivity.image_res = R.drawable.skin_nav_turn_lefter;
                                                        break;
                                                    case "notification_fork_right_sdl":
                                                        HUDActivity.image_res = R.drawable.skin_nav_turn_righter;
                                                        break;
                                                    case "notification_hard_left_sdl":
                                                        HUDActivity.image_res = R.drawable.nav_sharp_left;
                                                        break;
                                                    case "notification_hard_right_sdl":
                                                        HUDActivity.image_res = R.drawable.nav_sharp_right;
                                                        break;
                                                    case "notification_leave_roundabout_sdl":
                                                        HUDActivity.image_res = R.drawable.nav_roundabout;
                                                        break;
                                                    case "notification_left_sdl":
                                                        HUDActivity.image_res = R.drawable.nav_left;
                                                        break;
                                                    case "notification_right_sdl":
                                                        HUDActivity.image_res = R.drawable.nav_right;
                                                        break;
                                                    case "notification_slight_left_sdl":
                                                        HUDActivity.image_res = R.drawable.nav_slight_left;
                                                        break;
                                                    case "notification_slight_right_sdl":
                                                        HUDActivity.image_res = R.drawable.nav_slight_right;
                                                        break;
                                                    case "notification_straight_sdl":
                                                        HUDActivity.image_res = R.drawable.nav_stright;
                                                        break;
                                                    case "notification_uturn_left_sdl":
                                                        HUDActivity.image_res = R.drawable.nav_uturn_left;
                                                        break;
                                                    case "notification_uturn_right_sdl":
                                                        HUDActivity.image_res = R.drawable.nav_uturn_right;
                                                        break;
                                                    default:
                                                        HUDActivity.image_res = R.drawable.mts_logo;
                                                        //Toast.makeText(getApplicationContext(), "Нет картинки для маневра" + pair, LENGTH_SHORT).show();
                                                        break;
                                                }

                                                Intent intent3 = new Intent("YandexNavi.NAVIGATION_UPDATE_MANEUVER");
                                                intent3.setPackage("ru.link.YNarrows");
                                                intent3.putExtra("maneuver", "______isPutted___withNotification___");
                                                getApplicationContext().sendBroadcast(intent3);
                                            }
                                            break;

                                        case "titleView"://Расстояние до маневра
                                            if (prefix.equals("setText:")) {
                                                DistanceIdExists = true;
                                                Intent intent2 = new Intent("YandexNavi.NAVIGATION_UPDATE_DISTANCE");
                                                intent2.setPackage("ru.link.YNarrows");
                                                intent2.putExtra("distance", value);
                                                getApplicationContext().sendBroadcast(intent2);
                                            }
                                            break;

                                        case "descriptionView"://Улица

                                            if (prefix.equals("setText:")) {
                                                StreetIdExists = true;
                                                if ((value.equals("Камера контроля скорости"))
                                                        || value.equals("Направо")
                                                        || value.equals("Налево")
                                                        || value.equals("Почти на месте")
                                                        || value.equals("Кольцевое движение")
                                                ){
                                                    value = " ";
                                                }
                                                Intent intent4 = new Intent("YandexNavi.NAVIGATION_UPDATE_STREET");
                                                intent4.setPackage("ru.link.YNarrows");
                                                intent4.putExtra("street", value);
                                                getApplicationContext().sendBroadcast(intent4);
                                            }
                                            break;

                                        case "traffic_light_view": //сокращенный oldSDK (ниже 31)
                                            //это для contentView   в зависимости от объекта рефлексии
                                            if (prefix.equals("SetVisi:")) {//Выключить/включить вьюшку
                                                Intent intent5 = new Intent("YandexNavi.NAVIGATION_UPDATE_MAINLIGHT_ON_OF");
                                                intent5.setPackage("ru.link.YNarrows");
                                                intent5.putExtra("mainlight_on_off", value);
                                                getApplicationContext().sendBroadcast(intent5);
                                            }
                                            break;

                                        case "traffic_light_view_expanded": //расширенный
                                            //это для  bigcontentView   в зависимости от объекта рефлексии
                                            break;

                                        case "traffic_light_data": // для сокращенного
                                            //Toast.makeText(getApplicationContext(), "КЕЙС цвета = сокр.OLD", LENGTH_SHORT).show();

                                            switch (prefix) {
                                                case "setBRes:"://Цвет сфетофора
                                                    //Toast.makeText(getApplicationContext(), "Есть цвет. Значение: " + value  , LENGTH_SHORT).show();
                                                    switch (value) {
                                                        case "traffic_light_background_red_main":
                                                        case "traffic_light_background_red_counter":
                                                            Intent intent7 = new Intent("YandexNavi.NAVIGATION_UPDATE_LIGHTS_COLOR");
                                                            intent7.setPackage("ru.link.YNarrows");
                                                            intent7.putExtra("signal", "red");
                                                            getApplicationContext().sendBroadcast(intent7);
                                                            break;
                                                        case "traffic_light_background_green_main":
                                                        case "traffic_light_background_green_counter":
                                                            Intent intent8 = new Intent("YandexNavi.NAVIGATION_UPDATE_LIGHTS_COLOR");
                                                            intent8.setPackage("ru.link.YNarrows");
                                                            intent8.putExtra("signal", "green");
                                                            getApplicationContext().sendBroadcast(intent8);
                                                            break;
                                                        case "traffic_light_background_yellow_main":
                                                        case "traffic_light_background_yellow_counter":
                                                            Intent intent9 = new Intent("YandexNavi.NAVIGATION_UPDATE_LIGHTS_COLOR");
                                                            intent9.setPackage("ru.link.YNarrows");
                                                            intent9.putExtra("signal", "yellow");
                                                            getApplicationContext().sendBroadcast(intent9);
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                    break;

                                                case "setText:": //Показать сек.
                                                    if (value.equals("0")){
                                                        value = " ";
                                                    }
                                                    Intent intent6 = new Intent("YandexNavi.NAVIGATION_UPDATE_LIGHTS_SEC");
                                                    intent6.setPackage("ru.link.YNarrows");
                                                    intent6.putExtra("sec", value);
                                                    getApplicationContext().sendBroadcast(intent6);
                                                    break;
                                                default:
                                                    break;
                                            }
                                            break;

                                        case "traffic_light_expanded":
                                            if (prefix.equals("setBRes:")) {
                                                switch (value) {
                                                    case "traffic_light_background_red_main":
                                                    case "traffic_light_background_red_counter":
                                                        Intent intent7 = new Intent("YandexNavi.NAVIGATION_UPDATE_LIGHTS_COLOR");
                                                        intent7.setPackage("ru.link.YNarrows");
                                                        intent7.putExtra("signal", "red");
                                                        getApplicationContext().sendBroadcast(intent7);
                                                        break;
                                                    case "traffic_light_background_green_main":
                                                    case "traffic_light_background_green_counter":
                                                        Intent intent8 = new Intent("YandexNavi.NAVIGATION_UPDATE_LIGHTS_COLOR");
                                                        intent8.setPackage("ru.link.YNarrows");
                                                        intent8.putExtra("signal", "green");
                                                        getApplicationContext().sendBroadcast(intent8);
                                                        break;
                                                    case "traffic_light_background_yellow_main":
                                                    case "traffic_light_background_yellow_counter":
                                                        Intent intent9 = new Intent("YandexNavi.NAVIGATION_UPDATE_LIGHTS_COLOR");
                                                        intent9.setPackage("ru.link.YNarrows");
                                                        intent9.putExtra("signal", "yellow");
                                                        getApplicationContext().sendBroadcast(intent9);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                            break;

                                        case "traffic_light_data_expanded": // для расширенного
                                            if (prefix.equals("setText:")) { //Показать сек.
                                                if (value.equals("0")) {
                                                    value = " ";
                                                }
                                                Intent intent6 = new Intent("YandexNavi.NAVIGATION_UPDATE_LIGHTS_SEC");
                                                intent6.setPackage("ru.link.YNarrows");
                                                intent6.putExtra("sec", value);
                                                getApplicationContext().sendBroadcast(intent6);
                                            }
                                            break;

                                        case "traffic_light_expanded_yellow":
                                            if (prefix.equals("SetVisi:")) {//Сигнад сфетофора желтый Old expanded
                                                // 0 - желтый
                                                if (value.equals("0")) {
                                                    Intent intent9 = new Intent("YandexNavi.NAVIGATION_UPDATE_LIGHTS_COLOR");
                                                    intent9.setPackage("ru.link.YNarrows");
                                                    intent9.putExtra("signal", "yellow");
                                                    getApplicationContext().sendBroadcast(intent9);
                                                    //Toast.makeText(getApplicationContext(), "Желтый старый", LENGTH_SHORT).show();
                                                }
                                            }
                                            break;

                                        default:

                                            break;
                                    }
                                }
                            }

                            if (DistanceIdExists && !StreetIdExists) {//Маневр есть, а улицы нет
                                Intent intent4 = new Intent("YandexNavi.NAVIGATION_UPDATE_STREET");
                                intent4.setPackage("ru.link.YNarrows");
                                intent4.putExtra("street", " ");
                                getApplicationContext().sendBroadcast(intent4);
                            }
                        }
                    }
                    break;


                case 2://Стрелки 2 ГИС

                    Intent intent = new Intent("other_apps");
                    intent.putExtra("Notification Code", notificationCode);

                    Bundle extras = sbn.getNotification().extras;
                    int notificationID = sbn.getId();
                    intent.putExtra("ID", notificationID);

                    String Title = extras.getString("android.title");
                    intent.putExtra("Title", Title);
                    String NotificationText = Objects.requireNonNull(extras.getCharSequence("android.text")).toString();
                    intent.putExtra("Text", NotificationText);

                    HUDActivity.AppIcon2 = sbn.getNotification().getLargeIcon();
                    sendBroadcast(intent);
                    break;

                case 3://Зарезервирлвано

                    break;

                default://Ошибка в назначении
                    break;
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        int notificationCode = matchNotificationCode(sbn);
        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {

            StatusBarNotification[] activeNotifications = this.getActiveNotifications();

            if(activeNotifications != null) {
                for (StatusBarNotification activeNotification : activeNotifications) {
                    if (notificationCode == matchNotificationCode(activeNotification)) {
                        //Удалить
                    }
                }
            }
        }
    }

    private int matchNotificationCode(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        switch (packageName) {
            case ApplicationPackageNames.YN_PACK_NAME:
                return (InterceptedNotificationCode.YN_CODE);
            case ApplicationPackageNames.AnyNAVI_PACK_NAME:
                return (InterceptedNotificationCode.AnyNAVI_CODE);
            default:
                return (InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE);
        }
    }
}









