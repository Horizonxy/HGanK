package com.horizon.gank.hgank.widget;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class CreateFooterView {

	/** 实例化view */
	public abstract View inflaterView(Context cxt, int resId);
	
	/** 初始化view */
	public abstract void init();
	
	/** 正在加载 */
	public abstract void loading();
	
	/** 单次加载完成 */
	public abstract void complete();
	
	/** 加载失败  */
	public abstract void failure();
	
	/** 所有加载完成 */
	public abstract void finish();
	
	/** 设置view点击监听 */
	public void setOnClickListener(OnClickListener listener){};
	
	/** 获取view */
	public abstract View getView();
}
