package com.wmtc.wmane.ANEFunction;

import android.app.Activity;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.wmtc.wmane.ANETypeConversion;
import com.wmtc.wmane.SharedSDK.SharedManager;
import com.wmtc.wmane.WMANEShare;


/**
 * Created by wuyoujian on 17/3/15.
 */

public class RegisterSDKFunction implements FREFunction {
    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {
        try {
            WMANEShare.getSingleton().setFreContext(freContext);
            Activity activity = freContext.getActivity();
            String value = ANETypeConversion.FREObject2String(freObjects[0]);
            if (value != null) {
                Gson gson = new Gson();

                Type type = new TypeToken<ArrayList<SharedManager.AISharedPlatformSDKInfo>>(){}.getType();
                ArrayList<SharedManager.AISharedPlatformSDKInfo> sdks = gson.fromJson(value, type);
                SharedManager.getSingleton().regiterSharedPlatforms(activity,sdks);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
