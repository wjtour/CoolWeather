package com.example.user.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.coolweather.R;
import com.example.user.coolweather.model.WeatherInfo;
import com.example.user.coolweather.service.AutoUpdateService;
import com.example.user.coolweather.util.HttpCallbackListener;
import com.example.user.coolweather.util.HttpUtil;
import com.example.user.coolweather.util.Utility;
import com.example.user.coolweather.util.WeatherInfoAdapter;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends Activity implements View.OnClickListener {
    private List<WeatherInfo> weatherInfos=new ArrayList<>();
    PopupMenu popupMenu;
    Menu menu;
    private LinearLayout weatherInfoLayout;
    /**
     * 用于显示城市名

     */
    private TextView cityNameText;
    /**
     * 用于显示发布时间
     */
    private TextView publishText;
    /**
     * 用于显示天气描述信息
     */
    private TextView weatherDespText;
    /**
     * 用于显示气温1
     */
    private TextView temp1Text;
    /**
     * 用于显示气温2
     */
    private TextView temp2Text;
    /**
     * 用于显示当前日期
     */
    private TextView currentDateText;
    /**
     * 切换城市按钮
     */
    private Button switchCity;
    /**
     * 更新天气按钮
     */
    private Button refreshWeather;

    private  TextView fengxiang;
    private  TextView fengli;
    private  Button menuButton;
    private ListView weatherListView;
    private  Button share;
    private WeatherInfoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
// 初始化各控件
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        switchCity = (Button) findViewById(R.id.switch_city);
        switchCity.setVisibility(View.GONE);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        fengli=(TextView)findViewById(R.id.fengli);
        fengxiang=(TextView)findViewById(R.id.fengxiang);
        menuButton=(Button)findViewById(R.id.popupmenu_btn);
        share=(Button)findViewById(R.id.share);
        popupMenu = new PopupMenu(this, findViewById(R.id.popupmenu_btn));
        menu = popupMenu.getMenu();
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.popupmenu, menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settings:
                        Intent i=new Intent(WeatherActivity.this,SettingsActivity.class);
                        startActivity(i);
                        break;
                    case R.id.map:
                        SharedPreferences prefs = PreferenceManager. getDefaultSharedPreferences(WeatherActivity.this);
                        Uri mUri=Uri.parse("geo:q="+ prefs.getString("city_name", ""));
                        Intent mIntent=new Intent(Intent.ACTION_VIEW,mUri);
                        startActivity(mIntent);

                        break;
                    case R.id.share:
                        Intent textIntent = new Intent(Intent.ACTION_SEND);
                        textIntent.setType("text/plain");
                        prefs = PreferenceManager. getDefaultSharedPreferences(WeatherActivity.this);
                       String info=prefs.getString("city_name","")+":"+prefs.getString("type1","")+"\n"+prefs.getString("low1","")+"~"+prefs.getString("high1","")+"\n"+prefs.getString("fengxiang1","")+" "+prefs.getString("fengli1","");
                        textIntent.putExtra(Intent.EXTRA_TEXT,info);
                        startActivity(Intent.createChooser(textIntent, "分享"));

                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        weatherListView=(ListView)findViewById(R.id.weather_list);
        adapter=new WeatherInfoAdapter(WeatherActivity.this,R.layout.weather_item,weatherInfos);

        weatherListView.setAdapter(adapter);
        weatherListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(WeatherActivity.this,DetailActivity.class);
                i.putExtra("weatherIndex",position);
                startActivity(i);
            }
        });
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
// 有县级代号时就去查询天气
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
// 没有县级代号时就直接显示本地天气

            showAllWeather();
        }
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        showAllWeather();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.
                        getDefaultSharedPreferences(this);
                String weatherCode = prefs.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
                break;

            default:
                break;
        }
    }
    /**
     * 查询县级代号所对应的天气代号。
     */
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" +
                countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }
    /**
     * 查询天气代号所对应的天气。
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://wthrcdn.etouch.cn/weather_mini?citykey=" +
                weatherCode;
        queryFromServer(address, "weatherCode");
    }
    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
     */
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
// 从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            Utility.saveWeatherCode(WeatherActivity.this,weatherCode);
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {

// 处理服务器返回的天气信息
                    Utility.handleAllWeatherResponse(WeatherActivity.this,
                            response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAllWeather();
                            Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
                            startService(intent);
                        }
                    });
                }
            }
            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }
    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
     */
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        cityNameText.setText( prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }


    private void showAllWeather() {

        loadAllWeather();
        adapter.notifyDataSetChanged();
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        cityNameText.setText( prefs.getString("city_name", ""));
        publishText.setText( prefs.getString("refresh_time", "") + "刷新");
        temp1Text.setText(Utility.temp(prefs.getString("low1",""),prefs.getBoolean("showSheshi",true)));
        temp2Text.setText(Utility.temp(prefs.getString("high1",""),prefs.getBoolean("showSheshi",true)));
        weatherDespText.setText(prefs.getString("type1",""));
        currentDateText.setText(prefs.getString("date1",""));
        fengxiang.setText(prefs.getString("fengxiang1",""));
        fengli.setText(prefs.getString("fengli1",""));

        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);

    }
    private void loadAllWeather(){
        weatherInfos.clear();
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        WeatherInfo weatherInfo;
        for(int i=0;!prefs.getString("date"+i,"").equals("");i++){
            weatherInfo=new WeatherInfo();
            weatherInfo.setDate(prefs.getString("date"+i,""));
            weatherInfo.setFengli(prefs.getString("fengli"+i,""));
            weatherInfo.setFengxiang(prefs.getString("fengxiang"+i,""));
            weatherInfo.setLow(Utility.temp(prefs.getString("low"+i,""),prefs.getBoolean("showSheshi",true)));
            weatherInfo.setHigh(Utility.temp(prefs.getString("high"+i,""),prefs.getBoolean("showSheshi",true)));
            weatherInfo.setType(prefs.getString("type"+i,""));
            weatherInfos.add(weatherInfo);
        }

    }

    public void popupmenu(View v) {
        popupMenu.show();
    }
}
