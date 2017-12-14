package com.example.lenovo.editorimageandtext.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.lenovo.editorimageandtext.R;
import com.example.lenovo.editorimageandtext.adapter.ImageBucketAdapter;
import com.example.lenovo.editorimageandtext.app.AlbumHelper;
import com.example.lenovo.editorimageandtext.app.Bimp;
import com.example.lenovo.editorimageandtext.bean.ImageBucket;
import com.example.lenovo.editorimageandtext.utils.ImagePathUtil;

import java.util.ArrayList;


/**
 * 相册列表界面
 *
 * @author xyl
 */
public class BucketPicActivity extends BaseFragmentActivity {
    // 用来装载数据源的列表
    ArrayList<ImageBucket> dataList;
    ImageView photo_delete, photo_choose;
    GridView gridView;
    ImageBucketAdapter adapter;// 自定义的适配器
    AlbumHelper helper;
    public static final String EXTRA_IMAGE_LIST = "imagelist";
    // public static Bitmap bimap;
    private int type;
    private String grid = "";
    private static final int TAG_PERMISSION = 1023;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.beegree_activity_image_bucket);
        dynamicPermission();
        super.onCreate(savedInstanceState);
    }

    public void dynamicPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        TAG_PERMISSION);
            }
        } else {
            getContact();
        }
    }

    public void getContact() {
        initView();
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
        type = getIntent().getIntExtra("type", 0);
        grid = getIntent().getStringExtra("grid");
        if (Bimp.drr != null) {
            try {
                if (type != GRID_PICTURE)
                    Bimp.drr.clear();
            } catch (Exception e) {
            }
        }
        if (type == VIDEOTYPE) {
            initVideoData();
        } else {
            initData();
        }
        initView();
        photo_choose.setVisibility(View.GONE);
    }

    /**
     * 初始图片化数据
     */
    public void initData() {
        dataList = ImagePathUtil.getImageAlbumList(getApplicationContext());
    }

    /**
     * 初始视频化数据
     */
    public void initVideoData() {
        dataList = ImagePathUtil.getVideoAlbumList(getApplicationContext());
    }

    /**
     * 初始化view视图
     */
    protected void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
        photo_delete = (ImageView) findViewById(R.id.photo_delete);
        photo_choose = (ImageView) findViewById(R.id.photo_choose);
        adapter = new ImageBucketAdapter(BucketPicActivity.this, dataList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {

                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view,
                                                                    int position, long id) {
/**
 * 通知适配器，绑定的数据发生了改变，应当刷新视图
 */
                                                Intent intent = new Intent(BucketPicActivity.this,
                                                        ImageGridActivity.class);
                                                if (type == VIDEOTYPE) {
                                                    intent.putExtra(BucketPicActivity.EXTRA_IMAGE_LIST,
                                                            dataList.get(position));
                                                } else {
                                                    intent.putExtra(BucketPicActivity.EXTRA_IMAGE_LIST,
                                                            dataList.get(position));
                                                }
                                                intent.putExtra("type", type);
                                                startActivityForResult(intent, SELECTPHOTOFROMABLUM);
                                            }
                                        }

        );
        photo_delete.setOnClickListener(new View.OnClickListener()

                                        {
                                            @Override
                                            public void onClick(View view) {
                                                setResult(SELECTPHOTOFROMABLUM_RESULTCODE, null);
                                                finish();
                                                overridePendingTransition(0,
                                                        R.anim.push_bottom_out);
                                            }
                                        }

        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECTPHOTOFROMABLUM
                && resultCode == TAKE_PICTURE) {
            if (grid != null && grid.equals("grid")) {
                resultCode = GRID_PICTURE;
                setResult(resultCode, data);
            } else {
                setResult(resultCode, data);
            }
            finish();
            overridePendingTransition(0,
                    R.anim.push_bottom_out);
        }
        if (requestCode == SELECTPHOTOFROMABLUM
                && resultCode == SELECTPHOTOFROMABLUM) {
            setResult(resultCode, data);
            finish();
            overridePendingTransition(0,
                    R.anim.push_bottom_out);
        }
        if (requestCode == SELECTPHOTOFROMABLUM
                && resultCode == SELECTPHOTOFROMABLUM_RESULTCODE) {
            setResult(resultCode, data);
            finish();
            overridePendingTransition(0,
                    R.anim.push_bottom_out);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case TAG_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContact();
                } else {
                }
                return;
            }
        }
    }
}

