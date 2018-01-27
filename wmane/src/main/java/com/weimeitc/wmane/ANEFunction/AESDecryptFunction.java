package com.weimeitc.wmane.ANEFunction;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.weimeitc.wmane.ANETypeConversion;
import com.weimeitc.wmane.Utils.AESEncrypt;

/**
 * Created by wuyoujian on 2018/1/27.
 */

public class AESDecryptFunction implements FREFunction {
    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {
        String encrypt = ANETypeConversion.FREObject2String(freObjects[0]);
        String key = "维美天成ANE产品";

        try {
            String content = AESEncrypt.decrypt(encrypt,key);
            freContext.dispatchStatusEventAsync("decrypt",content);
        } catch (Exception e) {

        }

        return null;
    }
}
