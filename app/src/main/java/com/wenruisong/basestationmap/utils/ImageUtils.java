package com.wenruisong.basestationmap.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.MeasureSpec;

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

  //  image.setImageBitmap(bitmap);
}
