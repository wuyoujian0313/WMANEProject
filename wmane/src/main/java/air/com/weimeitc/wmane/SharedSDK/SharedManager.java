package air.com.weimeitc.wmane.SharedSDK;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import air.com.weimeitc.wmane.QQEntryActivity;


//air.com.weimeitc.bqwx:039fcbae92e6388b7a9babf728ddf696
//air.com.weimeitc.bqdj:233936a9c7c6ff761eb23e34f0e55ceb
//import air.com.weimeitc.bqwx.QQEntryActivity;
//import air.com.weimeitc.bqwh.QQEntryActivity;


public class SharedManager implements ActionSheet.IActionSheetListener {

    private ActionSheet mActionSheet;
    public static Activity activity;
    private List<AISharedPlatformSDKInfo> mSDKInfos;
    private List<AISharedPlatformScene> mScenes;
    private List<String> mMenus;
    //private BaseUiListener loginQQListener;

    private SharedFinishCallback mCallback;
    private SharedDataModel mData;

    // 微信 - 北汽高级维修
//    public static final  String WX_APP_ID = "wx828ddb181a65570c";
//    public static final  String WX_APP_SECRET = "d2f36fee5809ea6d1909ff56e29f1e83";
//    public static final  String QQ_APP_ID = "1106131684";
//    public static final  String QQ_APP_SECRET = "7kuxHSwsLybdLQ5O";

    // 微信 - 北汽汽车维护
//    public static final  String WX_APP_ID = "wx78bf5210b6ebf466";
//    public static final  String WX_APP_SECRET = "d2f36fee5809ea6d1909ff56e29f1e83";
//
//    //APP ID 1106347438 APP KEY NT66deIQ4RNl5gDA
//    public static final  String QQ_APP_ID = "1106347438";
//    public static final  String QQ_APP_SECRET = "NT66deIQ4RNl5gDA";


    // 北汽电机知识
    public static final  String WX_APP_ID = "wxf74876d011fb1356";
    public static final  String WX_APP_SECRET = "fedba484c5f88fc3398eee6bda007dce";
    public static final  String QQ_APP_ID = "1106060269";
    public static final  String QQ_APP_SECRET = "OR7B2A2kRZC6riPH";

    public static final  String WX_APP_REDIRECTURI = "";
    public static IWXAPI wxapi;
    public static final  String QQ_APP_REDIRECTURI = "";



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

    // 注册
    public void regiterSharedSDK(Activity activity) {

        SharedManager.activity = activity;
        this.mActionSheet = new ActionSheet(this.activity);
        this.mActionSheet.setCancelable(false);
        this.mActionSheet.setHaveCancleBtn(true);
        this.mActionSheet.setCanceledOnTouchOutside(true);
        this.mActionSheet.setItemClickListener(this);

        this.mSDKInfos = new ArrayList<>();
        this.mScenes = new ArrayList<>();
        this.mMenus = new ArrayList<>();

        AISharedPlatformSDKInfo sdk1 = new AISharedPlatformSDKInfo(E_AIPlatfrom.AIPlatfromWechat,
                WX_APP_ID,WX_APP_SECRET,WX_APP_REDIRECTURI);
        AISharedPlatformSDKInfo sdk2 = new AISharedPlatformSDKInfo(E_AIPlatfrom.AIPlatfromQQ,
                QQ_APP_ID,QQ_APP_SECRET,QQ_APP_REDIRECTURI);
        this.mSDKInfos.add(sdk1);
        this.mSDKInfos.add(sdk2);
        this.registerSharedPlatform();
    }

    private void registerSharedPlatform() {

        for (AISharedPlatformSDKInfo item:this.mSDKInfos) {

            E_AIPlatfrom platform = item.platfrom;
            if (platform == E_AIPlatfrom.AIPlatfromWechat) {
                this.wxapi = WXAPIFactory.createWXAPI(this.activity,WX_APP_ID,true);
                this.wxapi.registerApp(WX_APP_ID);

                this.mScenes.add(new AISharedPlatformScene(platform, E_AIPlatformScene.AIPlatformSceneSession,"分享到微信好友"));
                this.mScenes.add(new AISharedPlatformScene(platform, E_AIPlatformScene.AIPlatformSceneTimeline,"分享到微信朋友圈"));
                this.mScenes.add(new AISharedPlatformScene(platform, E_AIPlatformScene.AIPlatformSceneFavorite,"分享到微信收藏"));
            } else if(platform == E_AIPlatfrom.AIPlatfromQQ) {

                this.mScenes.add(new AISharedPlatformScene(platform, E_AIPlatformScene.AIPlatformSceneSession,"分享到QQ好友"));
                this.mScenes.add(new AISharedPlatformScene(platform, E_AIPlatformScene.AIPlatformSceneTimeline,"分享到QQ空间"));
            } else if (platform == E_AIPlatfrom.AIPlatfromWeibo) {

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
        } else if (scene.platfrom == E_AIPlatfrom.AIPlatfromWeibo ) {
            sharedToWeibo(scene);
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
                e.printStackTrace();;
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
            File file = new File(SharedManager.activity.getExternalCacheDir(),"temp.png");
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




    private void sharedToWeibo(AISharedPlatformScene scene) {

    }

    private static enum E_AIPlatfrom {
        AIPlatfromWechat,
        AIPlatfromQQ,
        AIPlatfromWeibo
    }

    private static enum  E_AIPlatformScene {
        AIPlatformSceneSession,
        AIPlatformSceneTimeline,
        AIPlatformSceneFavorite
    }

    private class AISharedPlatformSDKInfo {

        public E_AIPlatfrom platfrom;
        public String appId;
        public String appSecret;
        public String redirectURI;


        AISharedPlatformSDKInfo (E_AIPlatfrom platfrom,String appId,String appSecret,String redirectURI) {
            this.platfrom = platfrom;
            this.appId = appId;
            this.appSecret = appSecret;
            this.redirectURI = redirectURI;
        }
    }

    private class AISharedPlatformScene {
        public  E_AIPlatfrom platfrom;
        public  E_AIPlatformScene scene;
        public  String sceneName;
        AISharedPlatformScene(E_AIPlatfrom platfrom, E_AIPlatformScene scene,String sceneName) {
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

