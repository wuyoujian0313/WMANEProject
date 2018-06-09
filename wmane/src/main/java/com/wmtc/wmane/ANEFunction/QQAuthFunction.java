package com.wmtc.wmane.ANEFunction;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.wmtc.wmane.SharedSDK.SharedManager;

/**
 * Created by wuyoujian on 17/4/6.
 */

public class QQAuthFunction implements FREFunction {
    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {
        SharedManager.getSingleton().loginByQQ();
        return null;
    }
}
