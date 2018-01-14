package air.com.weimeitc.wmane;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;

/**
 * Created by wuyoujian on 17/3/15.
 */

public class wmane implements FREExtension {
    @Override
    public void initialize() {

    }

    @Override
    public FREContext createContext(String s) {
        WMFREContext Context = new WMFREContext();
        return Context;
    }

    @Override
    public void dispose() {

    }
}
