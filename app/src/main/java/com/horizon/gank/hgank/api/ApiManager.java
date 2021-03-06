package com.horizon.gank.hgank.api;

import com.horizon.gank.hgank.Application;
import com.horizon.gank.hgank.model.bean.GanKData;
import com.horizon.gank.hgank.model.bean.GanKResult;

import rx.Observable;

public class ApiManager {

    public static Observable<GanKResult<GanKData>> getGanKList(String type, int pageNo){
        return Application.getInstance().apiService.getGanKList(type, 20, pageNo);
    }

}
