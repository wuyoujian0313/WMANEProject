package com.wmtc.wmane.ANEFunction;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.alipay.sdk.app.EnvUtils;

/**
 * Created by wuyoujian on 2018/6/12.
 */

public class AlipaySandboxFunction implements FREFunction {

    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        return null;
    }
}
