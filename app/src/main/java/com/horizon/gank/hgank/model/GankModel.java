package com.horizon.gank.hgank.model;

import com.horizon.gank.hgank.api.ApiManager;
import com.horizon.gank.hgank.model.bean.GanKData;
import com.horizon.gank.hgank.model.bean.GanKResult;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GankModel {

    public Subscription getGanKList(String type, int pageNo, Subscriber<GanKResult<GanKData>> subscriber){
        return ApiManager.getGanKList(type, pageNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
