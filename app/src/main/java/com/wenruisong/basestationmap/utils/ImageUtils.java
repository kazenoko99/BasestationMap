package com.wenruisong.basestationmap.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.View;
import android.view.View.MeasureSpec;

import com.wenruisong.basestationmap.BasestationMapApplication;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wen on 2016/3/29.
 */
public class ImageUtils {
   static public Bitmap getViewBitmap(View v) {
       if (v == null) {
           return null;
       }
//       v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//       v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
//       v.buildDrawingCache();
    //   Bitmap bitmap = view.getDrawingCache();
       v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
               MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
       v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

       v.buildDrawingCache(true);

       Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
       Canvas c = new Canvas(bitmap);
       c.translate(-v.getScrollX(), -v.getScrollY());
       v.draw(c);


        return bitmap;
   }

    static public void screenShot(Bitmap bitmap,int arg1) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        if(null == bitmap){
            return ;
        }
        try {
            FileOutputStream fos = new FileOutputStream(
                    Environment.getExternalStorageDirectory() + "/basestationmap"
                            + sdf.format(new Date()) + ".png");
            boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            try {
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuffer buffer = new StringBuffer();
            if (b)
                buffer.append("截屏成功 ");
            else {
                buffer.append("截屏失败 ");
            }
            if (arg1 != 0)
                buffer.append("地图渲染完成，截屏无网格");
            else {
                buffer.append( "地图未渲染完成，截屏有网格");
            }
            ToastUtil.show(BasestationMapApplication.getContext(), buffer.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
