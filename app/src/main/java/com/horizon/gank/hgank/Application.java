package com.horizon.gank.hgank;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

import com.horizon.gank.hgank.api.ApiService;
import com.horizon.gank.hgank.utils.FileUtils;
import com.horizon.gank.hgank.utils.RetrofitUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public class Application extends android.app.Application {

    private static Application application;
    private ImageLoader imageLoader;
    private List<Activity> atyList;
    private Resources res;

    public int SCREENWIDTH, SCREENHEIGHT;

    public ApiService apiService;

    private DisplayImageOptions defaultOptions;

    @Override
    public void onCreate() {
        super.onCreate();

        atyList = new ArrayList<Activity>();

        application = this;

        res = this.getResources();

        apiService = new Retrofit.Builder()
                .client(RetrofitUtil.createOkHttpClient())
                .baseUrl(Constants.END_POIND)
                .addConverterFactory(GsonConverterFactory.create(RetrofitUtil.createGson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build().create(ApiService.class);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        SCREENHEIGHT = displayMetrics.heightPixels;
        SCREENWIDTH = displayMetrics.widthPixels;
    }

    public static Application getInstance(){
        if (application == null){
            application = new Application();
        }
        return application;
    }

    public void addAty(Activity aty){
        atyList.add(aty);
    }

    public void removeAty(Activity aty){
        atyList.remove(aty);
    }

    public void exit(){
        for (Activity aty : atyList) {
            aty.finish();
        }
        System.exit(0);
    }

    public DisplayImageOptions getDefaultOptions() {
        if(defaultOptions == null){
            defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new FadeInBitmapDisplayer(2000))
                    .build();
        }
        return defaultOptions;
    }

    public List<Activity> getAtyList() {
        return atyList;
    }

    public ImageLoader getImageLoader() {
        if(imageLoader == null){
            imageLoader = ImageLoader.getInstance();
            final String IMG_CACHE_PATH = FileUtils.getEnvPath(application, true, Constants.IMG_CACHE_DIR);
            File imgFile = new File(IMG_CACHE_PATH);
            if(!imgFile.exists()){
                imgFile.mkdirs();
            }
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(application)
                    .threadPoolSize(20)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .discCache(new UnlimitedDiskCache(imgFile))
                    .writeDebugLogs()
                    .build();
            imageLoader.init(config);
        }
        return imageLoader;
    }

    public Resources getRes(){
        return  res;
    }

}
