package com.horizon.gank.hgank.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.horizon.gank.hgank.utils.DisplayUtils;
import com.zhy.autolayout.AutoLinearLayout;

public class TrianglePopupLayout extends AutoLinearLayout {

    private Paint mBorderPaint;
    private Path mBorderPath;

    private int mBorderColor = Color.BLACK;
    private int mBorderWidth = 2;
    private int mTrangleWidth;
    private int mTrangleHeight;
    private int mOffsetX;

    public TrianglePopupLayout(Context context) {
        this(context, null);
    }

    public TrianglePopupLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        int padding = DisplayUtils.dip2px(context, 1);
        setPadding(padding, padding, padding, padding);
        mTrangleWidth = DisplayUtils.dip2px(context, 8);

        initPaint();
    }

    void initPaint(){
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mBorderPaint.setColor(mBorderColor);
//        mBorderPaint.setStyle(Paint.Style.FILL);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setDither(true);
        mBorderPaint.setPathEffect(new CornerPathEffect(8));//画的线的连接处，有点圆角
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        //mTrangleWidth = 40;//(int) (20 * height * 1f / 150);

        mTrangleHeight = (int) Math.sqrt(Math.pow(mTrangleWidth, 2) - Math.pow(mTrangleWidth/2, 2));
        setMeasuredDimension(width, height + mTrangleHeight);

        if(mOffsetX > width){
            mOffsetX = width - mTrangleWidth - (getMeasuredWidth() - mTrangleWidth) / 2;
        } else if(mOffsetX < 0){
            mOffsetX = -(getMeasuredWidth() - mTrangleWidth) / 2;
        }
        int trangleStart = (getMeasuredWidth() - mTrangleWidth) / 2 + mOffsetX;

        initPath(trangleStart, width, height);
    }

    private void initPath(int trangleStart, int width, int height){
        mBorderPath = new Path();
        mBorderPath.moveTo(0, mTrangleHeight);
        mBorderPath.lineTo(trangleStart, mTrangleHeight);
        mBorderPath.lineTo(trangleStart + mTrangleWidth/2, 0);
        mBorderPath.lineTo(trangleStart + mTrangleWidth, mTrangleHeight);
        mBorderPath.lineTo(width, mTrangleHeight);
        mBorderPath.lineTo(width, mTrangleHeight + height);
        mBorderPath.lineTo(0, mTrangleHeight + height);
        mBorderPath.close();
    }

    public void invalidate(int x){
        mOffsetX = x;
//        int trangleStart = (getMeasuredWidth() - mTrangleWidth) / 2 + x;
//        initPath(trangleStart, getMeasuredWidth(), getMeasuredHeight() - mTrangleHeight);
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        mBorderPaint.setColor(Color.WHITE);
        mBorderPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mBorderPath, mBorderPaint);

        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mBorderPath, mBorderPaint);

        canvas.translate(0, mTrangleHeight);
        super.dispatchDraw(canvas);
    }
}
