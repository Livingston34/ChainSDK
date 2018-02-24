package com.xybank.chain.android.sdk.http;

/**
 * HttpURLConnection网络请求返回监听器
 * Created by Livingston on 2018/2/22.
 */

public interface HttpCallbackModelListener<T> {

    // 网络请求成功
    void onFinish(T response);

    // 网络请求失败
    void onError(Exception e);
}
