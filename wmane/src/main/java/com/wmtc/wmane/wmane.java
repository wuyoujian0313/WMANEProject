package com.wmtc.wmane;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;

import org.json.JSONObject;

/**
 * Created by wuyoujian on 17/3/15.
 */

public class wmane implements FREExtension {

    private static WMFREContext context;



    @Override
    public void initialize() {


    }

    @Override
    public FREContext createContext(String s) {
        if (context == null) context = new WMFREContext();
        return context;
    }

    @Override
    public void dispose() {

    }

    public static void dispatchStatusEventAsync(String code, String level) {
        if(context != null) {
            context.dispatchStatusEventAsync(code, level);
        }
    }
}
