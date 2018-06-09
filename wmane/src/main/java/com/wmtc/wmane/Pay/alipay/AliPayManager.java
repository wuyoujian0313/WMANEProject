package com.wmtc.wmane.Pay.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.wmtc.wmane.Pay.alipay.utils.OrderInfoUtil2_0;
import com.wmtc.wmane.Pay.alipay.utils.PayResult;

import java.text.SimpleDateFormat;
import java.util.HashMap;
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
    private String payJson;

    private static final int SDK_PAY_FLAG = 1;

    private Activity context;
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

    public void registetSDK(String appId,String appSecret, Activity context) {
        aliPayAppId = appId;
        aliPayAppSecret = appSecret;
        this.context = context;
    }


    public class PayJsonInfo {
        private String goodsDesc;
        private String goodsName;
        private String orderNo;
        private String price;
        private String scheme;

        public String getGoodsDesc() {
            return goodsDesc;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public String getPrice() {
            return price;
        }

        public String getScheme() {
            return scheme;
        }

        public void setGoodsDesc(String goodsDesc) {
            this.goodsDesc = goodsDesc;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }
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

    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive");
        return builder;
    }

    public void pay(String payJson, PayFinishCallback finishCallback) {
        this.payJson = payJson;
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

        Gson gson = new Gson();
        PayJsonInfo payInfo = gson.fromJson(payJson,PayJsonInfo.class);

        String biz_content = "{";
        biz_content += "\"timeout_express\":\"30m\",";
        biz_content += "\"total_amount\":\"" + payInfo.getPrice() + "\"";
        biz_content += "\"body\":\"" + payInfo.getGoodsDesc() + "\"";
        biz_content += "\"subject\":\"" + payInfo.getGoodsName() + "\"";
        biz_content += "\"out_trade_no\":\"" + payInfo.getOrderNo() + "\"";
        biz_content += "}";

        Map<String, String> params = new HashMap<String, String>();
        params.put("app_id", aliPayAppId);
        params.put("biz_content", biz_content);
        params.put("charset", "utf-8");
        params.put("method", "alipay.trade.app.pay");
        params.put("sign_type", "RSA2");
        params.put("version", "1.0");

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sDateFormat.format(new java.util.Date());
        params.put("timestamp", timestamp);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        boolean rsa2 = (rsaKey.length() > 0);
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
