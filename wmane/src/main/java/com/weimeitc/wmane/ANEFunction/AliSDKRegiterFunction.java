package com.weimeitc.wmane.ANEFunction;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.weimeitc.wmane.ANETypeConversion;
import com.weimeitc.wmane.Pay.AliPayManager;

/**
 * Created by wuyoujian on 2018/1/27.
 */

public class AliSDKRegiterFunction implements FREFunction {

    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {
        String appId = ANETypeConversion.FREObject2String(freObjects[0]);
        String appSecret = ANETypeConversion.FREObject2String(freObjects[1]);
        AliPayManager.getSingleton().registetSDK(appId,appSecret);
        return null;
    }
}
