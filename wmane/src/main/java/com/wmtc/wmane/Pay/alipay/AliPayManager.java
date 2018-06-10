package com.wmtc.wmane.Pay.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.alipay.sdk.app.PayTask;
import java.util.Map;

/**
 * Created by wuyoujian on 2018/1/27.
 */

public class AliPayManager {
    private PayFinishCallback payCallback;
    private String aliPayAppId;
    private String aliPayAppSecret;
    private static final int SDK_PAY_FLAG = 1;
    private Activity context;

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

    public void registetSDK(String appId,String appSecret, Activity context) {
        aliPayAppId = appId;
        aliPayAppSecret = appSecret;
        this.context = context;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        //Toast.makeText(PayDemoActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        if (payCallback != null) {
                            payCallback.finishPayCallback(0,resultInfo);
                        }
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        //Toast.makeText(PayDemoActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        if (payCallback != null) {
                            payCallback.finishPayCallback(1,resultInfo);
                        }
                    }
                    break;
                }

                default:
                    break;
            }
        }
    };

    public void pay(String orderString, PayFinishCallback finishCallback) {
        this.payCallback = finishCallback;
        final String fOrderString = orderString;
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(context);
                Map<String, String> result = alipay.payV2(fOrderString, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}