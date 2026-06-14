package ru.link.YNarrows;

import static android.widget.Toast.LENGTH_SHORT;
import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.util.List;

public class NodeInfoForNavi extends AccessibilityService {
    public static boolean UserMode = true;
    public String maneuver = " ";
    public String distance = " ";
    public String street = " ";
    public String remdistance = " ";
    public String arrivalTime = " ";
    public String remTime = " ";
    public String speedlimit = " ";
    public String road = " ";
    public String maneuverNext = " ";
    public String distanceNext = " ";
    public String maneuverUpcoming = " ";
    public String distanceUpcoming = " ";


    public boolean maneuverVEx = false;
    public boolean distanceVEx = false;
    public boolean streetVEx = false;
    public boolean remdistanceVEx = false;
    public boolean arrivalTimeVEx = false;
    public boolean remTimeVEx = false;
    public boolean speedlimitVEx = false;
    public boolean NaviMode = false;

    private void reset() {
        this.maneuver = " ";
        this.distance = " ";
        this.street = " ";
        this.remdistance = " ";
        this.arrivalTime = " ";
        this.remTime = " ";
        this.speedlimit = " ";

        this.road = " ";
        this.maneuverNext = " ";
        this.distanceNext = " ";
        this.maneuverUpcoming = " ";
        this.distanceUpcoming = " ";

        this.maneuverVEx = false;
        this.distanceVEx = false;
        this.streetVEx = false;
        this.remdistanceVEx = false;
        this.arrivalTimeVEx = false;
        this.remTimeVEx = false;
        this.speedlimitVEx = false;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        try {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                String packageName = String.valueOf(event.getPackageName());
                //ComponentName componentName = ReflectUtil.getTopActivityByDisplayId(0);
                Toast.makeText(getApplicationContext(), packageName, LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), ""+ componentName, LENGTH_SHORT).show();

                if (!(packageName.equals("ru.yandex.yandexnavi")) &&
                        !(packageName.equals("com.geely.screengestureservice")) &&
                        !(packageName.equals("com.geely.hud")) &&
                        !(packageName.equals("com.ecarx.pas.avm")) &&
                        !(packageName.equals("com.geely.notification")) ) {
                    if (NaviMode){
                        reset();
                        NaviMode = false;
                        Intent intent = new Intent("YandexNavi.NAVIGATION_RESET");
                        intent.putExtra("someextra", packageName);
                        getApplicationContext().sendBroadcast(intent);
                        return;
                    }
                }


                NaviMode = false;
                return;
            }

            AccessibilityNodeInfo nodeInfo = event.getSource();
            if (UserMode) {
                if (nodeInfo != null) {
                    Object PackName = nodeInfo.getPackageName();
                    if (String.valueOf(PackName).equals("ru.yandex.yandexnavi")) {

                        //перед каждым событием подразумеваем, что нужный node отсутсвует
                        maneuverVEx = false;
                        distanceVEx = false;
                        streetVEx = false;

                        remdistanceVEx = false;
                        arrivalTimeVEx = false;
                        remTimeVEx = false;

                        speedlimitVEx = false;

                        List<AccessibilityNodeInfo> maneuverNodeList = nodeInfo.findAccessibilityNodeInfosByViewId("ru.yandex.yandexnavi:id/image_maneuverballoon_maneuver");
                        if (!(maneuverNodeList == null)) {
                            NaviMode = true;
                            maneuverVEx = true;

                            for (AccessibilityNodeInfo maneuverNode : maneuverNodeList) {
                                maneuver = (String) maneuverNode.getContentDescription();
                                if (maneuver == null) {
                                    maneuver = ">>>";
                                }
                                List<AccessibilityNodeInfo> exitNodeList = nodeInfo.findAccessibilityNodeInfosByViewId("ru.yandex.yandexnavi:id/exit_number_text");
                                if (!(exitNodeList == null)) {
                                    for (AccessibilityNodeInfo exitNode : exitNodeList) {
                                        String exit = (String) exitNode.getText();
                                        if (exit != null )  {
                                            maneuver = maneuver + " " + exit;
                                        }
                                        exitNode.recycle();
                                        break;
                                    }
                                }
                                maneuverNode.recycle();
                                break;
                            }

                            Intent intent = new Intent("YandexNavi.NAVIGATION_UPDATE_MANEUVER");
                            intent.putExtra("maneuver", maneuver);
                            getApplicationContext().sendBroadcast(intent);
                        }

                        List<AccessibilityNodeInfo> distanceNodeList = nodeInfo.findAccessibilityNodeInfosByViewId("ru.yandex.yandexnavi:id/text_maneuverballoon_distance");
                        if (!(distanceNodeList == null)) {
                            NaviMode = true;
                            distanceVEx = true;

                            for (AccessibilityNodeInfo distanceNode : distanceNodeList) {
                                distance = (String) distanceNode.getText();
                                if (distance == null) {
                                    distance = " ";
                                }
                                List<AccessibilityNodeInfo> measureNodeList = nodeInfo.findAccessibilityNodeInfosByViewId("ru.yandex.yandexnavi:id/text_maneuverballoon_metrics");
                                if (!(measureNodeList == null)) {
                                    for (AccessibilityNodeInfo measureNode : measureNodeList) {
                                        String meas = (String) measureNode.getText();
                                        if (meas != null) {
                                            distance = distance + meas;
                                        }
                                        measureNode.recycle();
                                        break;
                                    }
                                }
                                distanceNode.recycle();
                                break;
                            }
                            Intent intent = new Intent("YandexNavi.NAVIGATION_UPDATE_DISTANCE");
                            intent.putExtra("distance", distance);
                            getApplicationContext().sendBroadcast(intent);
                        }

                        List<AccessibilityNodeInfo> streetNodeList = nodeInfo.findAccessibilityNodeInfosByViewId("ru.yandex.yandexnavi:id/text_nextstreet");
                        if (!(streetNodeList == null)) {
                            NaviMode = true;
                            streetVEx = true;
                            for (AccessibilityNodeInfo streetNode : streetNodeList) {
                                street = (String) streetNode.getText();
                                if (street == null) {
                                    street = " ";
                                }
                                streetNode.recycle();
                                break;
                            }
                            Intent intent = new Intent("YandexNavi.NAVIGATION_UPDATE_STREET");
                            intent.putExtra("street", street);
                            getApplicationContext().sendBroadcast(intent);
                        }
                        else  {//Нет улицы при наличии маневра и расстояния
                            if (maneuverVEx || distanceVEx) {//Но если при этом есть маневр или дистанция, то улицу стереть
                                Intent intent = new Intent("YandexNavi.NAVIGATION_UPDATE_STREET");
                                intent.putExtra("street", " ");
                                getApplicationContext().sendBroadcast(intent);
                            }
                        }



// ПОка что НЕ вывожу
                        List<AccessibilityNodeInfo> roadNodeList = nodeInfo.findAccessibilityNodeInfosByViewId("ru.yandex.yandexnavi:id/roadsign_container");
                        if (!(roadNodeList == null)) {
                            NaviMode = true;
                            streetVEx = true;
                            for (AccessibilityNodeInfo roadNode : roadNodeList) {
                                road = (String) roadNode.getText();
                                if (road == null) {
                                    road = " ";
                                }
                                roadNode.recycle();
                                break;
                            }
                            //Intent intent = new Intent("YandexNavi.NAVIGATION_UPDATE_STREET_CURRENT");
                            //intent.setPackage("ru.link.YNarrows");
                            //intent.putExtra("street", road);
                            //getApplicationContext().sendBroadcast(intent);
                        }

                        //textview_eta_distance
                        //textview_eta_arrival
                        //textview_eta_time
                        //text_speedlimit

                    }
                    nodeInfo.recycle();
                }
            }

            else {//Debug mode - анализ нодов произвольного приложения в форграунде на ходу
                //Toast.makeText(getApplicationContext(), "Есть такое: " + event, Toast.LENGTH_SHORT).show();
                if (nodeInfo != null) {
                    //Object PackName = nodeInfo.getPackageName();
                    //Toast.makeText(getApplicationContext(), "ЛИСТ: " + PackName, Toast.LENGTH_SHORT).show();

                    nodeInfo.refresh();
                    Object Deskr = nodeInfo.getContentDescription();
                    if (Deskr != null){
                        Toast.makeText(getApplicationContext(), "dsc: " + Deskr+ " " + nodeInfo.getViewIdResourceName(), LENGTH_SHORT).show();
                    }

                    Object TEXT = nodeInfo.getText();
                    if (TEXT != null){
                        Toast.makeText(getApplicationContext(), "txt: " + TEXT+ " " + nodeInfo.getViewIdResourceName(), LENGTH_SHORT).show();
                    }

                    int count = nodeInfo.getChildCount();
                    for (int i = 0; i < count; i++) {
                        AccessibilityNodeInfo child = nodeInfo.getChild(i);
                        if (child != null) {


                            Object Deskr2 = child.getContentDescription();
                            if (Deskr2 != null){
                                Toast.makeText(getApplicationContext(), "Ch1 dsc: " + Deskr2+ " " + nodeInfo.getViewIdResourceName()+ " " + child.getViewIdResourceName(), LENGTH_SHORT).show();
                            }
                            Object TEXT2 = child.getText();
                            if (TEXT2 != null){
                                Toast.makeText(getApplicationContext(), "Ch1 txt: " + TEXT2+ " " + nodeInfo.getViewIdResourceName()+ " " + child.getViewIdResourceName(), LENGTH_SHORT).show();
                            }

                            int count2 = child.getChildCount();
                            for (int i2 = 0; i2 < count2; i2++) {
                                AccessibilityNodeInfo child2 = child.getChild(i2);
                                if (child2 != null) {

                                    Object Deskr3 = child2.getContentDescription();
                                    if (Deskr3 != null){
                                        Toast.makeText(getApplicationContext(), "Ch2 dsc: " + Deskr3+ " " + nodeInfo.getViewIdResourceName()+ " " + child.getViewIdResourceName()+ " " + child2.getViewIdResourceName(), LENGTH_SHORT).show();
                                    }
                                    Object TEXT3 = child2.getText();
                                    if (TEXT3 != null){
                                        Toast.makeText(getApplicationContext(), "Ch2 txt: " + TEXT3+ " " + nodeInfo.getViewIdResourceName()+ " " + child.getViewIdResourceName()+ " " + child2.getViewIdResourceName(), LENGTH_SHORT).show();
                                    }
                                    int count3 = child2.getChildCount();
                                    for (int i3 = 0; i3 < count3; i3++) {
                                        AccessibilityNodeInfo child3 = child2.getChild(i3);
                                        if (child3 != null) {

                                            Object Deskr4 = child3.getContentDescription();
                                            if (Deskr4 != null){
                                                Toast.makeText(getApplicationContext(), "Ch3 dsc: " + Deskr4+ " " + nodeInfo.getViewIdResourceName()+ " " + child.getViewIdResourceName()+ " " + child2.getViewIdResourceName()+ " " + child3.getViewIdResourceName(), LENGTH_SHORT).show();
                                            }
                                            Object TEXT4 = child3.getText();
                                            if (TEXT4 != null){
                                                Toast.makeText(getApplicationContext(), "Ch3 txt: " + TEXT4+ " " + nodeInfo.getViewIdResourceName()+ " " + child.getViewIdResourceName()+ " " + child2.getViewIdResourceName()+ " " + child3.getViewIdResourceName(), LENGTH_SHORT).show();
                                            }
                                            int count4 = child3.getChildCount();
                                            for (int i4 = 0; i4 < count4; i4++) {
                                                AccessibilityNodeInfo child4 = child3.getChild(i4);
                                                if (child4 != null) {

                                                    Object Deskr5 = child4.getContentDescription();
                                                    if (Deskr5 != null){
                                                        Toast.makeText(getApplicationContext(), "Ch4 dsc: " + Deskr5+ " " + nodeInfo.getViewIdResourceName()+ " " + child.getViewIdResourceName()+ " " + child2.getViewIdResourceName()+ " " + child3.getViewIdResourceName()+ " " + child4.getViewIdResourceName(), LENGTH_SHORT).show();
                                                    }


                                                    Object TEXT5 = child4.getText();
                                                    if (TEXT5 != null) {
                                                        Toast.makeText(getApplicationContext(), "Ch4 txt: " + TEXT5+ " " + nodeInfo.getViewIdResourceName()+ " " + child.getViewIdResourceName()+ " " + child2.getViewIdResourceName()+ " " + child3.getViewIdResourceName()+ " " + child4.getViewIdResourceName(), LENGTH_SHORT).show();
                                                    }

                                                    int count5 = child4.getChildCount();
                                                    for (int i5 = 0; i5 < count5; i5++) {
                                                        AccessibilityNodeInfo child5 = child4.getChild(i5);
                                                        if (child5 != null) {

                                                            Object Deskr6 = child5.getContentDescription();
                                                            if (Deskr6 != null){
                                                                Toast.makeText(getApplicationContext(), "Ch5 dsc: " + Deskr6+ nodeInfo.getViewIdResourceName()+ " " + child.getViewIdResourceName()+ " " + child2.getViewIdResourceName()+ " " + child3.getViewIdResourceName()+ " " + child4.getViewIdResourceName()+ " " + child5.getViewIdResourceName(), LENGTH_SHORT).show();
                                                            }
                                                            Object TEXT6 = child5.getText();
                                                            if (TEXT6 != null){
                                                                Toast.makeText(getApplicationContext(), "Ch5 txt: " + TEXT6+ nodeInfo.getViewIdResourceName()+ " " + child.getViewIdResourceName()+ " " + child2.getViewIdResourceName()+ " " + child3.getViewIdResourceName()+ " " + child4.getViewIdResourceName()+ " " + child5.getViewIdResourceName(), LENGTH_SHORT).show();
                                                            }
                                                            int count6 = child5.getChildCount();
                                                            for (int i6 = 0; i6 < count6; i6++) {
                                                                AccessibilityNodeInfo child6 = child5.getChild(i6);
                                                                if (child6 != null) {

                                                                    Object Deskr7 = child6.getContentDescription();
                                                                    if (Deskr7 != null){
                                                                        Toast.makeText(getApplicationContext(), "Ch6 dsc: " + Deskr7+ nodeInfo.getViewIdResourceName()+ " " + child.getViewIdResourceName()+ " " + child2.getViewIdResourceName()+ " " + child3.getViewIdResourceName()+ " " + child4.getViewIdResourceName()+ " " + child5.getViewIdResourceName()+ " " + child6.getViewIdResourceName(), LENGTH_SHORT).show();
                                                                    }
                                                                    Object TEXT7 = child6.getText();
                                                                    if (TEXT7 != null){
                                                                        Toast.makeText(getApplicationContext(), "Ch6 txt: " + TEXT7+ nodeInfo.getViewIdResourceName()+ " " + child.getViewIdResourceName()+ " " + child2.getViewIdResourceName()+ " " + child3.getViewIdResourceName()+ " " + child4.getViewIdResourceName()+ " " + child5.getViewIdResourceName()+ " " + child6.getViewIdResourceName(), LENGTH_SHORT).show();
                                                                    }
                                                                    int count7 = child6.getChildCount();
                                                                    for (int i7 = 0; i7 < count7; i7++) {
                                                                        AccessibilityNodeInfo child7 = child6.getChild(i7);
                                                                        if (child7 != null) {

                                                                            Object Deskr8 = child7.getContentDescription();

                                                                            if (Deskr8 != null){
                                                                                Toast.makeText(getApplicationContext(), "Ch7 dsc: " + Deskr8+ nodeInfo.getViewIdResourceName()+ " " + child.getViewIdResourceName()+ " " + child2.getViewIdResourceName()+ " " + child3.getViewIdResourceName()+ " " + child4.getViewIdResourceName()+ " " + child5.getViewIdResourceName()+ " " + child6.getViewIdResourceName()+ " " + child7.getViewIdResourceName(), LENGTH_SHORT).show();
                                                                            }
                                                                            Object TEXT8 = child7.getText();
                                                                            if (TEXT8 != null){
                                                                                Toast.makeText(getApplicationContext(), "Ch7 txt: " + TEXT8+ nodeInfo.getViewIdResourceName()+ " " + child.getViewIdResourceName()+ " " + child2.getViewIdResourceName()+ " " + child3.getViewIdResourceName()+ " " + child4.getViewIdResourceName()+ " " + child5.getViewIdResourceName()+ " " + child6.getViewIdResourceName()+ " " + child7.getViewIdResourceName(), LENGTH_SHORT).show();
                                                                            }
                                                                            int count8 = child7.getChildCount();
                                                                            for (int i8 = 0; i8 < count8; i8++) {
                                                                                AccessibilityNodeInfo child8 = child7.getChild(i8);
                                                                                if (child8 != null) {

                                                                                    Object Deskr9 = child8.getContentDescription();
                                                                                    if (Deskr9 != null){
                                                                                        Toast.makeText(getApplicationContext(), "Ch8 dsc: " + Deskr9+ nodeInfo.getViewIdResourceName()+ " " + child.getViewIdResourceName()+ " " + child2.getViewIdResourceName()+ " " + child3.getViewIdResourceName()+ " " + child4.getViewIdResourceName()+ " " + child5.getViewIdResourceName()+ " " + child6.getViewIdResourceName()+ " " + child7.getViewIdResourceName()+ " " + child8.getViewIdResourceName(), LENGTH_SHORT).show();
                                                                                    }
                                                                                    Object TEXT9 = child8.getText();
                                                                                    if (TEXT9 != null){
                                                                                        Toast.makeText(getApplicationContext(), "Ch8 txt: " + TEXT9+ nodeInfo.getViewIdResourceName()+ " " + child.getViewIdResourceName()+ " " + child2.getViewIdResourceName()+ " " + child3.getViewIdResourceName()+ " " + child4.getViewIdResourceName()+ " " + child5.getViewIdResourceName()+ " " + child6.getViewIdResourceName()+ " " + child7.getViewIdResourceName()+ " " + child8.getViewIdResourceName(), LENGTH_SHORT).show();
                                                                                    }
                                                                                    child8.recycle();
                                                                                }
                                                                            }
                                                                            child7.recycle();
                                                                        }
                                                                    }
                                                                    child6.recycle();
                                                                }
                                                            }
                                                            child5.recycle();
                                                        }
                                                    }
                                                    child4.recycle();
                                                }
                                            }
                                            child3.recycle();
                                        }
                                    }
                                    child2.recycle();
                                }
                            }
                            child.recycle();
                        }
                    }
                    nodeInfo.recycle();
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onInterrupt() {

    }
}



