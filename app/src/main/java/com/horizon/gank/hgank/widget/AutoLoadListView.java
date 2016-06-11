package com.horizon.gank.hgank.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.horizon.gank.hgank.R;


/**
 * 滑动到底部自动加载更多
 * @author 蒋先明
 * @date 2016年4月8日
 */
public class AutoLoadListView extends ListView {

	/** 自动加载监听 */
	private OnAutoLoadListener mAutoLoadListener;
	/** 滚动监听 */
	private OnAutoScrollListener mAutoScrollListener;
	/** 当前加载状态 */
	public State mState;
	/** 底部view */
	private CreateFooterView mFooterView;
	
	public AutoLoadListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AutoLoadListView(Context context) {
		this(context, null);
	}
	
	/**
	 * 底部view 状态
	 * @author 蒋先明
	 * @date 2016年4月8日
	 */
	public static enum State {
		/** 正在加载 */
		LOADING,
		/** 加载失败 */
		FAILURE,
		/** 完成加载，没有更多 */
		FINISH,
		/** 单次加载完成 */
		COMPLETE
	}

	/**
	 * 初始化
	 * @author 蒋先明
	 * @date 2016年4月13日
	 * @param context
	 */
	private void init(Context context) {
		//1、实例化footer；
		mFooterView = new FooterView(context, R.layout.view_autoload_footer);
		
		mFooterView.setOnClickListener(new ClickListener());
		
		addFooterView(mFooterView.getView());
		
		//2、 listview添加滚动监听；
		this.setOnScrollListener(new ScrollListener());
	}
	
	/**
	 * 自动加载操作
	 * @author 蒋先明
	 * @date 2016年4月13日
	 */
	public interface OnAutoLoadListener {
		public void onLoading(); 
	}
	
	/**
	 * 设置底部view
	 * @author 蒋先明
	 * @date 2016年4月13日
	 * @param footView
	 */
	public void setFootView(CreateFooterView footView){
		if(footView !=null){
			removeFooterView(mFooterView.getView());
			
			this.mFooterView = footView;
			addFooterView(mFooterView.getView());
		}
	}
	
	/**
	 * dp 转 px
	 * @param dpVal
	 */
	protected int dp2px(int dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, getResources().getDisplayMetrics());
	}
	
	/**
	 * 滚动监听
	 * @author 蒋先明
	 * @date 2016年4月8日
	 */
	class ScrollListener implements OnScrollListener {

		private boolean mLastItemVisible;
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && null != mAutoLoadListener && mLastItemVisible) {
				setState(State.LOADING);
				mAutoLoadListener.onLoading();
			}
			if(null != mAutoScrollListener){
				mAutoScrollListener.onScrollStateChanged(view, scrollState);
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (null != mAutoLoadListener) {
				mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1) && mState != State.FINISH && mState != State.FAILURE && mState != State.LOADING;
			} else {
				mLastItemVisible = false;
			}
			if(null != mAutoScrollListener){
				mAutoScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		}
		
	}
	
	/**
	 * 加载失败 点击操作
	 * @author 蒋先明
	 * @date 2016年4月13日
	 */
	class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if(mState != State.FAILURE){
				return;
			}
			if(null != mFooterView){
				mFooterView.loading();
			}
			if(null != mAutoLoadListener){
				mAutoLoadListener.onLoading();
			}
		}
	}
	
	/**
	 * 加载失败
	 * @author 蒋先明
	 * @date 2016年4月13日
	 */
	public void onFailuer(){
		setState(State.FAILURE);
	}
	
	/**
	 * 所有加载完成  暂无更多数据
	 * @author 蒋先明
	 * @date 2016年4月13日
	 */
	public void onFinish(){
		setState(State.FINISH);
	}
	
	/**
	 * 单次加载完成
	 * @author 蒋先明
	 * @date 2016年4月13日
	 */
	public void onComplete(){
		setState(State.COMPLETE);
	}
	
	/**
	 * 设置底部view显示状态
	 * @author 蒋先明
	 * @date 2016年4月8日
	 * @param state
	 */
	private void setState(State state){
		mState = state;
		if(mFooterView == null){
			return;
		}
		switch (state) {
		case COMPLETE:
			mFooterView.complete();
			break;
		case FINISH:
			mFooterView.finish();
			break;
		case LOADING:
			mFooterView.loading();
			break;
		case FAILURE:
			mFooterView.failure();
			break;
		default:
			break;
		}
	}

	/**
	 * 设置状态回调
	 * @author 蒋先明
	 * @date 2016年4月8日
	 * @param mAutoLoadListener
	 */
	public void setAutoLoadListener(OnAutoLoadListener mAutoLoadListener) {
		this.mAutoLoadListener = mAutoLoadListener;
	}

	public void setAutoScrollListener(OnAutoScrollListener mAutoScrollListener){
		this.mAutoScrollListener = mAutoScrollListener;
	}

	/**
	 * 滑动监听
	 * @author 蒋先明
	 * @date 2016年4月13日
	 */
	public interface OnAutoScrollListener {
        public void onScrollStateChanged(AbsListView view, int scrollState);
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
	}

	public CreateFooterView getFooterView(){
		return mFooterView;
	}

	/**
	 * 创建footer view
	 * @author 蒋先明
	 * @date 2016年4月13日
	 */
	class FooterView extends CreateFooterView {

		private LinearLayout view;
		private MonIndicator indicator;
		private TextView tvFooter;
		private int height;
		
		public FooterView(Context cxt, int resId) {
			inflaterView(cxt, resId);
			init();
		}
		
		@Override
		public void init() {
			view.setPadding(0, -height, 0, 0);
			view.setVisibility(View.GONE);
		}

		@Override
		public void loading() {
			view.setPadding(0, 0, 0, 0);
			view.setVisibility(View.VISIBLE);
			tvFooter.setText("正在加载...");
			view.setClickable(false);
			indicator.setVisibility(View.VISIBLE);
			tvFooter.setVisibility(View.GONE);
		}

		@Override
		public void complete() {
			view.setPadding(0, -height, 0, 0);
			view.setVisibility(View.GONE);
			tvFooter.setText("");
			view.setClickable(false);
		}

		@Override
		public void failure() {
			view.setPadding(0, 0, 0, 0);
			view.setVisibility(View.VISIBLE);
			tvFooter.setText("加载失败，点击重试");
			indicator.setVisibility(View.GONE);
			tvFooter.setVisibility(View.VISIBLE);
			view.setClickable(true);
		}

		@Override
		public void finish() {
			view.setPadding(0, 0, 0, 0);
			view.setVisibility(View.VISIBLE);
			tvFooter.setText("没有更多数据");
			view.setClickable(false);
			indicator.setVisibility(View.GONE);
			tvFooter.setVisibility(View.VISIBLE);
		}

		@Override
		public View inflaterView(Context cxt, int resId) {
			view = (LinearLayout) LayoutInflater.from(cxt).inflate(resId, null);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, height = dp2px(40));
			view.setLayoutParams(lp);
			
			indicator = (MonIndicator) view.findViewById(R.id.indicator);
			tvFooter = (TextView) view.findViewById(R.id.tv_footer);
			
			return view;
		}
		
		@Override
		public void setOnClickListener(OnClickListener listener){
			view.setOnClickListener(listener);
		}

		@Override
		public LinearLayout getView() {
			return view;
		}
		
	}
}
