package com.weimeitc.wmane.ANEFunction;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

import com.weimeitc.wmane.SharedSDK.SharedManager;


/**
 * Created by wuyoujian on 17/4/5.
 */

public class WXAuthFunction implements FREFunction {
    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {
        SharedManager.getSingleton().loginByWX();
        return null;
    }
}