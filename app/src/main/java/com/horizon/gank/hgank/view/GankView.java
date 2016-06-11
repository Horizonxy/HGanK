package com.horizon.gank.hgank.view;

import com.horizon.gank.hgank.model.bean.GanKData;
import com.horizon.gank.hgank.model.bean.GanKResult;

import rx.Subscription;

public interface GankView {

    int getPageNo();
    String getType();

    void failure();
    void success(GanKResult<GanKData> data);

    void addSubscription(Subscription subscription);
}
