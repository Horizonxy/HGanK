package com.horizon.gank.hgank.presenter;

import com.horizon.gank.hgank.model.GankModel;
import com.horizon.gank.hgank.model.bean.GanKData;
import com.horizon.gank.hgank.model.bean.GanKResult;
import com.horizon.gank.hgank.utils.SimpleSubscriber;
import com.horizon.gank.hgank.view.GankView;

import rx.Subscription;

public class GankPresenter {

    private GankView vGank;
    private GankModel mGank;

    public GankPresenter(GankModel mGank, GankView vGank) {
        this.mGank = mGank;
        this.vGank = vGank;
    }

    public void getGankList(){
        Subscription subscription = mGank.getGanKList(vGank.getType(), vGank.getPageNo(), new SimpleSubscriber<GanKResult<GanKData>>(){

            @Override
            public void onError(Throwable e) {
                vGank.failure();
            }

            @Override
            public void onNext(GanKResult<GanKData> obj) {
                if (obj.isError()) {
                    vGank.failure();
                } else{
                    vGank.success(obj);
                }
            }
        });

        vGank.addSubscription(subscription);
    }

}
