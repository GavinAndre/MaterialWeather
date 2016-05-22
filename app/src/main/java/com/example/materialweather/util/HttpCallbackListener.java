package com.example.materialweather.util;

/**
 * Created by GavinAndre on 2015/11/16 0016.
 */
public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);
}
