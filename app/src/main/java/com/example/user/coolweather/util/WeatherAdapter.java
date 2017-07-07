package com.example.user.coolweather.util;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.coolweather.R;
import com.example.user.coolweather.model.WeatherInfo;

import java.util.List;

/**
 * Created by USER on 2017-05-30 0030.
 */
public class WeatherAdapter extends BaseAdapter {
    Context context;
    List<WeatherInfo> weatherInfos;
    public WeatherAdapter(Context context, List<WeatherInfo> weatherInfos) {
        this.context=context;
        this.weatherInfos=weatherInfos;
    }

    @Override

    public int getCount() {
        return weatherInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return weatherInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WeatherInfo weatherInfo=(WeatherInfo) getItem(position);
        View v;
        ViewHolder viewHolder;
        if((v=convertView)==null)
        {
            v= LayoutInflater.from(context).inflate(R.layout.weather_item,null);

            viewHolder=new ViewHolder();
            viewHolder.dateView=(TextView) v.findViewById(R.id.dateView);
            viewHolder.typeView=(TextView)v.findViewById(R.id.typeView);
            viewHolder.tempView=(TextView)v.findViewById(R.id.tempView);
            v.setTag(viewHolder);
        }else {

            viewHolder=(ViewHolder) v.getTag();
        }
        viewHolder.dateView.setText(weatherInfo.getDate());
        viewHolder.tempView.setText(weatherInfo.getLow().split(" ")[1]+"~"+weatherInfo.getHigh().split(" ")[1]);
        viewHolder.typeView.setText(weatherInfo.getType());
        return  v;
    }
    class ViewHolder{
        TextView dateView;
        TextView typeView;
        TextView tempView;
    }
}
