package air.com.weimeitc.wmane;

/**
 * Created by wuyoujian on 17/3/15.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.adobe.fre.*;

public class ANETypeConversion {
    public static String FREObject2String(FREObject object ) {
        String text = null;
        try {
            text = object.getAsString();
        } catch (Exception e) {
            return null;
        }

        return text;
    }

    public static FREObject String2FREObject(String text) {
        if (text == null) {
            return null;
        }

        FREObject object = null;
        try {
            object = FREObject.newObject(text);
        } catch (Exception e) {
            return null;
        }

        return object;
    }

    public static Bitmap FREObject2Bitmap(FREObject object) {
        Bitmap bmp = null;
        try {
            FREBitmapData asBitmap = (FREBitmapData) object;
            bmp = ANEHelper.getBitmapFromFreBitmapdata(asBitmap);
        } catch (Exception e) {
            return null;

        }

        return bmp;
    }

    public static FREObject boolean2FREObject(boolean b) {
        FREObject object = null;
        try {
            object = FREObject.newObject(b);
        } catch (Exception e) {
            return null;
        }

        return object;
    }

    public static int FREObject2Int(FREObject object) {
        int value = 0;
        try {
            value = object.getAsInt();
        } catch (Exception e) {
            return 0;
        }

        return value;
    }

    private static class ANEHelper {
        public static Bitmap getBitmapFromFreBitmapdata(final FREBitmapData as3Bitmap){
            //http://stackoverflow.com/questions/17314467/bitmap-channels-order-different-in-android
            Bitmap m_encodingBitmap         = null;
            Canvas m_canvas                 = null;
            Paint m_paint                   = null;
            final float[] m_bgrToRgbColorTransform  =
                    {
                            0,  0,  1f, 0,  0,
                            0,  1f, 0,  0,  0,
                            1f, 0,  0,  0,  0,
                            0,  0,  0,  1f, 0
                    };
            final ColorMatrix m_colorMatrix               = new ColorMatrix(m_bgrToRgbColorTransform);
            final ColorMatrixColorFilter m_colorFilter               = new ColorMatrixColorFilter(m_colorMatrix);
            try{
                as3Bitmap.acquire();
                int srcWidth = as3Bitmap.getWidth();
                int srcHeight = as3Bitmap.getHeight();
                m_encodingBitmap    = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
                m_canvas        = new Canvas(m_encodingBitmap);
                m_paint         = new Paint();
                m_paint.setColorFilter(m_colorFilter);

                m_encodingBitmap.copyPixelsFromBuffer(as3Bitmap.getBits());
                as3Bitmap.release();
            }catch (Exception e) {
                e.printStackTrace();
            }
            //
            // Convert the bitmap from BGRA to RGBA.
            //
            m_canvas.drawBitmap(m_encodingBitmap, 0, 0, m_paint);
            return m_encodingBitmap;
        }
    }
}
