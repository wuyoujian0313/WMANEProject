package com.wmtc.wmane.ANEFunction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.wmtc.wmane.ANETypeConversion;

import org.json.JSONObject;


/**
 * author: wuyoujian
 * Date: 2019/8/23
 */
public class AppBroadcastFunction implements FREFunction {

    private final static String ACTION_ASR_RESULT_FOR_GAME_COURSE_MODE = "ACTION_ASR_RESULT_FOR_GAME_COURSE_MODE";
    private final static String ACTION_TOUCH_PAD_PRESSED_FOR_GAME_COURSE_MODE ="ACTION_TOUCH_PAD_PRESSED_FOR_GAME_COURSE_MODE";

    private FREContext freContext;
    //继承BroadcastReceiver基类
    public class ASRBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.contentEquals(ACTION_ASR_RESULT_FOR_GAME_COURSE_MODE)) {
                String asr = intent.getStringExtra("EXTRA_ASR_RESULT");
                try {
                    JSONObject jsonObject = new JSONObject(asr);
                    String text = jsonObject.getString("text");
                    String javascript = "JS_ASR('" + text + "')";

                    freContext.dispatchStatusEventAsync("text",text);
                } catch (Exception e) {
                    freContext.dispatchStatusEventAsync("error","数据异常");
                }
            } else if (action.contentEquals(ACTION_TOUCH_PAD_PRESSED_FOR_GAME_COURSE_MODE)) {
                String area = intent.getStringExtra("EXTRA_TOUCH_PAD_DETECTED_REGION");
                freContext.dispatchStatusEventAsync("text",area);
            }
        }
    }

    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {

        this.freContext = freContext;
        ASRBroadcastReceiver asrBroadcastReceiver = new ASRBroadcastReceiver();
        //实例化IntentFilter
        IntentFilter intentFilter = new IntentFilter();

        //设置接收广播的类型
        intentFilter.addAction(ACTION_ASR_RESULT_FOR_GAME_COURSE_MODE);
        intentFilter.addAction(ACTION_TOUCH_PAD_PRESSED_FOR_GAME_COURSE_MODE);

        //动态注册
        freContext.getActivity().registerReceiver(asrBroadcastReceiver, intentFilter);


        String action = ANETypeConversion.FREObject2String(freObjects[0]);
        String value = ANETypeConversion.FREObject2String(freObjects[1]);

        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("param",value);
        freContext.getActivity().sendBroadcast(intent);
        return null;
    }
}
