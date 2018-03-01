package com.weimeitc.wmane.ANEFunction;

import android.app.Activity;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.weimeitc.wmane.ANETypeConversion;
import com.weimeitc.wmane.Pay.wxpay.WXPayManager;

/**
 * Created by wuyoujian on 2018/1/27.
 */

public class WXSDKRegiterFunction implements FREFunction {

    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {
        String appId = ANETypeConversion.FREObject2String(freObjects[0]);
        String appSecret = ANETypeConversion.FREObject2String(freObjects[1]);
        String partner = ANETypeConversion.FREObject2String(freObjects[2]);

        Activity activity = freContext.getActivity();
        WXPayManager.getSingleton().registetSDK(appId,appSecret,partner,activity);
        return null;
    }
}
