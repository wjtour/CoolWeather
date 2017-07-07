package com.example.user.coolweather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;

import com.example.user.coolweather.R;
import com.example.user.coolweather.activity.WeatherActivity;
import com.example.user.coolweather.receiver.AutoUpdateReceiver;
import com.example.user.coolweather.util.HttpCallbackListener;
import com.example.user.coolweather.util.HttpUtil;
import com.example.user.coolweather.util.Utility;

public class AutoUpdateService extends Service {
    private String cityName;
    private String time;
    private String high;
    private String type;
    private String  low;
    int no=1;
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    void showNotifition(){

        NotificationManager manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
     //   builder.setContentInfo(time+"更新");//补充内容
        builder.setContentText(low+"~"+high);//主内容
        builder.setContentTitle(cityName+" "+type); //通知标题
        builder.setSmallIcon(R.drawable.ic_launcher);//通知logo
        builder.setTicker(cityName+"  "+low+"~"+high);//
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(Notification.DEFAULT_LIGHTS);//设置指示灯  
        builder.setDefaults(Notification.DEFAULT_SOUND);//设置提示声音  
        builder.setDefaults(Notification.DEFAULT_VIBRATE);//设置震动  
        Intent intent = new Intent(this, WeatherActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        manager.cancel(no-1);
        manager.notify(no++, notification);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                updateWeather();
                ;

            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8*60*60*  1000; // 这是8小时的毫秒数
       // int anHour = 10 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
       manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
       // manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 更新天气信息。
     */
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        String weatherCode = prefs.getString("weather_code", "");
        String address = "http://wthrcdn.etouch.cn/weather_mini?citykey=" +
                weatherCode;
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleAllWeatherResponse(AutoUpdateService.this,
                        response);
                showAllWeather();
                SharedPreferences prefs = PreferenceManager.
                        getDefaultSharedPreferences(AutoUpdateService.this);
                boolean b=prefs.getBoolean("showNotifition",true);
                if(b) {
                    showNotifition();
                }
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

        });
    }

    private void showAllWeather() {

        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        cityName=prefs.getString("city_name", "");
        time=prefs.getString("refresh_time", "");
        low= Utility.temp(prefs.getString("low1",""),prefs.getBoolean("showSheshi",true));

        high=Utility.temp(prefs.getString("high1",""),prefs.getBoolean("showSheshi",true));;
        type=prefs.getString("type1","");

    }

}