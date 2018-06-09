package com.wmtc.wmane.Pay.wxpay;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

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

public class WXPayManager {

    private static final String apiURL = "";
    private PayFinishCallback payCallback;
    private String wxPayAppId;
    private String wxPayAppSecret;
    private String wxPartner;
    private IWXAPI wxAPI;
    private Activity context;

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

    public void registetSDK(String appId,String appSecret, String partner,Activity context) {
        wxPayAppId = appId;
        wxPayAppSecret = appSecret;
        wxPartner = partner;
        this.context = context;

        wxAPI = WXAPIFactory.createWXAPI(context, wxPayAppId);
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

    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive");
        return builder;
    }

    public void pay(final String payJson, PayFinishCallback finishCallback) {

        Gson gson = new Gson();
        final PayJsonInfo payInfo = gson.fromJson(payJson,PayJsonInfo.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //创建一个FormBody.Builder
                    FormBody.Builder builder = new FormBody.Builder();
                    builder.add("appid", wxPayAppId);
                    builder.add("appsecret",wxPayAppSecret);
                    builder.add("partner", wxPartner);
                    builder.add("money",payInfo.getPrice());
                    builder.add("device_info","WEB");
                    builder.add("body",payInfo.getGoodsName());
                    builder.add("spbill_create_ip","192.168.1.1");
                    builder.add("fee_type","CNY");

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
                        final WeixinOrderInfo resultObj = gson.fromJson(resultJson,WeixinOrderInfo.class);
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callWeixinPay(resultObj);
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void callWeixinPay(WeixinOrderInfo order){
        try{
            PayReq req = new PayReq();
            req.appId = order.getAppid();
            req.partnerId = order.getPartnerid();;
            req.prepayId = order.getPrepayid();
            req.packageValue = order.getReturnPackage();
            req.nonceStr = order.getNoncestr();
            req.timeStamp = order.getTimestamp();
            req.sign = order.getSign();

            wxAPI.sendReq(req);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public class WeixinOrderInfo extends Object {

        private String appid;
        private String noncestr;
        private String out_trade_no;
        private String returnPackage;
        private String partnerid;
        private String prepayid;
        private String sign;
        private String timestamp;

        public String getAppid() {
            return appid;
        }
        public void setAppid(String appid) {
            this.appid = appid;
        }
        public String getNoncestr() {
            return noncestr;
        }
        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }
        public String getOut_trade_no() {
            return out_trade_no;
        }
        public void setOut_trade_no(String out_trade_no) {
            this.out_trade_no = out_trade_no;
        }
        public String getReturnPackage() {
            return returnPackage;
        }
        public void setReturnPackage(String returnPackage) {
            this.returnPackage = returnPackage;
        }
        public String getPartnerid() {
            return partnerid;
        }
        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }
        public String getPrepayid() {
            return prepayid;
        }
        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }
        public String getSign() {
            return sign;
        }
        public void setSign(String sign) {
            this.sign = sign;
        }
        public String getTimestamp() {
            return timestamp;
        }
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
