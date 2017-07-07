package com.example.user.coolweather.util;

/**
 * Created by USER on 2017-04-30 0030.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}