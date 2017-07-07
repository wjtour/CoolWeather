package com.example.user.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.user.coolweather.db.CoolWeatherDB;
import com.example.user.coolweather.model.City;
import com.example.user.coolweather.model.County;
import com.example.user.coolweather.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by USER on 2017-04-30 0030.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB
                                                                       coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
// 将解析出来的数据存储到Province表
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
                                               String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();

                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
// 将解析出来的数据存储到City表
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
                                                 String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
// 将解析出来的数据存储到County表
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }


    /**
     * 解析服务器返回的JSON数据，并将解析出的数据存储到本地。
     */
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");

            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
                    weatherDesp, publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
     */
    public static void saveWeatherInfo(Context context, String cityName,
                                       String weatherCode, String temp1, String temp2, String weatherDesp, String
                                               publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",
                Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));

        editor.commit();
    }

    /*
         {
                "fengxiang": "南风",
                "fengli": "微风级",
                "high": "高温 34℃",
                "type": "多云",
                "low": "低温 22℃",
                "date": "19日星期五"
            }

            "yesterday": {
            "fl": "3-4级",
            "fx": "东北风",
            "high": "高温 27℃",
            "type": "多云",
            "low": "低温 14℃",
            "date": "14日星期日"
        },

     */


    public static void handleAllWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray forecast=data.getJSONArray("forecast");
            JSONObject yesterday=data.getJSONObject("yesterday");

            String city = data.getString("city");

            saveCityName(context,city);

            String high = yesterday.getString("high");
            String low = yesterday.getString("low");
            String type = yesterday.getString("type");
            String fengli = yesterday.getString("fl");
            String fengxiang=yesterday.getString("fx");
            String date=yesterday.getString("date");
            saveAllWeatherInfo(context, 0, fengxiang, fengli,high,type,low,date);


            for(int i=0;i<forecast.length();i++){

                high = forecast.getJSONObject(i).getString("high");
                low =  forecast.getJSONObject(i).getString("low");
               type = forecast.getJSONObject(i).getString("type");
                fengli =  forecast.getJSONObject(i).getString("fengli");
                fengxiang= forecast.getJSONObject(i).getString("fengxiang");
                date= forecast.getJSONObject(i).getString("date");
                saveAllWeatherInfo(context, i+1, fengxiang, fengli,high,type,low,date);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    


    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
     *  {
     "fengxiang": "南风",
     "fengli": "微风级",
     "high": "高温 34℃",
     "type": "多云",
     "low": "低温 22℃",
     "date": "19日星期五"
     }

     */
   static public String parse(String s){
        String regEx1="[^0-9]";
        Pattern p1 = Pattern.compile(regEx1);
        Matcher m1 = p1.matcher(s);
        return m1.replaceAll("").trim();
    }
    static public   String temp(String temp,boolean isSheshi){
        if(isSheshi){
            return  temp+"℃";
        }else {
            return (int)(Integer.parseInt(temp)*9.0/5.0+32)+"℉";
        }
    }
    public static void saveAllWeatherInfo(Context context,int index, String fengxiang,
                                       String fengli, String high, String type, String
                                               low,String date) {

        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();

        editor.putString("fengxiang"+index, fengxiang);

        editor.putString("fengli"+index, fengli);

        editor.putString("high"+index, parse(high));
        editor.putString("type"+index, type);
        editor.putString("low"+index, parse(low));
        editor.putString("date"+index,date);

        editor.commit();
    }

    public static void saveWeatherCode(Context context, String weatherCode) {

        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();

        editor.putString("weather_code", weatherCode);

        editor.commit();
    }

    public static void saveCityName(Context context, String cityName) {

        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日 HH:mm",Locale.CHINA);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",
                Locale.CHINA);
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("refresh_time", sdf.format(new Date()));
        editor.commit();
    }


}
