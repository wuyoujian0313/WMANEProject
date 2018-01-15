package air.com.weimeitc.wmane;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;


import java.util.HashMap;
import java.util.Map;

import air.com.weimeitc.wmane.ANEFunction.IsinstallFunction;
import air.com.weimeitc.wmane.ANEFunction.QQAuthFunction;
import air.com.weimeitc.wmane.ANEFunction.RegisterSDKFunction;
import air.com.weimeitc.wmane.ANEFunction.SendImageFunction;
import air.com.weimeitc.wmane.ANEFunction.SendImageURLFunction;
import air.com.weimeitc.wmane.ANEFunction.SendLinkFunction;
import air.com.weimeitc.wmane.ANEFunction.SendTextFunction;
import air.com.weimeitc.wmane.ANEFunction.WXAuthFunction;

/**
 * Created by wuyoujian on 17/3/15.
 */

public class WMFREContext extends FREContext {

    private static final String LOGIN_FUNCTION_QQ = "login_function_qq";
    private static final String LOGIN_FUNCTION_WX = "login_function_wx";
    private static final String SHARING_FUNCTION_REGISTER = "sharing_function_register";
    private static final String SHARING_FUNCTION_TEXT = "sharing_function_text";
    private static final String SHARING_FUNCTION_LINK = "sharing_function_link";
    private static final String SHARING_FUNCTION_IMAGE = "sharing_function_image";
    private static final String SHARING_FUNCTION_IMAGE_URL = "sharing_function_image_url";
    private static final String SHARING_FUNCTION_IS_INSTALLED = "sharing_function_is_installed";

    @Override
    public void dispose() {

    }

    @Override
    public Map<String, FREFunction> getFunctions() {
        Map<String, FREFunction> map = new HashMap<String, FREFunction>();
        //映射
        map.put(LOGIN_FUNCTION_QQ, new QQAuthFunction());
        map.put(LOGIN_FUNCTION_WX, new WXAuthFunction());
        map.put(SHARING_FUNCTION_REGISTER, new RegisterSDKFunction());
        map.put(SHARING_FUNCTION_TEXT, new SendTextFunction());
        map.put(SHARING_FUNCTION_LINK, new SendLinkFunction());
        map.put(SHARING_FUNCTION_IMAGE, new SendImageFunction());
        map.put(SHARING_FUNCTION_IMAGE_URL, new SendImageURLFunction());
        map.put(SHARING_FUNCTION_IS_INSTALLED, new IsinstallFunction());
        return map;
    }

}
