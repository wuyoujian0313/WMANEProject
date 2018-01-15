package air.com.weimeitc.wmane.ANEFunction;

import android.app.Activity;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.SocketHandler;

import air.com.weimeitc.wmane.ANETypeConversion;
import air.com.weimeitc.wmane.SharedSDK.SharedManager;
import air.com.weimeitc.wmane.WMANEShare;


/**
 * Created by wuyoujian on 17/3/15.
 */

public class RegisterSDKFunction implements FREFunction {
    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {
        try {
            Activity activity = freContext.getActivity();
            WMANEShare.getSingleton().setFreContext(freContext);

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
