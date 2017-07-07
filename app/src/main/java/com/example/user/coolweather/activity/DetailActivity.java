package com.example.user.coolweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.coolweather.R;
import com.example.user.coolweather.util.Utility;

public class DetailActivity extends Activity {

    private int index;
    private TextView cityNameText;
    private TextView currentDateText;
    private TextView weatherDespText;
    private TextView publishText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView fengli;
    private TextView fengxiang;
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_detail);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        fengli=(TextView)findViewById(R.id.fengli);
        fengxiang=(TextView)findViewById(R.id.fengxiang);
        backButton=(Button)findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        index=getIntent().getIntExtra("weatherIndex",1);
        showAllWeather();
    }

    private void showAllWeather() {



        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        cityNameText.setText( prefs.getString("city_name", ""));
        publishText.setText( prefs.getString("refresh_time", "") + "刷新");
        temp1Text.setText(Utility.temp(prefs.getString("low"+index,""),prefs.getBoolean("showSheshi",true)));
        temp2Text.setText(Utility.temp(prefs.getString("high"+index,""),prefs.getBoolean("showSheshi",true)));
        weatherDespText.setText(prefs.getString("type"+index,""));
        currentDateText.setText(prefs.getString("date"+index,""));
        fengxiang.setText(prefs.getString("fengxiang"+index,""));
        fengli.setText(prefs.getString("fengli"+index,""));

        cityNameText.setVisibility(View.VISIBLE);

    }
}
