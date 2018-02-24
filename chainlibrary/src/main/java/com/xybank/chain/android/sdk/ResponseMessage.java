package com.xybank.chain.android.sdk;

import java.io.Serializable;

/**
 * Created by Livingston on 2018/2/22.
 */

public class ResponseMessage implements Serializable {

    private Body body;

    // 错误码 0为通过，其他值为错误
    private int errorCode;

    // 错误消息描述
    private String errMsg;

    // 业务消息 (保留字段)
    private String message;

    // 签名
    private String signature;

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public class Body implements Serializable {

        // 记录流水号
        private String recordNo;

        public String getRecordNo() {
            return recordNo;
        }

        public void setRecordNo(String recordNo) {
            this.recordNo = recordNo;
        }
    }
}
