package com.example.lenovo.editorimageandtext.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.editorimageandtext.R;
import com.example.lenovo.editorimageandtext.adapter.ImageGridAdapter;
import com.example.lenovo.editorimageandtext.app.AlbumHelper;
import com.example.lenovo.editorimageandtext.app.Bimp;
import com.example.lenovo.editorimageandtext.bean.EditPostBean;
import com.example.lenovo.editorimageandtext.bean.ImageBucket;
import com.example.lenovo.editorimageandtext.bean.ImageItem;
import com.example.lenovo.editorimageandtext.bean.VideoItem;
import com.example.lenovo.editorimageandtext.utils.FileSizeUtil;
import com.example.lenovo.editorimageandtext.utils.ImageCompressUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class ImageGridActivity extends BaseFragmentActivity {
    public static final String EXTRA_IMAGE_LIST = "imagelist";
    public static final int UPLOADSUCCESS = 0x10;
    public static final int UPLOADSUCCESSANDDISMISSDIALOG = 0x11;

    // ArrayList<Entity> dataList;
    List<ImageItem> dataList;
    List<VideoItem> videoList;
    private int type;
    GridView gridView;
    ImageGridAdapter adapter;
    AlbumHelper helper;
    ImageView photo_delete, photo_choose;
    private Dialog dialog;
    private ProgressBar bar;
    private TextView tip;
    private ArrayList<String> picurls = new ArrayList<String>();

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(ImageGridActivity.this, "最多只能选20张相片",
                            Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };
    private StringBuffer bytefileStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.beegree_activity_image_grid);
        initView();
        type = getIntent().getIntExtra("type", 0);
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
        ImageBucket imageBucket = (ImageBucket) getIntent().getSerializableExtra(
                EXTRA_IMAGE_LIST);
        if (type == VIDEOTYPE) {
            videoList = imageBucket.videoList;
        } else {
            dataList = imageBucket.imageList;
        }
        initView();
        photo_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        photo_choose.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (type == GRID_PICTURE) {
                    if (adapter.map.size() + Bimp.drr.size() > 9) {
                        Toast.makeText(mContext, "最多只能选取9张", Toast.LENGTH_SHORT).show();
                    } else {
                        if (adapter.map.size() > 0) {
                            setdrr();
                        }
                    }
                } else {
                    if (adapter.map.size() > 0) {
                        setdrr();
                    }
                }
            }

        });
    }

    protected void setdrr() {
        for (Entry<String, EditPostBean> entry : adapter.map.entrySet()) {
            EditPostBean editPostBean = new EditPostBean();
            editPostBean.setBitmap(entry.getValue().getBitmap());
            editPostBean.setPath(entry.getKey());
            editPostBean.setDuration(entry.getValue().getDuration());
            Bimp.drr.add(editPostBean);
        }
        Intent intent = new Intent();
        setResult(TAKE_PICTURE, intent);
        finish();
    }

    protected void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
        photo_delete = (ImageView) findViewById(R.id.photo_delete);
        photo_choose = (ImageView) findViewById(R.id.photo_choose);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        if (type == VIDEOTYPE) {
            adapter = new ImageGridAdapter(ImageGridActivity.this, null, videoList,
                    mHandler, type);
        } else {
            adapter = new ImageGridAdapter(ImageGridActivity.this, dataList, null,
                    mHandler, type);
        }
        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("数据上传");
        View view = View.inflate(this, R.layout.customerdialogcheckroll, null);
        bar = (ProgressBar) view.findViewById(R.id.customerdialog_pb);
        tip = (TextView) view.findViewById(R.id.customerdialog_tv);

        dialog.setContentView(view);
        gridView.setAdapter(adapter);
    }

    //

    // 上传结束后清除数据和选中状态
    private void clearSelect() {
        for (Entry<String, EditPostBean> entry : adapter.map.entrySet()) {
            for (ImageItem item : adapter.dataList) {
                if (item.imagePath.equals(entry.getKey())) {
                    item.isSelected = false;
                    break;
                }
            }
        }
        adapter.setSelectTotal(0);
        adapter.map.clear();
        Bimp.drr.clear();
    }

    private String getfile(int position) {

        String filename = picurls.get(position).substring(
                picurls.get(position).lastIndexOf("/") + 1);
        return filename;

    }

    private String getbytefileStream(int position) throws IOException {
        bytefileStream = new StringBuffer();
        boolean compressed = false;
        if (FileSizeUtil.getFileOrFilesSize(picurls.get(position),
                FileSizeUtil.SIZETYPE_MB) > 1) {
            ImageCompressUtils utils = new ImageCompressUtils();
            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED)
                utils.setFilename(getFilesDir() + "/imagecompress"
                        + getfile(position));
            utils.compressImage(picurls.get(position), 512, false);
            compressed = true;
        }
        InputStream is;
        if (compressed) {
            is = new FileInputStream(getFilesDir() + "/imagecompress"
                    + getfile(position));
        } else {
            is = new FileInputStream(picurls.get(position));
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = 0;
        byte[] b = new byte[4096];
        while ((i = is.read(b)) != -1) {
            baos.write(b, 0, i);
        }

        b = Base64.encode(baos.toByteArray(), Base64.DEFAULT);
        Log.i(this.getClass().getSimpleName(), "" + b.length);
        String content = new String(b);
        bytefileStream.append(content);
        content = null;
        is.close();
        is = null;
        baos.close();
        baos = null;

        return bytefileStream.toString();
    }

    private int sendCount = 0;

    @Override
    protected void onPause() {
        Log.i("ImageGridActivity", "onpause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("ImageGridActivity", "onstop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i("ImageGridActivity", "ondestroy");
        super.onDestroy();
    }

}
