package air.com.weimeitc.wmane.ANEFunction;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

import air.com.weimeitc.wmane.ANETypeConversion;
import air.com.weimeitc.wmane.SharedSDK.SharedManager;


/**
 * Created by wuyoujian on 17/3/15.
 */

public class SendImageURLFunction implements FREFunction {
    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {

        String value = ANETypeConversion.FREObject2String(freObjects[0]);
        if (value != null) {
            SharedManager.SharedDataModel model = SharedManager.getSingleton().new SharedDataModel();

            model.dataType = SharedManager.E_SharedDataType.SharedDataTypeImageURL;
            model.url = value;

            SharedManager.getSingleton().sharedData(model, new SharedManager.SharedFinishCallback() {
                @Override
                public void finishSharedCallback(int statusCode, Object resp) {
                    //
                }
            });
        }
        return null;
    }
}
