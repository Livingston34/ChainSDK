package com.xybank.chain.android.sdk.http;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.xybank.chain.android.sdk.http.HttpCallbackStringListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Livingston on 2018/2/22.
 */

public class HttpUtils {

    static ExecutorService threadPool = Executors.newCachedThreadPool();
    static Gson gson = new Gson();

    /**
     * Post方法 返回数据会解析成字符串 String
     *
     * @param context   上下文
     * @param urlString 请求的路径
     * @param listener  回调监听
     * @param params    参数列表
     */
    public static void doPost(final Context context,
                              final String urlString,
                              final Map<String, Object> params,
                              final HttpCallbackStringListener listener) {
        final String out = gson.toJson(params);
        // 因为网络请求是耗时操作，所以需要另外开启一个线程来执行该任务。
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpURLConnection httpURLConnection = null;
                ResponseCall responseCall = new ResponseCall(context, listener);
                try {
                    url = new URL(urlString);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestProperty("accept", "*/*");
                    httpURLConnection.setRequestProperty("connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("Charset", "UTF-8");
                    httpURLConnection.setRequestProperty("Content-Length", String.valueOf(out.length()));
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                    httpURLConnection.setRequestProperty("accept", "application/json");

                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setReadTimeout(8000);

                    // 设置运行输入
                    httpURLConnection.setDoInput(true);
                    // 设置运行输出
                    httpURLConnection.setDoOutput(true);

                    PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
                    // 发送请求参数
                    printWriter.write(out);
                    // flush输出流的缓冲
                    printWriter.flush();
                    printWriter.close();

                    if (httpURLConnection.getResponseCode() == 200) {
                        // 获取网络的输入流
                        InputStream is = httpURLConnection.getInputStream();
                        BufferedReader bf = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        //最好在将字节流转换为字符流的时候 进行转码
                        StringBuffer buffer = new StringBuffer();
                        String line;
                        while ((line = bf.readLine()) != null) {
                            buffer.append(line);
                        }
                        bf.close();
                        is.close();
                        responseCall.doSuccess(buffer.toString());
                    } else {
                        responseCall.doFail(new NetworkErrorException("response err code:" +
                                httpURLConnection.getResponseCode()));
                    }
                } catch (MalformedURLException e) {
                    if (listener != null) {
                        // 回调onError()方法
                        responseCall.doFail(e);
                    }
                } catch (IOException e) {
                    if (listener != null) {
                        // 回调onError()方法
                        responseCall.doFail(e);
                    }
                } finally {
                    if (httpURLConnection != null) {
                        // 最后记得关闭连接
                        httpURLConnection.disconnect();
                    }
                }
            }
        });
    }
}
