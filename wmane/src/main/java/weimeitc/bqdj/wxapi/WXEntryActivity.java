package air.com.weimeitc.bqdj.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.wmtc.wmane.SharedSDK.SharedManager;
import com.wmtc.wmane.WMANEShare;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            SharedManager.wxapi.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        SharedManager.wxapi.handleIntent(intent, this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {}

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle data = msg.getData();
            String nickname = data.getString("nickname");
            String unionId = data.getString("unionId");
            // UI界面的更新等相关操作

            WMANEShare.getSingleton().getFreContext().dispatchStatusEventAsync("login_function_qq",nickname +"###"+unionId);
        }
    };


    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (resp.getType()== 1) {
                    final String code = ((SendAuth.Resp) resp).code;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 在这里进行 http request.网络请求相关操作
                            try {

                                if (code != null) {
                                    String appId = SharedManager.WX_APP_ID;
                                    String appSecret = SharedManager.WX_APP_SECRET;

                                    final String url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                                            appId, appSecret, code);

                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder()
                                            .url(url)
                                            .build();

                                    Response response = client.newCall(request).execute();
                                    String responseData = response.body().string();

                                    Gson gson = new Gson();
                                    WXAccessTokenInfo accessTokenInfo = gson.fromJson(responseData, WXAccessTokenInfo.class);

                                    final String infoUrl = String.format("https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s",
                                            accessTokenInfo.getAccess_token(), accessTokenInfo.getOpenid());

                                    Request requestInfo = new Request.Builder()
                                            .url(infoUrl)
                                            .build();

                                    Response responseInfo = client.newCall(requestInfo).execute();
                                    String infoString = responseInfo.body().string();
                                    WXUserInfo userInfo = gson.fromJson(infoString, WXUserInfo.class);

                                    Message msg = new Message();
                                    Bundle data = new Bundle();
                                    data.putString("nickname", userInfo.getNickname());
                                    data.putString("unionId", userInfo.getUnionid());
                                    msg.setData(data);
                                    handler.sendMessage(msg);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                break;
            default:
                break;
        }
        //

        this.finish();
    }
}
