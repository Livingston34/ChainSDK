package com.xybank.chain.android.sdk.http;

/**
 * Created by Livingston on 2018/2/22.
 */

public interface HttpCallbackStringListener {

    // 网络请求成功
    void onFinish(String response);

    // 网络请求失败
    void onError(Exception e);
}
