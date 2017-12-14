package com.example.lenovo.editorimageandtext.activity;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;


public class BaseFragmentActivity extends FragmentActivity {
    public static final int TAKE_PICTURE = 0x000001;
    public static final int SELECTPHOTOFROMABLUM = 0x000002;
    public static final int SELECTPHOTOFROMABLUM_RESULTCODE = 0x000003;
    public static final int UPLOADPHOTOFROMABLUM = 0x000004;
    public static final int UPLOADPHOTOFROMABLUM_RESULTCODE = 0x000005;
    public static final int SELECTSTATION = 0x000006;
    public static final int VIDEOTYPE = 0x000007;
    public static final int GRID_PICTURE = 0x000008;
    public static boolean isTouch = true;
    public static boolean isLoad = false;


    protected Context mContext;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;
        Fresco.initialize(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setSimpleDraweeImage(String path, Activity act, SimpleDraweeView imageView) {
        Uri uri = Uri.parse("file://" + path);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setAutoRotateEnabled(true)
                .setResizeOptions(new ResizeOptions(getScreenWidth(act) / 4, getScreenWidth(act) / 4))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(imageView.getController())
                .setImageRequest(request)
                .build();
        imageView.setController(controller);
    }

    public void setSimpleDrawee(String path, SimpleDraweeView imageView) {
        DraweeController mDraweeController = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                //设置uri,加载本地的gif资源
                .setUri(Uri.parse(path))//设置uri
                .build();
        //设置Controller
        imageView.setController(mDraweeController);
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }
}
