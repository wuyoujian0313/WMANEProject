package air.com.weimeitc.wmane.ANEFunction;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

import air.com.weimeitc.wmane.ANETypeConversion;
import air.com.weimeitc.wmane.SharedSDK.SharedManager;


/**
 * Created by wuyoujian on 17/3/15.
 */

public class IsinstallFunction implements FREFunction {
    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {
        boolean isInstalled = SharedManager.getSingleton().isInstallSharedApp();
        return ANETypeConversion.boolean2FREObject(isInstalled);
    }
}
