package com.wmtc.wmane.SharedSDK;

/**
 * Created by wuyoujian on 17/3/14.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.*;
import com.tencent.mm.opensdk.openapi.*;
import com.wmtc.wmane.QQEntryActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class SharedManager implements ActionSheet.IActionSheetListener {

    private ActionSheet mActionSheet;
    private ArrayList<AISharedPlatformSDKInfo> mSDKInfos;
    private List<AISharedPlatformScene> mScenes;
    private List<String> mMenus;
    private SharedFinishCallback mCallback;
    private SharedDataModel mData;

    private  IWXAPI wxapi;
    private  Activity activity;

    public IWXAPI getWxapi() {
        return wxapi;
    }

    public void setWxapi(IWXAPI wxapi) {
        this.wxapi = wxapi;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    // 分享回调函数
    public interface SharedFinishCallback {
        void finishSharedCallback(int statusCode, Object resp);
    }

    // 单例函数
    public static final SharedManager getSingleton() {
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final SharedManager INSTANCE = new SharedManager();
    }

    // 是否安装微信、QQ等app
    public boolean isInstallSharedApp () {
        return wxapi.isWXAppInstalled();
    }

    public void regiterSharedPlatforms(Activity activity, ArrayList<AISharedPlatformSDKInfo> platforms) {

        this.activity = activity;
        this.mActionSheet = new ActionSheet(activity);
        this.mActionSheet.setHaveCancleBtn(true);
        this.mActionSheet.setCancelable(false);
        this.mActionSheet.setHaveCancleBtn(true);
        this.mActionSheet.setCanceledOnTouchOutside(true);
        this.mActionSheet.setItemClickListener(this);

        this.mSDKInfos = new ArrayList<>();
        this.mScenes = new ArrayList<>();
        this.mMenus = new ArrayList<>();

        for (AISharedPlatformSDKInfo sdk:platforms) {
            this.mSDKInfos.add(sdk);
        }

        this.registerSharedPlatform();
    }

    private void registerSharedPlatform() {

        for (AISharedPlatformSDKInfo item:this.mSDKInfos) {
            int platform = item.platform;
            if (platform == E_AIPlatfrom.AIPlatfromWechat) {
                this.wxapi = WXAPIFactory.createWXAPI(this.activity,item.getAppId(),true);
                this.wxapi.registerApp(item.getAppId());

                this.mScenes.add(new AISharedPlatformScene(platform, E_AIPlatformScene.AIPlatformSceneSession,"分享到微信好友"));
                this.mScenes.add(new AISharedPlatformScene(platform, E_AIPlatformScene.AIPlatformSceneTimeline,"分享到微信朋友圈"));
                this.mScenes.add(new AISharedPlatformScene(platform, E_AIPlatformScene.AIPlatformSceneFavorite,"分享到微信收藏"));
            } else if(platform == E_AIPlatfrom.AIPlatfromQQ) {

                this.mScenes.add(new AISharedPlatformScene(platform, E_AIPlatformScene.AIPlatformSceneSession,"分享到QQ好友"));
                this.mScenes.add(new AISharedPlatformScene(platform, E_AIPlatformScene.AIPlatformSceneTimeline,"分享到QQ空间"));
            }
        }

        this.addActionSheetMenu();
    }

    private void addActionSheetMenu() {
        for (AISharedPlatformScene scene:this.mScenes ) {
            this.mMenus.add(scene.sceneName);
        }

        this.mActionSheet.setOtherButtonTitlesSimple(this.mMenus);
    }

    public String getAppId(int platform) {
        for (AISharedPlatformSDKInfo sdk:this.mSDKInfos) {
            if (sdk.platform == platform) {
                return sdk.getAppId();
            }
        }

        return null;
    }

    public String getAppSecret(int platform) {
        for (AISharedPlatformSDKInfo sdk:this.mSDKInfos) {
            if (sdk.platform == platform) {
                return sdk.getAppSecret();
            }
        }

        return null;
    }

    public void loginByWX() {
        // send oauth request
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "weimeitc_aneProject";

        this.wxapi.sendReq(req);
    }

    public void loginByQQ() {
        Intent intent = new Intent(activity,QQEntryActivity.class);
        intent.putExtra("type","auth");
        activity.startActivity(intent);
    }


    public void sharedData(SharedDataModel data,SharedFinishCallback callback)  {
        this.mData = data;
        this.mCallback = callback;
        this.mActionSheet.show();
    }

    @Override
    public void onActionSheetItemClick(ActionSheet actionSheet, int itemPosition, ActionSheet.ItemModel itemModel) {
        AISharedPlatformScene scene = this.mScenes.get(itemPosition);
        if (scene.platfrom == E_AIPlatfrom.AIPlatfromWechat) {
            sharedToWeixin(scene);
        } else if (scene.platfrom == E_AIPlatfrom.AIPlatfromQQ) {
            sharedToQQ(scene);
        }
    }

    private void sharedToWeixin(AISharedPlatformScene scene) {

        if (!this.wxapi.isWXAppInstalled()) {
            Toast.makeText(this.activity,"手机未安装微信客户端!",Toast.LENGTH_SHORT).show();
            return;
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.scene = scene.scene.ordinal();

        if (this.mData.dataType == E_SharedDataType.SharedDataTypeText) {

            WXTextObject textObj = new WXTextObject();
            textObj.text = this.mData.content;

            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = textObj;
            msg.title = "title";
            msg.description = "description";

            req.transaction = APIHelper.buildTransaction("text");
            req.message = msg;
        } else if (this.mData.dataType == E_SharedDataType.SharedDataTypeImage) {
            //req.
            if (this.mData.image != null) {
                WXImageObject imgObj = new WXImageObject(this.mData.image);
                WXMediaMessage msg = new WXMediaMessage();
                msg.mediaObject = imgObj;

                Bitmap thumbBmp = Bitmap.createScaledBitmap(this.mData.image, APIHelper.THUMB_SIZE, APIHelper.THUMB_SIZE, true);
                msg.thumbData = APIHelper.bmpToByteArray(thumbBmp,true);

                req.transaction = APIHelper.buildTransaction("image");
                req.message = msg;
            }
        } else if (this.mData.dataType == E_SharedDataType.SharedDataTypeImageURL) {

            WXImageObject imageObject = new WXImageObject();

            WXMediaMessage msg = new WXMediaMessage();
            try {
                Bitmap bmp = BitmapFactory.decodeStream(new URL(this.mData.url).openStream());
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, APIHelper.THUMB_SIZE, APIHelper.THUMB_SIZE, true);

                msg.thumbData = APIHelper.bmpToByteArray(thumbBmp, true);
                imageObject.imageData = APIHelper.bmpToByteArray(bmp,true);
                msg.mediaObject = imageObject;

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            req.transaction = APIHelper.buildTransaction("imageURL");
            req.message = msg;
        } else if (this.mData.dataType == E_SharedDataType.SharedDataTypeURL) {

            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = this.mData.url;

            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = this.mData.title;
            msg.description = this.mData.content;

            req.transaction = APIHelper.buildTransaction("webpage");
            req.message = msg;
        }

        wxapi.sendReq(req);
    }


    private void sharedToQQ(AISharedPlatformScene scene) {

        final Bundle params = new Bundle();

        params.putString(QQShare.SHARE_TO_QQ_TITLE, "QQ分享");
        if (this.mData.dataType == E_SharedDataType.SharedDataTypeText) {
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY,this.mData.content);
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        } else if (this.mData.dataType == E_SharedDataType.SharedDataTypeImage) {
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,QQShare.SHARE_TO_QQ_TYPE_IMAGE);

            Bitmap bm = this.mData.image;
            File file = new File(this.activity.getExternalCacheDir(),"temp.png");
            if (file.exists()){
                file.delete();
            }

            try {
                FileOutputStream out = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG,90,out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String filePath = file.getPath();
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,filePath);

        } else if (this.mData.dataType == E_SharedDataType.SharedDataTypeImageURL) {
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,this.mData.imageUrl);
        } else if (this.mData.dataType == E_SharedDataType.SharedDataTypeURL) {
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,this.mData.url);
        }

        Intent intent = new Intent(activity,QQEntryActivity.class);
        intent.putExtra("type","sharing");
        intent.putExtra("params",params);
        activity.startActivity(intent);
    }

    public class E_AIPlatfrom {
        public static final int AIPlatfromWechat = 0;
        public static final int AIPlatfromQQ = 1;

    }


    public class AISharedPlatformSDKInfo {

        private int platform;
        private String appId;
        private String appSecret;

        public int getPlatform() {
            return platform;
        }

        public void setPlatform(int platform) {
            this.platform = platform;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }
    }

    private static enum  E_AIPlatformScene {
        AIPlatformSceneSession,
        AIPlatformSceneTimeline,
        AIPlatformSceneFavorite
    }

    private class AISharedPlatformScene extends Object {
        public  int platfrom;
        public  E_AIPlatformScene scene;
        public  String sceneName;
        AISharedPlatformScene(int platfrom, E_AIPlatformScene scene,String sceneName) {
            this.platfrom = platfrom;
            this.scene = scene;
            this.sceneName = sceneName;
        }
    }

    public static enum E_SharedDataType {
        SharedDataTypeText,     // 文字分享
        SharedDataTypeImage,    // 图片分享
        SharedDataTypeImageURL, // 远程图片分享
        SharedDataTypeURL,      // 网页分享
        SharedDataTypeMusic,    // 音乐分享
        SharedDataTypeVideo,    // 视频分享
    }

    /* ！！！！！！！分享支持的类型说明
    1、微信和新浪微博都支持SharedDataType定义的类型
    2、QQ目前为了QQ聊天和QQ空间统一，就都只是支持：
    SharedDataTypeText、SharedDataTypeImage、SharedDataTypeURL这4个类型的分享
    */

    // ！！！！！！ 请注意上面支持的类型说明
    public class SharedDataModel {

        public E_SharedDataType dataType;
        public String title;
        public String content;
        public String url;
        public String lowBandUrl;
        public Bitmap image;
        public String imageUrl;
    }

    private static class APIHelper {

        private static final int THUMB_SIZE = 100;
        public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
            if (needRecycle) {
                bmp.recycle();
            }

            byte[] result = output.toByteArray();
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        public static String buildTransaction(final String type) {
            return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
        }
    }
}

