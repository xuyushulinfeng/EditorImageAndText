package com.example.lenovo.editorimageandtext.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.editorimageandtext.R;
import com.example.lenovo.editorimageandtext.activity.BaseFragmentActivity;
import com.example.lenovo.editorimageandtext.activity.ImageGridActivity;
import com.example.lenovo.editorimageandtext.app.Bimp;
import com.example.lenovo.editorimageandtext.bean.EditPostBean;
import com.example.lenovo.editorimageandtext.bean.ImageItem;
import com.example.lenovo.editorimageandtext.bean.VideoItem;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageGridAdapter extends BaseAdapter {

    public static final int SELECTPHOTOFROMABLUM_RESULTCODE = 0x000003;
    private TextCallback textcallback = null;
    final String TAG = getClass().getSimpleName();
    Activity act;
    public List<ImageItem> dataList;
    public List<VideoItem> videoList;
    // 选中图片的map集合
    public Map<String, EditPostBean> map = new HashMap<String, EditPostBean>();
    private Handler mHandler;
    private int selectTotal = 0;
    private int type;
    private Calendar c = Calendar.getInstance();

    public void setSelectTotal(int selectTotal) {
        this.selectTotal = selectTotal;
    }

    public static interface TextCallback {
        public void onListen(int count);
    }

    public void setTextCallback(TextCallback listener) {
        textcallback = listener;
    }

    public ImageGridAdapter(Activity act, List<ImageItem> listOne, List<VideoItem> listTwo, Handler mHandler, int type) {
        this.act = act;
        dataList = listOne;
        videoList = listTwo;
        this.mHandler = mHandler;
        this.type = type;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (dataList != null) {
            count = dataList.size();
        } else if (videoList != null) {
            count = videoList.size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    class Holder {
        private SimpleDraweeView iv;
        private ImageView selected;
        private TextView text;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;

        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(act, R.layout.beegree_item_image_grid, null);
            holder.iv = (SimpleDraweeView) convertView.findViewById(R.id.image);
            holder.selected = (ImageView) convertView
                    .findViewById(R.id.isselected);
            holder.text = (TextView) convertView
                    .findViewById(R.id.item_image_grid_text);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (type == BaseFragmentActivity.VIDEOTYPE) {
            final VideoItem item = videoList.get(position);
            holder.iv.setTag(item.getImagePath());
            ((ImageGridActivity) act).setSimpleDraweeImage(item.getImagePath(), act, holder.iv);
            if (item.isSelected) {
                holder.selected.setImageResource(R.drawable.icon_data_select);
                holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
            } else {
                holder.selected.setImageDrawable(null);
                holder.text.setBackgroundColor(0x00000000);
            }
            holder.iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    c.setTime(new Date(item.getDuration()));
                    if (c.get(Calendar.SECOND) > 30) {
                        final AlertDialog myDialog = new AlertDialog.Builder(
                                act).create();
                        myDialog.show();
                        Window window = myDialog.getWindow();
                        window.setContentView(R.layout.beegree_layout_notice);
                        TextView text_sure = (TextView) myDialog.getWindow()
                                .findViewById(R.id.text_sure);
                        text_sure.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();
                            }
                        });
                    } else {
                        for (int i = 0; i < videoList.size(); i++) {
                            if (videoList.get(i).isSelected) {
                                videoList.get(i).isSelected = !videoList.get(i).isSelected;
                                holder.selected.setImageDrawable(null);
                                holder.text.setBackgroundColor(0x00000000);
                                selectTotal = 0;
                                if (textcallback != null)
                                    textcallback.onListen(selectTotal);
                                map.clear();
                            }
                        }
                        notifyDataSetChanged();
                        String path = videoList.get(position).getImagePath();
                        Bitmap bitmap = videoList.get(position).getBitmap();

                        if ((Bimp.drr.size() + selectTotal) < 20) {
                            item.setSelected(!item.isSelected);
                            if (item.isSelected) {
                                holder.selected
                                        .setImageResource(R.drawable.icon_data_select);
                                holder.text
                                        .setBackgroundResource(R.drawable.bgd_relatly_line);
                                selectTotal++;
                                if (textcallback != null)
                                    textcallback.onListen(selectTotal);
                                EditPostBean editPostBean = new EditPostBean();
                                editPostBean.setDuration(item.getDuration());
                                editPostBean.setBitmap(item.getBitmap());
                                editPostBean.setPath(item.getImagePath());
                                map.put(path, editPostBean);

                            } else if (!item.isSelected) {
                                holder.selected.setImageDrawable(null);
                                holder.text.setBackgroundColor(0x00000000);
                                selectTotal--;
                                if (textcallback != null)
                                    textcallback.onListen(selectTotal);
                                map.remove(path);
                            }
                        } else if ((Bimp.drr.size() + selectTotal) >= 20) {
                            if (item.isSelected == true) {
                                item.setSelected(!item.isSelected);
                                holder.selected.setImageDrawable(null);
                                selectTotal--;
                                map.remove(path);

                            } else {
                                Message message = Message.obtain(mHandler, 0);
                                message.sendToTarget();
                            }
                        }
                    }

                }

            });
        } else {
            final ImageItem item = dataList.get(position);
            holder.iv.setTag(item.getImagePath());
            ((ImageGridActivity) act).setSimpleDraweeImage(item.getImagePath(), act, holder.iv);
            if (item.isSelected) {
                holder.selected.setImageResource(R.drawable.icon_data_select);
                holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
            } else {
                holder.selected.setImageDrawable(null);
                holder.text.setBackgroundColor(0x00000000);
            }
            holder.iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (type == SELECTPHOTOFROMABLUM_RESULTCODE) {
                        for (int i = 0; i < dataList.size(); i++) {
                            if (dataList.get(i).isSelected) {
                                dataList.get(i).isSelected = !dataList.get(i).isSelected;
                                holder.selected.setImageDrawable(null);
                                holder.text.setBackgroundColor(0x00000000);
                                selectTotal = 0;
                                if (textcallback != null)
                                    textcallback.onListen(selectTotal);
                                map.clear();
                            }
                        }
                        notifyDataSetChanged();
                    }
                    String path = dataList.get(position).getImagePath();
                    Bitmap bitmap = dataList.get(position).getBitmap();

                    if ((Bimp.drr.size() + selectTotal) < 20) {
                        item.setSelected(!item.isSelected);
                        if (item.isSelected) {
                            holder.selected
                                    .setImageResource(R.drawable.icon_data_select);
                            holder.text
                                    .setBackgroundResource(R.drawable.bgd_relatly_line);
                            selectTotal++;
                            if (textcallback != null)
                                textcallback.onListen(selectTotal);
                            EditPostBean editPostBean = new EditPostBean();
                            editPostBean.setBitmap(item.getBitmap());
                            editPostBean.setPath(item.getImagePath());
                            map.put(path, editPostBean);

                        } else if (!item.isSelected) {
                            holder.selected.setImageDrawable(null);
                            holder.text.setBackgroundColor(0x00000000);
                            selectTotal--;
                            if (textcallback != null)
                                textcallback.onListen(selectTotal);
                            map.remove(path);
                        }
                    } else if ((Bimp.drr.size() + selectTotal) >= 20) {
                        if (item.isSelected == true) {
                            item.setSelected(!item.isSelected);
                            holder.selected.setImageDrawable(null);
                            selectTotal--;
                            map.remove(path);

                        } else {
                            Message message = Message.obtain(mHandler, 0);
                            message.sendToTarget();
                        }
                    }
                }

            });
        }

        return convertView;
    }

}
