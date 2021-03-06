package com.horizon.gank.hgank.api;

import com.horizon.gank.hgank.model.bean.GanKData;
import com.horizon.gank.hgank.model.bean.GanKResult;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;


public interface ApiService {

    @GET("api/data/{type}/{pageSize}/{pageNo}")
    Observable<GanKResult<GanKData>> getGanKList(
            @Path("type") String type,
            @Path("pageSize") int pageSize,
            @Path("pageNo") int pageNo
    );

}
