package com.weimeitc.wmane.ANEFunction;

import android.app.Activity;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.weimeitc.wmane.ANETypeConversion;
import com.weimeitc.wmane.Pay.alipay.AliPayManager;

/**
 * Created by wuyoujian on 2018/1/27.
 */

public class AliSDKRegiterFunction implements FREFunction {

    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {
        String appId = ANETypeConversion.FREObject2String(freObjects[0]);
        String appSecret = ANETypeConversion.FREObject2String(freObjects[1]);

        Activity activity = freContext.getActivity();
        AliPayManager.getSingleton().registetSDK(appId,appSecret,activity);
        return null;
    }
}
