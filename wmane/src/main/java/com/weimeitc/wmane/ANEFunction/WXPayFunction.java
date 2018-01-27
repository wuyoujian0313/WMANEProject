package com.weimeitc.wmane.ANEFunction;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.weimeitc.wmane.ANETypeConversion;
import com.weimeitc.wmane.Pay.wxpay.WXPayManager;

/**
 * Created by wuyoujian on 2018/1/27.
 */

public class WXPayFunction implements FREFunction {

    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {

        String value = ANETypeConversion.FREObject2String(freObjects[0]);
        WXPayManager.getSingleton().pay(value, new WXPayManager.PayFinishCallback() {
            @Override
            public void finishPayCallback(int statusCode, Object resp) {

            }
        });

        return null;
    }
}
