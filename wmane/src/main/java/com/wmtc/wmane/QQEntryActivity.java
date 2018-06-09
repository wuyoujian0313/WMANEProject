package com.wmtc.wmane;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;


import org.json.JSONObject;

import java.io.File;

import com.wmtc.wmane.SharedSDK.SharedManager;
import com.wmtc.wmane.SharedSDK.ThreadManager;

/**
 * Created by wuyoujian on 2017/5/4.
 */

public class QQEntryActivity extends Activity {

    private BaseUiListener loginQQListener;
    private SharedUiListener QQShareListener;

    public static Tencent tencentAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tencentAPI = Tencent.createInstance(SharedManager.getSingleton().getAppId(SharedManager.E_AIPlatfrom.AIPlatfromQQ),QQEntryActivity.this);
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        if (type.equalsIgnoreCase("sharing")) {

            QQShareListener = new SharedUiListener();
            final Bundle params = intent.getBundleExtra("params");

            // QQ分享要在主线程做
            ThreadManager.getMainHandler().post(new Runnable() {

                @Override
                public void run() {
                    tencentAPI.shareToQQ(QQEntryActivity.this,params,QQShareListener);
                }
            });



        } else {
            loginQQListener = new BaseUiListener();
            if (!tencentAPI.isSessionValid()) {
                tencentAPI.login(QQEntryActivity.this, "all", loginQQListener);
            } else {
                tencentAPI.logout(QQEntryActivity.this);
            }
        }
    }

    private class SharedUiListener implements IUiListener {

        @Override
        public void onCancel() {

            File file = new File(QQEntryActivity.this.getExternalCacheDir(), "temp.png");
            if (file.exists()) {
                file.delete();
            }
            finish();

        }

        @Override
        public void onComplete(Object response) {

            File file = new File(QQEntryActivity.this.getExternalCacheDir(), "temp.png");
            if (file.exists()) {
                file.delete();
            }

            finish();
        }

        @Override
        public void onError(UiError e) {

            File file = new File(QQEntryActivity.this.getExternalCacheDir(), "temp.png");
            if (file.exists()) {
                file.delete();
            }

            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode,resultCode,data,loginQQListener);
        } else if (requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_QZONE_SHARE) {
            Tencent.onActivityResultData(requestCode,resultCode,data,QQShareListener);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                finish();
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            doComplete((JSONObject)response);
        }

        protected void doComplete(JSONObject values) {
            //
            try {
                String openid = values.getString("openid");
                WMANEShare.getSingleton().getFreContext().dispatchStatusEventAsync("login_function_qq",openid +"###"+openid);
            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
        }

        @Override
        public void onError(UiError e) {
            finish();
        }

        @Override
        public void onCancel() {
            finish();
        }
    }
}
