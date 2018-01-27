package com.weimeitc.wmane.Pay.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.weimeitc.wmane.Pay.alipay.utils.OrderInfoUtil2_0;
import com.weimeitc.wmane.Pay.alipay.utils.PayResult;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wuyoujian on 2018/1/27.
 */

public class AliPayManager {

    private static final String apiURL = "";

    private PayFinishCallback payCallback;
    private String aliPayAppId;
    private String aliPayAppSecret;

    private static final int SDK_PAY_FLAG = 1;

    public static Activity context;
    private String rsaKey;

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
        aliPayAppId = appId;
        aliPayAppSecret = appSecret;
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
        };
    };

    /**
     * 统一为请求添加头信息
     * @return
     */
    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive");
        return builder;
    }

    public void pay(String payJson, PayFinishCallback finishCallback) {

        this.payCallback = finishCallback;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //创建一个FormBody.Builder
                    FormBody.Builder builder = new FormBody.Builder();
                    builder.add("appId", aliPayAppId);
                    //生成表单实体对象
                    RequestBody formBody = builder.build();
                    //创建一个请求
                    final Request request = addHeaders().url(apiURL).post(formBody).build();
                    //创建一个Call
                    OkHttpClient httpClient = new OkHttpClient().newBuilder()
                            .connectTimeout(30, TimeUnit.SECONDS)//设置超时时间
                            .build();

                    final Call call = httpClient.newCall(request);
                    //执行请求
                    Response response = call.execute();
                    if (response.isSuccessful()) {
                        //
                        String resultJson = response.body().string();
                        Gson gson = new Gson();
                        RSAResult resultObj = gson.fromJson(resultJson,RSAResult.class);
                        rsaKey = resultObj.getRSAKey();
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toPay();
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void toPay() {

        boolean rsa2 = (rsaKey.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(aliPayAppId, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String sign = OrderInfoUtil2_0.getSign(params, rsaKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(context);
                Map<String, String> result = alipay.payV2(orderInfo, true);
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

    public class RSAResult {
        private String RSAKey;

        public String getRSAKey() {
            return RSAKey;
        }

        public void setRSAKey(String RSAKey) {
            this.RSAKey = RSAKey;
        }
    }
}
