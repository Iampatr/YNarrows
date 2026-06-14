package ru.link.YNarrows.utils;

import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.widget.RemoteViews;
import android.widget.Toast;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RemoteViewsUtils {

    private RemoteViewsUtils() {
        // Prevent instantiation
    }

    public static RemoteViews extractRemoteViews(Notification notification, String kind) {
        try {
            Field contentViewField = Notification.class.getDeclaredField(kind);
            contentViewField.setAccessible(true);
            RemoteViews contentView = (RemoteViews) contentViewField.get(notification);
            if (contentView != null) {
                return contentView;
            }

        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
        return null;
    }

    @SuppressLint("DiscouragedPrivateApi")
    public static List<Pair<String, String>> getRemoteViewActions(Context context, RemoteViews remoteViews, int combination) {
        return getRemoteViewActions(context, remoteViews, combination, context.getPackageName());
    }

    @SuppressLint("DiscouragedPrivateApi")
    public static List<Pair<String, String>> getRemoteViewActions(Context context, RemoteViews remoteViews, int combination, String sourcePackage) {
        // Find the private field named "mActions"
        Field actionsField;
        try {
            actionsField = RemoteViews.class.getDeclaredField("mActions");
            actionsField.setAccessible(true);

        } catch (NoSuchFieldException e) {
            // Field not found – return an empty list
            return List.of();
        }

        // Obtain the field value
        Object rawActions;
        try {
            rawActions = actionsField.get(remoteViews);
        } catch (IllegalAccessException e) {
            return List.of();
        }

        // The field is expected to be an ArrayList of unknown objects
        if (!(rawActions instanceof ArrayList<?>)) {
            return List.of();
        }

        @SuppressWarnings("unchecked")
        ArrayList<Object> actions = (ArrayList<Object>) rawActions;

        // Convert each action object to a Pair, discarding nulls

        List<Pair<String, String>> result = new ArrayList<>();
        for (Object action : actions) {
            Pair<String, String> pair = getRemoteViewAction(context, action, combination, sourcePackage);
            if (pair != null) {
                result.add(pair);
            }
        }
        return result;
    }

    private static Pair<String, String> getRemoteViewAction(Context context, Object action, int combination, String sourcePackage) {

        Class<?> actionClass = action.getClass();

        //ТУТ дебажим поля пользовательского представления, чтобы понимать, что искать и что фильтровать далее
        //Field[] declaredFields = actionClass.getDeclaredFields();
        //for (Field field :declaredFields) {
        //field.setAccessible(true);
        //Toast.makeText(context, " " + field, LENGTH_LONG).show();
        //}

        //По результатам дебагга понимаем, что интересует поле methodName. Может называться по-разному в разных версиях Андроидю
        //Отражаем это поле и получаем доступ к нему.
        Field methodNameField;
        try {
            methodNameField = actionClass.getDeclaredField("methodName");
            methodNameField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            return null;
        }

        // Получаем значение(название) действия в конкретном пользовательском представлении в отражении его класса actionClass
        Object methodNameObj;
        try {
            methodNameObj = methodNameField.get(action);
        } catch (IllegalAccessException e) {
            return null;
        }

        // Фильтры полученного значения действия по имени
        String methodName = (String) methodNameObj;

        String prefix = "";

        if (combination == 1){
            switch (Objects.requireNonNull(methodName)) {
                case "setText":
                    prefix = "setText:";
                    break;
                case "setImageResource":
                    prefix = "setImRe:";
                    break;

                default:
                    return null;
            }
        } else if (combination == 2) {// длина префикса действия должна быть ровно 8 символов для корректности дальнейшего извлечения
            switch (Objects.requireNonNull(methodName)) {
                case "setText":
                    prefix = "setText:";
                    break;
                case "setImageResource":
                    prefix = "setImRe:";
                    break;
                //case "setTextColor":
                //    prefix = "setTCol:";
                //    break;
                case "setBackgroundResource":
                    prefix = "setBRes:";
                    break;
                case "setVisibility":
                    prefix = "SetVisi:";
                    break;

                default:
                    return null;
            }
        }
        else {
            return null;
        }


        //Получаем viewID для отобранного действия
        Field[] fieldArr;
        Class<? super Object> superclass = (Class<? super Object>) actionClass.getSuperclass();
        //суперкласс - Action
        //public static abstract class Action implements Parcelable
        //Одно из полей - public private protected int viewId;
        if (superclass == null || (fieldArr = superclass.getDeclaredFields()) == null) {
            fieldArr = new Field[0];
        }

        String ViewID = "";
        for (Field field2 : fieldArr) {
            field2.setAccessible(true);
            if (field2.getName().equals("viewId")) {
                try {
                    int num = field2.getInt(action);
                    try {
                        if (sourcePackage.equals(context.getPackageName())) {
                            ViewID = context.getResources().getResourceEntryName(num);
                        } else {
                            android.content.res.Resources res = context.createPackageContext(sourcePackage, 0).getResources();
                            ViewID = res.getResourceEntryName(num);
                        }
                    } catch (Exception e) {
                        ViewID = String.valueOf(num);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }

        // Доступ к полю value для отфильтрованного значения действия
        Field valueField;
        try {
            valueField = actionClass.getDeclaredField("value");
            valueField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            return null;
        }

        //Получение значения выбранного действия
        Object value;
        String combined;
        try {
            value = valueField.get(action);
            assert value != null;
            if (prefix.equals("setImRe:") || prefix.equals("setBRes:")) {
                int resId = (int) value;
                try {
                    android.content.res.Resources res;
                    if (sourcePackage.equals(context.getPackageName())) {
                        res = context.getResources();
                    } else {
                        res = context.createPackageContext(sourcePackage, 0).getResources();
                    }
                    combined = prefix + res.getResourceEntryName(resId);
                } catch (Exception e) {
                    combined = prefix + value;
                }
            } else {
                combined = prefix + value;
            }
        } catch (IllegalAccessException e) {
            return null;
        }

        return new Pair<>(ViewID, combined);
    }

    private Bundle getExtra (Notification notification) {
        // Access extras using reflections.
        try {
            Field field = notification.getClass().getDeclaredField("extras");
            field.setAccessible(true);
            return (Bundle) field.get(notification);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    public static Object gBitmapCacheGet (RemoteViews remoteViews) {

        Field BitmapCacheField;

        try {
            BitmapCacheField = RemoteViews.class.getDeclaredField("mBitmapCache");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        BitmapCacheField.setAccessible(true);
        Object bitmapCache;

        try {
            bitmapCache = BitmapCacheField.get(remoteViews);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return bitmapCache;
    }

    public static void BitmapCacheIntGet(Context context, Object action) {
        Class<?> actionClass = action.getClass();

        //get = actionClass.getDeclaredFields();
        //for (Field field2 : get) {
        //    field2.setAccessible(true);
        //    Toast.makeText(context, "Поле: " + field2, LENGTH_SHORT).show();
        //}

        Field get2;
        try {
            get2 = actionClass.getDeclaredField("mBitmapMemory");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        get2.setAccessible(true);

        try {
            int number = (int) get2.get(action);
            if (number != -1){
                Toast.makeText(context, "Поле mBitmapMemory: " + number, LENGTH_SHORT).show();
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}



