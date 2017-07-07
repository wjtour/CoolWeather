package com.example.user.coolweather.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.user.coolweather.R;
import com.example.user.coolweather.model.WeatherInfo;

import java.util.List;

/**
 * Created by USER on 2017-05-30 0030.
 */
public class WeatherInfoAdapter extends ArrayAdapter<WeatherInfo> {


    int resourceId;

    public WeatherInfoAdapter(Context context, int resource, List<WeatherInfo> objects) {
        super(context, resource, objects);
        this.resourceId=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WeatherInfo weatherInfo=getItem(position);
        View v;
        ViewHolder viewHolder;
        if((v=convertView)==null)
        {
            v= LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder=new ViewHolder();
            viewHolder.dateView=(TextView) v.findViewById(R.id.dateView);
            viewHolder.typeView=(TextView)v.findViewById(R.id.typeView);
            viewHolder.tempView=(TextView)v.findViewById(R.id.tempView);
            v.setTag(viewHolder);
        }else {

            viewHolder=(ViewHolder) v.getTag();
        }
        viewHolder.dateView.setText(weatherInfo.getDate());
        viewHolder.tempView.setText(weatherInfo.getLow()+"~"+weatherInfo.getHigh());
        viewHolder.typeView.setText(weatherInfo.getType());
        return  v;
    }
    class ViewHolder{
        TextView dateView;
        TextView typeView;
        TextView tempView;
    }
}
