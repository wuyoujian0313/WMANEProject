package com.weimeitc.wmane.Pay;

/**
 * Created by wuyoujian on 2018/1/27.
 */

public class WXPayManager {

    private PayFinishCallback payCallback;

    // 单例函数
    public static final WXPayManager getSingleton() {
        return WXPayManager.LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final WXPayManager INSTANCE = new WXPayManager();
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
