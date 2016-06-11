package com.horizon.gank.hgank.utils;

import android.content.Context;

import java.lang.reflect.Field;

public class DisplayUtils {

    public static int getStatusBarHeight(Context context){
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
