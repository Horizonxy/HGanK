package com.horizon.gank.hgank.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.horizon.gank.hgank.R;
import com.horizon.gank.hgank.utils.SystemStatusManager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WelcomeActivity extends Activity {

    @Bind(R.id.iv_welcome)
    ImageView vWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemStatusManager.setTranslucentStatusRes(this, R.color.transparent);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

//        DiskCache diskCache = Application.getInstance().getImageLoader().getDiskCache();
//            File file = diskCache.get(PreUtils.getString(this, Constants.PRE_WEL_PIC_URL, ""));
//            if (file != null && file.exists()) {
//                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                vWelcome.setImageBitmap(bitmap);
//            } else {
//                vWelcome.setBackgroundResource(R.mipmap.welcome);
//            }


        Animation anim = AnimationUtils.loadAnimation(this, R.anim.splash_start);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        vWelcome.startAnimation(anim);
    }

}
