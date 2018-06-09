package com.wmtc.wmane.ANEFunction;

import android.graphics.Bitmap;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

import com.wmtc.wmane.ANETypeConversion;
import com.wmtc.wmane.SharedSDK.SharedManager;

/**
 * Created by wuyoujian on 17/3/15.
 */

public class SendImageFunction implements FREFunction {
    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {

        Bitmap value = ANETypeConversion.FREObject2Bitmap(freObjects[0]);
        if (value != null) {
            SharedManager.SharedDataModel model = SharedManager.getSingleton().new SharedDataModel();

            model.dataType = SharedManager.E_SharedDataType.SharedDataTypeImage;
            model.image = value;

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
