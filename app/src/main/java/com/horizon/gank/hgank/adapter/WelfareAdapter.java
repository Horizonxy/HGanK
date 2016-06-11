package com.horizon.gank.hgank.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.horizon.gank.hgank.Application;
import com.horizon.gank.hgank.Constants;
import com.horizon.gank.hgank.R;
import com.horizon.gank.hgank.activity.PictureDetailActivity;
import com.horizon.gank.hgank.model.bean.GanKData;
import com.horizon.gank.hgank.utils.PreUtils;
import com.horizon.gank.hgank.utils.SmallPicInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

public class WelfareAdapter extends RecyclerView.Adapter<WelfareAdapter.WelfaceHolder> {

    private List<GanKData> mList;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private Context mCxt;
    private LayoutInflater mInflater;

    public WelfareAdapter(List<GanKData> list, Context cxt) {
        this.mList = list;
        this.mCxt = cxt;
        this.mInflater = LayoutInflater.from(cxt);
        mImageLoader = Application.getInstance().getImageLoader();
        mOptions = Application.getInstance().getDefaultOptions();
    }

    @Override
    public WelfaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_welfare, null);

        return new WelfaceHolder(view);
    }

    @Override
    public void onBindViewHolder(final WelfaceHolder holder, int position) {

        GanKData data = mList.get(position);
        if (!TextUtils.isEmpty(data.getUrl())) {
//            DiskCache diskCache = Application.getInstance().getImageLoader().getDiskCache();
//            File file = diskCache.get(data.getUrl());
//            if (file != null && file.exists()) {
//                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                holder.mItemIvWelfare.setImageBitmap(bitmap);
//            } else {
                mImageLoader.displayImage(data.getUrl(), holder.mItemIvWelfare, mOptions, new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        int width = loadedImage.getWidth();
                        int height = loadedImage.getHeight();
                        float scale = Application.getInstance().SCREENWIDTH/2 * 1f / width;

                        Matrix matrix = new Matrix();
                        matrix.setScale(scale,scale);
                        Bitmap bitmap = Bitmap.createBitmap(loadedImage, 0, 0, width, height, matrix, false);
                        holder.mItemIvWelfare.setImageBitmap(bitmap);
                    }
                });
//            }

            PreUtils.putString(mCxt, Constants.PRE_WEL_PIC_URL, data.getUrl());

            String desc = data.getDesc();
            String user = data.getWho();
            holder.mItemTvTime.setText(TextUtils.isEmpty(desc) ? "" : desc);
            holder.mItemTvUser.setText(TextUtils.isEmpty(user) ? "" : "-- " + user);

            holder.mItemIvWelfare.setOnClickListener(new ImageClickListener(holder, data));
        }
    }

    class ImageClickListener implements View.OnClickListener {

        private WelfaceHolder holder;
        private GanKData data;

        public ImageClickListener(WelfaceHolder holder, GanKData data) {
            this.holder = holder;
            this.data = data;
        }

        @Override
        public void onClick(View view) {
            holder.mItemIvWelfare.setDrawingCacheEnabled(true);
            Bitmap bitmap = holder.mItemIvWelfare.getDrawingCache();

            int[] screenLocation = new int[2];
            holder.mItemIvWelfare.getLocationOnScreen(screenLocation);

            SmallPicInfo info = new SmallPicInfo(data, screenLocation[0], screenLocation[1], holder.mItemIvWelfare.getWidth(), holder.mItemIvWelfare.getHeight(), 0, Bitmap.createBitmap(bitmap));

            Intent intent = new Intent(mCxt, PictureDetailActivity.class);
            intent.putExtra(Constants.BUNDLE_PIC_INFOS, info);
            mCxt.startActivity(intent);
            ((Activity) mCxt).overridePendingTransition(0, 0);

            holder.mItemIvWelfare.setDrawingCacheEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class WelfaceHolder extends RecyclerView.ViewHolder {

        ImageView mItemIvWelfare;
        TextView mItemTvTime;
        TextView mItemTvUser;

        public WelfaceHolder(View itemView) {
            super(itemView);
            mItemIvWelfare = (ImageView) itemView.findViewById(R.id.item_iv_welfare);
            mItemTvTime = (TextView) itemView.findViewById(R.id.item_tv_time);
            mItemTvUser = (TextView) itemView.findViewById(R.id.item_tv_user);
        }
    }
}
