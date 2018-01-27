package com.weimeitc.wmane.ANEFunction;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.weimeitc.wmane.ANETypeConversion;
import com.weimeitc.wmane.Pay.alipay.AliPayManager;
import com.weimeitc.wmane.WMANEShare;

/**
 * Created by wuyoujian on 2018/1/27.
 */

public class AliPayFunction implements FREFunction {
    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {

        final  FREContext fContext = freContext;
        String value = ANETypeConversion.FREObject2String(freObjects[0]);
        AliPayManager.getSingleton().pay(value, new AliPayManager.PayFinishCallback() {
            @Override
            public void finishPayCallback(int statusCode, Object resp) {

                if (statusCode == 0) {
                    fContext.dispatchStatusEventAsync("alipay","0");
                } else {
                    fContext.dispatchStatusEventAsync("alipay","1");
                }
            }
        });

        return null;
    }
}
