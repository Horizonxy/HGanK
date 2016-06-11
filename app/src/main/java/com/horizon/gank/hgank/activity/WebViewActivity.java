package com.horizon.gank.hgank.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.horizon.gank.hgank.Constants;
import com.horizon.gank.hgank.R;
import com.horizon.gank.hgank.utils.LogUtils;
import com.horizon.gank.hgank.web.WebChromeClient;
import com.horizon.gank.hgank.web.WebView;
import com.horizon.gank.hgank.web.WebViewClient;
import com.horizon.gank.hgank.web.WebViewView;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import butterknife.Bind;
import butterknife.OnClick;

public class WebViewActivity extends BaseActivity implements WebViewView {

    @Bind(R.id.web_view)
    WebView mWevView;
    @Bind(R.id.progress)
    ProgressBar mProgress;

    boolean firstLoadAfter;
    String url;

    @Bind(R.id.tv_other)
    TextView tvOther;

    private boolean mHasVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        setBtnLeft(MaterialDesignIconic.Icon.gmi_mail_reply_all);

        firstLoadAfter = true;

        mWevView.setWebChromeClient(new WebChromeClient(this));
        mWevView.setWebViewClient(new WebViewClient(this));

        url = getIntent().getStringExtra(Constants.BUNDLE_WEBVIEW_URL);
        mHasVideo = getIntent().getBooleanExtra(Constants.BUNDLE_WEBVIEW_VEDIO, false);

        if(url != null) {
            if(!url.startsWith("http")){
                url = "http://".concat(url);
            }
            firstLoad();
        }
    }



    @OnClick(R.id.btn_left)
    void clickLeft(){
        back();
    }

    private void firstLoad(){
        if(isNetworkAvailable()) {
            mWevView.loadUrl(url);
        } else {
            onNoNet();
        }
    }

    @Override
    void onRetry() {
        onInitBody();
        firstLoad();
    }

    @Override
    public void firstLoadAfter(){
        if(firstLoadAfter){
            onInitAfter();
            firstLoadAfter = false;
        }
        LogUtils.e("firstLoadAfter "+mHasVideo+" "+!tvOther.isShown());

        if(mHasVideo && !tvOther.isShown()){
            tvOther.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.tv_other)
    void otherClick(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mWevView.getUrl()));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        mWevView.onResume();
        mWevView.resumeTimers();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mWevView.onPause();
        mWevView.pauseTimers();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWevView.stopLoading();
        mWevView.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    back();
                    return true;
                default:
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void back(){
        if (url != null) {
            if(url.equals(mWevView.getUrl())){
                finish();
            } else if (null != mWevView.getUrl() && mWevView.getUrl().startsWith("data:")) {//页面请求失败，报错
                if (mWevView.canGoBackOrForward(-2)) {
                    mWevView.goBackOrForward(-2);
                    finish();
                } else if (mWevView.canGoBack()) {
                    mWevView.goBack();
                }else {
                    finish();
                }
            } else if (mWevView.canGoBack()) {
                mWevView.goBack();
            } else {
                finish();
            }
        } else if(mWevView.canGoBack()){
            mWevView.goBack();
        } else {
            finish();
        }
    }

    @Override
    public void onReceivedTitle(String title) {
        setTitle(title);
    }

    @Override
    public void onProgressChanged(int newProgress) {
        if(newProgress == 100){
            mProgress.setVisibility(View.GONE);
        } else {
            if(mProgress.getVisibility() == View.GONE){
                mProgress.setVisibility(View.VISIBLE);
            }
            mProgress.setProgress(newProgress);
        }
    }

    private boolean isNetworkAvailable() {
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }
}
