package com.example.lenovo.editorimageandtext.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.lenovo.editorimageandtext.R;
import com.example.lenovo.editorimageandtext.view.InitializeView;


/**
 * 作者：xyl on 2017/5/4 14:49
 */

public class VideoActivity extends BaseFragmentActivity implements InitializeView, TextureView.SurfaceTextureListener
        , View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private TextureView surfaceView;
    private ImageView image_back, image_button;
    private ProgressBar progress_bar;
    private String path;
    private MediaPlayer mediaPlayer;
    private int mSurfaceViewWidth, mSurfaceViewHeight;
    private Boolean isPlay = false;
    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beegree_layout_video);
        initViews();
        setDates();
    }

    @Override
    public void initViews() {
        surfaceView = (TextureView) findViewById(R.id.preview_video);
        image_back = (ImageView) findViewById(R.id.image_back);
        image_button = (ImageView) findViewById(R.id.image_button);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    @Override
    public void setDates() {
        image_back.setOnClickListener(this);
        image_button.setOnClickListener(this);
        surfaceView.setSurfaceTextureListener(this);
        surfaceView.setOnClickListener(this);
        path = getIntent().getStringExtra("path");

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
    }

    @Override
    protected void onStop() {
        if (mediaPlayer.isPlaying()) {
            isPlay = false;
            mediaPlayer.pause();
            progress_bar.setVisibility(View.GONE);
            image_button.setVisibility(View.GONE);
            progress_bar.setVisibility(View.VISIBLE);
        }
        super.onStop();
    }

    private void prepare(Surface surface) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置需要播放的视频
            mediaPlayer.setDataSource(path);
            // 把视频画面输出到Surface
            mediaPlayer.setSurface(surface);
            mediaPlayer.setLooping(false);
            mediaPlayer.prepareAsync();
            mediaPlayer.seekTo(0);
        } catch (Exception e) {
        }
    }

    private void stop() {
        mediaPlayer.stop();
        finish();
    }

    @Override
    public void onBackPressed() {
        stop();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (isPlay) {
            isPlay = false;
            progress_bar.setVisibility(View.GONE);
            image_button.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1, int arg2) {
        prepare(new Surface(arg0));
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.image_back) {
            stop();

        } else if (i == R.id.image_button) {
            if (!mediaPlayer.isPlaying()) {
                image_button.setVisibility(View.GONE);
                isPlay = true;
                mediaPlayer.start();
            }

        }
    }


    public void onVideoSizeChanged(MediaPlayer mMediaPlayer) {
        int wid = mMediaPlayer.getVideoWidth();
        int hig = mMediaPlayer.getVideoHeight();
        // 根据视频的属性调整其显示的模式

        if (wid > hig) {
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } else {
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mSurfaceViewWidth = dm.widthPixels;
        mSurfaceViewHeight = dm.heightPixels;
        if (wid > hig) {
            // 竖屏录制的视频，调节其上下的空余

            int w = mSurfaceViewHeight * wid / hig;
            int margin = (mSurfaceViewWidth - w) / 2;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(margin, 0, margin, 0);
            surfaceView.setLayoutParams(lp);
        } else {
            // 横屏录制的视频，调节其左右的空余

            int h = mSurfaceViewWidth * hig / wid;
            int margin = (mSurfaceViewHeight - h) / 2;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, margin, 0, margin);
            surfaceView.setLayoutParams(lp);
        }
    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        progress_bar.setVisibility(View.GONE);
        onVideoSizeChanged(mediaPlayer);
        if (!mediaPlayer.isPlaying()) {
            isPlay = true;
            mediaPlayer.start();
        }
    }
}
