package com.horizon.gank.hgank.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.horizon.gank.hgank.Constants;
import com.horizon.gank.hgank.R;
import com.horizon.gank.hgank.adapter.WelfareAdapter;
import com.horizon.gank.hgank.db.CommonDaoImpl;
import com.horizon.gank.hgank.model.GankModel;
import com.horizon.gank.hgank.model.bean.CommonCacheVo;
import com.horizon.gank.hgank.model.bean.GanKData;
import com.horizon.gank.hgank.model.bean.GanKResult;
import com.horizon.gank.hgank.presenter.GankPresenter;
import com.horizon.gank.hgank.utils.GsonUtils;
import com.horizon.gank.hgank.utils.NetUtils;
import com.horizon.gank.hgank.utils.ThemeUtils;
import com.horizon.gank.hgank.view.GankView;
import com.horizon.gank.hgank.widget.AutoRecyclerView;
import com.horizon.gank.hgank.widget.PopupWindow;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscription;

public class MainActivity extends BaseActivity implements GankView {

    @Bind(R.id.recycler_view)
    AutoRecyclerView mRecyclerView;

    private static final String TITLE = "福利";
    private int mPageNo;

    private List<GanKData> mData;
    private GankPresenter mPresenter;

    private WelfareAdapter mAdapter;

    private PopupWindow popupWindow;

    private CommonDaoImpl mCommonDao;
    private Map<String, Object> mCacheMap = new HashMap<String, Object>();

    private String ATY = "gank_type_list_";
    private String DATA_TYPE;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("干货集中营." + TITLE);
        ATY = ATY.concat(TITLE);
        DATA_TYPE = TITLE;
        mCommonDao = new CommonDaoImpl(this);

        initView();

        mCacheMap.put(CommonCacheVo.ATY, ATY);
        mCacheMap.put(CommonCacheVo.DATA_TYPE, DATA_TYPE);

        mPageNo = 1;
        mPresenter = new GankPresenter(new GankModel(), this);

        loadGankData();
    }

    private void loadGankData(){
        if(NetUtils.isNetworkConnected(this)){
            mPresenter.getGankList();
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                    //加载缓存
                    mCacheMap.put(CommonCacheVo.DATA_PAGE_NO, mPageNo);
                    List<CommonCacheVo> commonlist = mCommonDao.findByColumns(mCacheMap);
                    if(commonlist != null && !commonlist.isEmpty()){
                        List<GanKData> list = GsonUtils.getList(commonlist.get(0).getData(), GanKData.class);
                        if(mPageNo == 1){
                            onInitAfter();
                            mData.clear();
                        }
                        mData.addAll(list);

                        mPageNo++;
                    } else if(mPageNo > 1) {
                        //mListView.onFinish();
                        return;
                    }
                    mAdapter.notifyDataSetChanged();
                    //mListView.onComplete();
                }
            }, 500);
        }
    }

    private void initView() {
        setBtnLeft(MaterialDesignIconic.Icon.gmi_fire);
        setBtnRight(MaterialDesignIconic.Icon.gmi_more);
        btnRight.setVisibility(View.VISIBLE);

        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(14));

        mRecyclerView.setLoadingData(new AutoRecyclerView.LoadingData() {
            @Override
            public void onLoadMore() {
                loadGankData();
            }
        });
        mRecyclerView.setAdapter(mAdapter = new WelfareAdapter(mData = new ArrayList<GanKData>(), this));
    }

    @Override
    public void addSubscription(Subscription subscription) {
        super.addCompositeSubscription(subscription);
    }

    @Override
    public int getPageNo() {
        return mPageNo;
    }

    @Override
    public String getType() {
        return TITLE;
    }

    @Override
    public void failure() {
        if (getPageNo() == 1){
            onInitAfter();
        }
    }

    @Override
    public void success(GanKResult<GanKData> data) {
        if (getPageNo() == 1){
            onInitAfter();
            mData.clear();
        }

        mData.addAll(data.getResults());
        mAdapter.notifyDataSetChanged();

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

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            }
        }
    }

    private void setIconDrawable(TextView view, IIcon icon) {
        view.setCompoundDrawablesWithIntrinsicBounds(new IconicsDrawable(this)
                        .icon(icon)
                        .color(ThemeUtils.getThemeColor(this, R.attr.colorPrimary)).sizePx(40),
                null, null, null);
        view.setCompoundDrawablePadding(15);
    }

    @OnClick(R.id.btn_left)
    void leftClick(){
    }

    @OnClick(R.id.btn_right)
    void rightClick(){
        if(popupWindow == null) {
            popupWindow = new PopupWindow(this, R.layout.view_type_popup) {
                @Override
                public void onBindView(View root) {
                    TextView tvAndroid = (TextView) root.findViewById(R.id.tv_android);
                    TextView tvIos = (TextView) root.findViewById(R.id.tv_ios);
                    TextView tvApp = (TextView) root.findViewById(R.id.tv_app);
                    TextView tvJs = (TextView) root.findViewById(R.id.tv_js);
                    TextView tvVedio = (TextView) root.findViewById(R.id.tv_vedio);
                    TextView tvRecom = (TextView) root.findViewById(R.id.tv_recom);
                    TextView tvData = (TextView) root.findViewById(R.id.tv_data);

                    setIconDrawable(tvAndroid, MaterialDesignIconic.Icon.gmi_android);
                    setIconDrawable(tvIos, MaterialDesignIconic.Icon.gmi_apple);
                    setIconDrawable(tvApp, MaterialDesignIconic.Icon.gmi_apps);
                    setIconDrawable(tvJs, MaterialDesignIconic.Icon.gmi_language_javascript);
                    setIconDrawable(tvVedio, MaterialDesignIconic.Icon.gmi_collection_video);
                    setIconDrawable(tvRecom, MaterialDesignIconic.Icon.gmi_collection_plus);
                    setIconDrawable(tvData, MaterialDesignIconic.Icon.gmi_attachment);

                    PopupItemClickListener clickListener = new PopupItemClickListener();
                    tvAndroid.setOnClickListener(clickListener);
                    tvIos.setOnClickListener(clickListener);
                    tvApp.setOnClickListener(clickListener);
                    tvJs.setOnClickListener(clickListener);
                    tvVedio.setOnClickListener(clickListener);
                    tvRecom.setOnClickListener(clickListener);
                    tvData.setOnClickListener(clickListener);
                }
            };
        }
        popupWindow.showAsDropDownCenter(btnRight);
    }

    class PopupItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, GankTypeActivity.class);
            switch (view.getId()){
                case R.id.tv_android:
                    intent.putExtra(Constants.UNDLE_GANK_TYPE, "Android");
                    break;
                case R.id.tv_ios:
                    intent.putExtra(Constants.UNDLE_GANK_TYPE, "iOS");
                    break;
                case R.id.tv_app:
                    intent.putExtra(Constants.UNDLE_GANK_TYPE, "App");
                    break;
                case R.id.tv_js:
                    intent.putExtra(Constants.UNDLE_GANK_TYPE, "前端");
                    break;
                case R.id.tv_data:
                    intent.putExtra(Constants.UNDLE_GANK_TYPE, "拓展资源");
                    break;
                case R.id.tv_recom:
                    intent.putExtra(Constants.UNDLE_GANK_TYPE, "瞎推荐");
                    break;
                case R.id.tv_vedio:
                    intent.putExtra(Constants.UNDLE_GANK_TYPE, "休息视频");
                    break;
            }
            startActivity(intent);

            if(popupWindow != null && popupWindow.isShowing()){
                popupWindow.dismiss();
            }
        }
    }

}
