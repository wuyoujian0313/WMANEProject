package com.wmtc.wmane;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import java.util.HashMap;
import java.util.Map;

import com.wmtc.wmane.ANEFunction.AlipaySandboxFunction;
import com.wmtc.wmane.ANEFunction.AppBroadcastFunction;
import com.wmtc.wmane.ANEFunction.IsinstallFunction;
import com.wmtc.wmane.ANEFunction.QQAuthFunction;
import com.wmtc.wmane.ANEFunction.RegisterSDKFunction;
import com.wmtc.wmane.ANEFunction.SendImageFunction;
import com.wmtc.wmane.ANEFunction.SendImageURLFunction;
import com.wmtc.wmane.ANEFunction.SendLinkFunction;
import com.wmtc.wmane.ANEFunction.SendTextFunction;
import com.wmtc.wmane.ANEFunction.WXAuthFunction;

import com.wmtc.wmane.ANEFunction.AESDecryptFunction;
import com.wmtc.wmane.ANEFunction.AESEncryptFunction;
import com.wmtc.wmane.ANEFunction.AliPayFunction;
import com.wmtc.wmane.ANEFunction.AliSDKRegiterFunction;
import com.wmtc.wmane.ANEFunction.WXPayFunction;
import com.wmtc.wmane.ANEFunction.WXSDKRegiterFunction;
//

/**
 * Created by wuyoujian on 17/3/15.
 */

public class WMFREContext extends FREContext {
    private static final String LOGIN_FUNCTION_QQ = "login_function_qq";
    private static final String LOGIN_FUNCTION_WX = "login_function_wx";
    private static final String SHARING_FUNCTION_TEXT = "sharing_function_text";
    private static final String SHARING_FUNCTION_LINK = "sharing_function_link";
    private static final String SHARING_FUNCTION_IMAGE = "sharing_function_image";
    private static final String SHARING_FUNCTION_IMAGE_URL = "sharing_function_image_url";
    private static final String SHARING_FUNCTION_IS_INSTALLED = "sharing_function_is_installed";
    private static final String SHARING_FUNCTION_REGISTER_SHARESDKS = "registerShareSDKs";
    private static final String PAY_FUNCTION_REGISTER_WXPAYSDK  = "registerWXPaySDK";
    private static final String PAY_FUNCTION_WXPAY  = "wxpay";
    private static final String PAY_FUNCTION_REGISTER_ALIPAYSDK  = "registerAlipaySDK";
    private static final String PAY_FUNCTION_ALIPAY  = "alipay";
    private static final String ENCRYPT_FUNCTION  = "encrypt_wm";
    private static final String DECRYPT_FUNCTION  = "decrypt_wm";
    private static final String ALIPAYSANDBOX_FUNCTION  = "alipaySandbox";
    private static final String APPBROADCAST_FUNCTION = "JN_AppBroadcast";



    @Override
    public void dispose() {

    }

    @Override
    public Map<String, FREFunction> getFunctions() {
        Map<String, FREFunction> map = new HashMap<String, FREFunction>();
        //映射
        map.put(LOGIN_FUNCTION_QQ, new QQAuthFunction());
        map.put(LOGIN_FUNCTION_WX, new WXAuthFunction());
        map.put(SHARING_FUNCTION_REGISTER_SHARESDKS, new RegisterSDKFunction());
        map.put(SHARING_FUNCTION_TEXT, new SendTextFunction());
        map.put(SHARING_FUNCTION_LINK, new SendLinkFunction());
        map.put(SHARING_FUNCTION_IMAGE, new SendImageFunction());
        map.put(SHARING_FUNCTION_IMAGE_URL, new SendImageURLFunction());
        map.put(SHARING_FUNCTION_IS_INSTALLED, new IsinstallFunction());

        map.put(PAY_FUNCTION_REGISTER_WXPAYSDK, new WXSDKRegiterFunction());
        map.put(PAY_FUNCTION_WXPAY, new WXPayFunction());
        map.put(PAY_FUNCTION_REGISTER_ALIPAYSDK,new AliSDKRegiterFunction());
        map.put(PAY_FUNCTION_ALIPAY,new AliPayFunction());
        map.put(ENCRYPT_FUNCTION,new AESEncryptFunction());
        map.put(DECRYPT_FUNCTION,new AESDecryptFunction());
        map.put(ALIPAYSANDBOX_FUNCTION,new AlipaySandboxFunction());
        map.put(APPBROADCAST_FUNCTION,new AppBroadcastFunction());
        return map;
    }


}
