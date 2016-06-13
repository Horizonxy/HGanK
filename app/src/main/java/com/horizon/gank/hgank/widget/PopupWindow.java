package com.horizon.gank.hgank.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.horizon.gank.hgank.Application;
import com.horizon.gank.hgank.utils.DisplayUtils;

public abstract class PopupWindow extends android.widget.PopupWindow {

    public PopupWindow(Context context, int layoutId) {
        super(context);

        TrianglePopupLayout root = (TrianglePopupLayout) LayoutInflater.from(context).inflate(layoutId, null);

        onBindView(root);

        setContentView(root);

        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setTouchable(true);
        setFocusable(true);

        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        setBackgroundDrawable(dw);

        setOutsideTouchable(true);
    }

    public void showAsDropDownCenter(View v) {
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int[] location = new int[2];
        v.getLocationOnScreen(location);

        int viewX = location[0];
        int viewWidth = v.getWidth();
        int popupWidth = getContentView().getMeasuredWidth();
        int viewCenterX = viewX + viewWidth / 2;
        int screenWidth = Application.getInstance().SCREENWIDTH;

        int popupOffsetX = 0;
        if((viewCenterX + popupWidth/2) > screenWidth){ //右侧超出屏幕
            int offsetScreenRight = DisplayUtils.dip2px(Application.getInstance(), 16);
            popupOffsetX = -offsetScreenRight - (popupWidth - viewWidth);
            int layoutOffsetX = (popupWidth/2 + offsetScreenRight) - (screenWidth - viewCenterX);
            ((TrianglePopupLayout)getContentView()).invalidate(layoutOffsetX);
        } else if((viewCenterX - popupWidth/2) < 0){ //左侧超出屏幕
            int offsetScreenLeft = DisplayUtils.dip2px(Application.getInstance(), 16);
            popupOffsetX = offsetScreenLeft;
            int layoutOffsetX =  viewCenterX  - (popupWidth/2 + popupOffsetX);
            ((TrianglePopupLayout)getContentView()).invalidate(layoutOffsetX);
        } else {
            popupOffsetX = (viewWidth - popupWidth) / 2;
        }

        showAsDropDown(v, popupOffsetX/*(int) (-290*1f/ 1080 * Application.getInstance().SCREENWIDTH)*/, 2);
    }

    public abstract void onBindView(View root);

}
