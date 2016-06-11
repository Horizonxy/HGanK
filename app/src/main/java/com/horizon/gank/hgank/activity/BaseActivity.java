package com.horizon.gank.hgank.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.horizon.gank.hgank.Application;
import com.horizon.gank.hgank.R;
import com.horizon.gank.hgank.utils.SystemStatusManager;
import com.horizon.gank.hgank.utils.ThemeUtils;
import com.horizon.gank.hgank.widget.MonIndicator;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BaseActivity extends AutoLayoutActivity {

    @Bind(R.id.base_init_load)
    MonIndicator mInitLoad;
    @Bind(R.id.base_no_net)
    LinearLayout llNoNet;

    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.btn_left)
    ImageButton btnLeft;
    @Bind(R.id.btn_right)
    ImageButton btnRight;

    FrameLayout layoutContent;

    CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application.getInstance().addAty(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View layout = getLayoutInflater().inflate(R.layout.activity_base, null);
        View vContent = getLayoutInflater().inflate(layoutResID, null);

        layoutContent = (FrameLayout) layout.findViewById(R.id.base_layout_content);
        layoutContent.addView(vContent);

        SystemStatusManager.setTranslucentStatusColor(this, ThemeUtils.getThemeColor(this, R.attr.colorPrimary));

        super.setContentView(layout);

        ButterKnife.bind(this);

        onInitBody();
    }

    public void setTitle(String title) {
        if (title == null) {
            title = "";
        }
        tvTitle.setText(title);
    }


    public void setBtnLeft(MaterialDesignIconic.Icon icon){
        if(btnLeft != null) {
            btnLeft.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(icon));
        }
    }

    public void setBtnRight(MaterialDesignIconic.Icon icon){
        if(btnRight != null) {
            btnRight.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(icon));
        }
    }

    public void onInitBody(){
        mInitLoad.setVisibility(View.VISIBLE);
        layoutContent.setVisibility(View.GONE);
        llNoNet.setVisibility(View.GONE);
    }

    public void onInitAfter(){
        mInitLoad.setVisibility(View.GONE);
        layoutContent.setVisibility(View.VISIBLE);
        llNoNet.setVisibility(View.GONE);
    }

    public void onNoNet(){
        mInitLoad.setVisibility(View.GONE);
        layoutContent.setVisibility(View.GONE);
        llNoNet.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.base_btn_retry)
    void onRetry(){

    }

    public void addCompositeSubscription(Subscription subscription) {
        if(mCompositeSubscription == null){
            mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(subscription);
    }


    @Override
    protected void onDestroy() {
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
        Application.getInstance().removeAty(this);

        super.onDestroy();
    }
}
