package com.horizon.gank.hgank.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.horizon.gank.hgank.Application;

public abstract class PopupWindow extends android.widget.PopupWindow {

    TrianglePopupLayout root;

    public PopupWindow(Context context, int layoutId) {
        super(context);

        root = (TrianglePopupLayout) LayoutInflater.from(context).inflate(layoutId, null);

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
//        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        int[] location = new int[2];
//        v.getLocationOnScreen(location);
//
//        int viewX = location[0];
//        int viewWidth = v.getWidth();
//        int popupWidth = getContentView().getMeasuredWidth();
//
//        int offsetX = viewX + viewWidth / 2 - popupWidth/2;
//
//        root.invalidate(offsetX);

        showAsDropDown(v, (int) (-290*1f/ 1080 * Application.getInstance().SCREENWIDTH), 2);
    }

    public abstract void onBindView(View root);

}
