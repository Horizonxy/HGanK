package com.horizon.gank.hgank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.horizon.gank.hgank.Constants;
import com.horizon.gank.hgank.R;
import com.horizon.gank.hgank.adapter.quickadapter.BaseAdapterHelper;
import com.horizon.gank.hgank.adapter.quickadapter.QuickAdapter;
import com.horizon.gank.hgank.db.CommonDaoImpl;
import com.horizon.gank.hgank.model.GankModel;
import com.horizon.gank.hgank.model.bean.CommonCacheVo;
import com.horizon.gank.hgank.model.bean.GanKData;
import com.horizon.gank.hgank.model.bean.GanKResult;
import com.horizon.gank.hgank.presenter.GankPresenter;
import com.horizon.gank.hgank.utils.GsonUtils;
import com.horizon.gank.hgank.utils.NetUtils;
import com.horizon.gank.hgank.view.GankView;
import com.horizon.gank.hgank.widget.AutoLoadListView;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscription;

public class GankTypeActivity extends BaseActivity implements GankView {

    @Bind(R.id.gank_list_view)
    AutoLoadListView mListView;

    private int mPageNo;
    private String mType;
    private QuickAdapter mAdapter;
    private List<GanKData> mData;

    private GankPresenter mPresenter;

    private CommonDaoImpl mCommonDao;
    private Map<String, Object> mCacheMap = new HashMap<String, Object>();

    private String ATY = "gank_type_list_";
    private String DATA_TYPE;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gank_type);

        mType = getIntent().getStringExtra(Constants.UNDLE_GANK_TYPE);
        setTitle("干货集中营." + (mType.equals("iOS") ? "IOS" : mType));
        ATY = ATY.concat(mType);
        DATA_TYPE = mType;
        mCommonDao = new CommonDaoImpl(this);

        initView();

        mCacheMap.put(CommonCacheVo.ATY, ATY);
        mCacheMap.put(CommonCacheVo.DATA_TYPE, DATA_TYPE);

        mPageNo = 1;
        mPresenter = new GankPresenter(new GankModel(), this);

        loadGankData();

    }

    @OnClick(R.id.btn_left)
    void back() {
        finish();
    }

    private void loadGankData() {

        if (NetUtils.isNetworkConnected(this)) {
            mPresenter.getGankList();
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCacheMap.put(CommonCacheVo.DATA_PAGE_NO, mPageNo);
                    List<CommonCacheVo> commonlist = mCommonDao.findByColumns(mCacheMap);
                    if(commonlist != null && !commonlist.isEmpty()){
                        List<GanKData> list = GsonUtils.getList(commonlist.get(0).getData(), GanKData.class);

                        if(mPageNo == 1){
                            mData.clear();
                            onInitAfter();
                        }
                        mData.addAll(list);

                        mPageNo++;
                    } else if(mPageNo > 1) {
                        mListView.onFinish();
                        return;
                    }

                    mListView.onComplete();
                }
            }, 500);
        }
    }

    private void initView() {
        setBtnLeft(MaterialDesignIconic.Icon.gmi_mail_reply_all);
        setBtnRight(MaterialDesignIconic.Icon.gmi_refresh);

        mListView.setAdapter(mAdapter = new QuickAdapter<GanKData>(this, R.layout.item_type_list, mData = new ArrayList<GanKData>()) {
            @Override
            protected void convert(BaseAdapterHelper helper, final GanKData item) {
                if(item== null){
                    return;
                }
                helper.setText(R.id.tv_title, item.getDesc())
                        .setText(R.id.tv_user, TextUtils.isEmpty(item.getWho()) ? "" : "via. " + item.getWho())
                .setOnClickListener(R.id.ll_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(GankTypeActivity.this, WebViewActivity.class);
                        intent.putExtra(Constants.BUNDLE_WEBVIEW_URL, item.getUrl());
                        if("休息视频".equals(mType)){
                            intent.putExtra(Constants.BUNDLE_WEBVIEW_VEDIO, true);
                        }
                        startActivity(intent);
                    }
                });
            }
        });

        mListView.setAutoLoadListener(new AutoLoadListView.OnAutoLoadListener() {
            @Override
            public void onLoading() {
                loadGankData();
            }
        });
    }

    @Override
    public int getPageNo() {
        return mPageNo;
    }

    @Override
    public String getType() {
        return mType;
    }

    @Override
    public void failure() {
        if (getPageNo() == 1) {
            onInitAfter();
        }

        mListView.onFailuer();
    }

    @Override
    public void success(GanKResult<GanKData> data) {
        if (getPageNo() == 1) {
            onInitAfter();
            mData.clear();
        }

        mData.addAll(data.getResults());

        if (data.getResults().size() == 0) {
            mListView.onFinish();
        } else {
            mListView.onComplete();
        }

        if(mPageNo == 1){
            Map<String, Object> delMap = new HashMap<String, Object>();
            delMap.put(CommonCacheVo.ATY, ATY);
            delMap.put(CommonCacheVo.DATA_TYPE, DATA_TYPE);
            mCommonDao.deleteByColumns(delMap);
        }

        CommonCacheVo cache = new CommonCacheVo();
        cache.setAty(ATY);
        cache.setData(GsonUtils.getString(data.getResults()));
        cache.setData_type(DATA_TYPE);
        cache.setData_page_no(mPageNo);
        mCommonDao.save(cache);

        mPageNo++;
    }

    @Override
    public void addSubscription(Subscription subscription) {
        super.addCompositeSubscription(subscription);
    }

    private MaterialDesignIconic.Icon getIconByType(String type) {
        if ("Android".equals(type)) {
            return MaterialDesignIconic.Icon.gmi_android;
        } else if ("IOS".equals(type)) {
            return MaterialDesignIconic.Icon.gmi_apple;
        } else if ("JS".equals(type)) {
            return MaterialDesignIconic.Icon.gmi_language_javascript;
        } else if ("Video".equals(type)) {//休息视频
            return MaterialDesignIconic.Icon.gmi_collection_video;
        } else if ("APP".equals(type)) {//APP
            return MaterialDesignIconic.Icon.gmi_apps;
        } else if ("Recom".equals(type)) {//干货推荐
            return MaterialDesignIconic.Icon.gmi_collection_plus;
        } else/* if("DATA".equals(type))*/ {//拓展资源
            return MaterialDesignIconic.Icon.gmi_attachment;
        }
    }
}
