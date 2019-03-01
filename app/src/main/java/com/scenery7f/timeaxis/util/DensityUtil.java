package com.scenery7f.timeaxis.util;

import android.content.Context;

/**
 * Created by snoopy on 2017/9/15.
 */

public class DensityUtil {

    private static Context context;

    public static void setContext(Context context) {
        DensityUtil.context = context;
    }

    public static int dip2px(float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }
    public static int px2dip(float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }
}
