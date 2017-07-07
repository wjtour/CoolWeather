package com.example.user.coolweather.activity;

import android.app.Activity;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.user.coolweather.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends Activity {

    Button swichCity;
    Button onNotifi;
    CheckBox checkBox;
    CheckBox checkBox1;
    Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);

        back=(Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        swichCity=(Button)findViewById(R.id.switch_city);
        checkBox=(CheckBox)findViewById(R.id.checkBox);
        checkBox1=(CheckBox)findViewById(R.id.checkBox1);
        onNotifi=(Button)findViewById(R.id.onOff);
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(SettingsActivity.this);
        String cityName=prefs.getString("city_name","");
        swichCity.setText("当前城市："+cityName);
        onNotifi.setText(prefs.getBoolean("showNotifition",true)?"关闭通知":"显示通知");
        onNotifi.setVisibility(View.GONE);
        checkBox.setChecked(prefs.getBoolean("showNotifition",true)?true:false);
        checkBox1.setChecked(prefs.getBoolean("showSheshi",true)?false:true);
        checkBox.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = PreferenceManager.
                        getDefaultSharedPreferences(SettingsActivity.this);
                boolean b=prefs.getBoolean("showNotifition",true);
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(SettingsActivity.this).edit();
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日 HH:mm",Locale.CHINA);

                if (b){
                    editor.putBoolean("showNotifition",false);
                    onNotifi.setText("显示通知");
                //    checkBox.setText("显示通知");
                }else {
                    editor.putBoolean("showNotifition",true);
                    onNotifi.setText("关闭通知");
                    //checkBox.setText("关闭通知");
                }

                editor.commit();

            }
        });

        checkBox1.setOnClickListener( new View.OnClickListener(){


            @Override
            public void onClick(View v) {

                SharedPreferences prefs = PreferenceManager.
                        getDefaultSharedPreferences(SettingsActivity.this);
                boolean b=prefs.getBoolean("showSheshi",true);
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(SettingsActivity.this).edit();
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日 HH:mm",Locale.CHINA);

                if (b){
                    editor.putBoolean("showSheshi",false);
                  //  onNotifi.setText("显示通知");
                    //    checkBox.setText("显示通知");
                }else {
                    editor.putBoolean("showSheshi",true);
                    //onNotifi.setText("关闭通知");
                    //checkBox.setText("关闭通知");
                }

                editor.commit();

            }
        });

        swichCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();

            }
        });
    }
}
