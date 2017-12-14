package com.example.lenovo.editorimageandtext.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.editorimageandtext.R;
import com.example.lenovo.editorimageandtext.activity.BucketPicActivity;
import com.example.lenovo.editorimageandtext.bean.ImageBucket;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class ImageBucketAdapter extends BaseAdapter {
    final String TAG = getClass().getSimpleName();

    Activity act;
    List<ImageBucket> dataList;

    public ImageBucketAdapter(Activity act, List<ImageBucket> list) {
        this.act = act;
        dataList = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        int count = 0;
        if (dataList != null) {
            count = dataList.size();
        }
        return count;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    class Holder {
        private SimpleDraweeView iv;
        private ImageView selected;
        private TextView name;
        private TextView count;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        // TODO Auto-generated method stub
        Holder holder;
        if (arg1 == null) {
            holder = new Holder();
            arg1 = View.inflate(act, R.layout.beegree_item_image_bucket, null);
            holder.iv = (SimpleDraweeView) arg1.findViewById(R.id.image);
            holder.selected = (ImageView) arg1.findViewById(R.id.isselected);
            holder.name = (TextView) arg1.findViewById(R.id.name);
            holder.count = (TextView) arg1.findViewById(R.id.count);
            arg1.setTag(holder);
        } else {
            holder = (Holder) arg1.getTag();
        }
        ImageBucket item = dataList.get(arg0);
        holder.count.setText("" + item.count);
        holder.name.setText(item.bucketName);
        holder.selected.setVisibility(View.GONE);
        if (item.imageList != null && item.imageList.size() > 0) {
            String sourcePath = item.imageList.get(0).imagePath;
            holder.iv.setTag(sourcePath);
            ((BucketPicActivity) act).setSimpleDraweeImage(sourcePath, act, holder.iv);
        } else if (item.videoList != null && item.videoList.size() > 0) {
            String sourcePath = item.videoList.get(0).imagePath;
            holder.iv.setTag(sourcePath);
            ((BucketPicActivity) act).setSimpleDraweeImage(sourcePath, act, holder.iv);
        } else {
            holder.iv.setImageBitmap(null);
            Log.e(TAG, "no images in bucket " + item.bucketName);
        }
        return arg1;
    }

}
