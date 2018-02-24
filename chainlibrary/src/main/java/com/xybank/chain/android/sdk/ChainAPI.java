package com.xybank.chain.android.sdk;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.xybank.chain.android.sdk.encrypt.SM3Digest;
import com.xybank.chain.android.sdk.http.HttpCallbackStringListener;
import com.xybank.chain.android.sdk.http.HttpUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Livingston on 2018/2/22.
 */

public class ChainAPI {

    /**
     * 数据加密模式
     */
    public enum EncryptMode {
        SM3("SM3");

        private final String encryptMode;

        EncryptMode(String encryptMode) {
            this.encryptMode = encryptMode;
        }

        public String getEncryptMode() {
            return encryptMode;
        }
    }


    private static final String TAG = "ChainAPI";
    private static final Map<Context, ChainAPI> sInstanceMap = new HashMap<>();

    private final Context mContext;
    private final String mToken;
    private final String mServerUrl;

    ChainAPI(Context context, String token, String serverURL) {
        this.mContext = context;
        this.mToken = token;
        this.mServerUrl = serverURL;
    }

    public static ChainAPI init(Context context, String token, String serverURL) {
        if (null == context) return null;
        synchronized (sInstanceMap) {
            final Context appContext = context.getApplicationContext();

            ChainAPI instance = sInstanceMap.get(appContext);
            if (null == instance) {
                instance = new ChainAPI(appContext, token, serverURL);
                sInstanceMap.put(appContext, instance);
            }

            return instance;
        }
    }

    public static ChainAPI getInstance(Context context) {
        if (null == context) return null;
        synchronized (sInstanceMap) {
            final Context appContext = context.getApplicationContext();
            ChainAPI instance = sInstanceMap.get(appContext);

            if (null == instance) {
                Log.d(TAG, "The static method init(Context context, String serverURL) " +
                        "should be called before calling getInstance()");
            }
            return instance;
        }
    }

    /**
     * 获取授权流水凭证号
     *
     * @param encryptMode 数据加密模式
     * @return 授权流水凭证号
     */
    public String getAuzCertificateNo(EncryptMode encryptMode) {
        if (null == encryptMode || EncryptMode.SM3.getEncryptMode().equals(encryptMode.getEncryptMode()))
            return String.valueOf(new Date().getTime()) + (int) (1000 + Math.random() * 9000);
        return "";
    }

    /**
     * 用户数据加密
     *
     * @param realName    姓名
     * @param mobile      电话号码
     * @param idCard      身份证号码
     * @param bankCard    银行卡号
     * @param encryptMode 加密方式
     * @return 用户数据加密信息
     */
    public String encryptContent(String realName, String mobile,
                                 String idCard, String bankCard,
                                 EncryptMode encryptMode) {
        String content = "{" +
                "\"realName\":" + (null == realName ? "\"\"" : ("\"" + realName + '\"')) +
                ",\"mobile\":" + (null == mobile ? "\"\"" : ("\"" + mobile + '\"')) +
                ",\"idCard\":" + (null == idCard ? "\"\"" : ("\"" + idCard + '\"')) +
                ",\"bankCard\":" + (null == bankCard ? "\"\"" : ("\"" + bankCard + '\"')) +
                '}';
        if (null == encryptMode || EncryptMode.SM3.getEncryptMode().equals(encryptMode.getEncryptMode()))
            return SM3Digest.SM3Hash(content);
        return "";
    }

    /**
     * 用户授权
     *
     * @param encryptContent 用户数据加密信息
     * @param encryptMode    加密方式
     * @param listener
     */
    public void authorize(String encryptContent,
                          String auzCertificateNo,
                          EncryptMode encryptMode,
                          final HttpCallbackStringListener listener) {
        Map<String, Object> authorize = new HashMap<>();

        authorize.put("token", mToken);
        authorize.put("encryptContent", encryptContent);
        authorize.put("encryption", encryptMode.getEncryptMode());
        authorize.put("auzCertificateNo", auzCertificateNo);
        authorize.put("activeTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        authorize.put("signature", "123456");

        Log.e("TAG", new Gson().toJson(authorize));
        HttpUtils.doPost(mContext, mServerUrl + "authorize/", authorize, new HttpCallbackStringListener() {
            @Override
            public void onFinish(String response) {
                if (null == listener) return;
                ResponseMessage responseMessage = new Gson().fromJson(response, ResponseMessage.class);
                if (0 == responseMessage.getErrorCode()) {
                    listener.onFinish(responseMessage.getBody().getRecordNo());
                    return;
                }
                listener.onError(new Exception(responseMessage.getErrMsg()));
            }

            @Override
            public void onError(Exception e) {
                if (null != listener) listener.onError(e);
            }
        });
    }

}
