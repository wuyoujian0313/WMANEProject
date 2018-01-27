package com.weimeitc.wmane.Pay;

import com.weimeitc.wmane.SharedSDK.SharedManager;

/**
 * Created by wuyoujian on 2018/1/27.
 */

public class AliPayManager {

    private PayFinishCallback payCallback;

    // 单例函数
    public static final AliPayManager getSingleton() {
        return AliPayManager.LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final AliPayManager INSTANCE = new AliPayManager();
    }

    // 分享回调函数
    public interface PayFinishCallback {
        void finishPayCallback(int statusCode, Object resp);
    }

    public void registetSDK(String appId,String appSecret) {

    }

    public void pay(String payJson, PayFinishCallback finishCallback) {

    }
}
