package com.wmtc.wmane.ANEFunction;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.wmtc.wmane.ANETypeConversion;
import com.wmtc.wmane.Utils.AESEncrypt;

/**
 * Created by wuyoujian on 2018/1/27.
 */

public class AESDecryptFunction implements FREFunction {
    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {
        String encrypt = ANETypeConversion.FREObject2String(freObjects[0]);
        String key = "_weimeitiancheng";

        try {
            String content = AESEncrypt.decrypt(encrypt,key);
            freContext.dispatchStatusEventAsync("decrypt",content);
        } catch (Exception e) {

        }

        return null;
    }


}
