package air.com.weimeitc.wmane;

import com.adobe.fre.FREContext;

/**
 * Created by wuyoujian on 17/4/9.
 */

public class WMANEShare {

    private  FREContext freContext;

    // 单例函数
    public static final WMANEShare getSingleton() {
        return WMANEShare.LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final WMANEShare INSTANCE = new WMANEShare();
    }


    public FREContext getFreContext() {
        return freContext;
    }

    public void setFreContext(FREContext freContext) {
        this.freContext = freContext;
    }
}
