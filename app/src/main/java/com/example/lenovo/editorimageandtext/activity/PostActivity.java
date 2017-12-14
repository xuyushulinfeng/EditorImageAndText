package com.example.lenovo.editorimageandtext.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.editorimageandtext.R;
import com.example.lenovo.editorimageandtext.app.Bimp;
import com.example.lenovo.editorimageandtext.bean.MyBean;
import com.example.lenovo.editorimageandtext.helper.BaseSpringSystem;
import com.example.lenovo.editorimageandtext.helper.ExpandableViewHoldersUtil;
import com.example.lenovo.editorimageandtext.helper.ItemTouchHelperAdapter;
import com.example.lenovo.editorimageandtext.helper.ItemTouchHelperViewHolder;
import com.example.lenovo.editorimageandtext.helper.KeyBoardShowListener;
import com.example.lenovo.editorimageandtext.helper.OnStartDragListener;
import com.example.lenovo.editorimageandtext.helper.RecycleViewDivider;
import com.example.lenovo.editorimageandtext.helper.SimpleItemTouchHelperCallback;
import com.example.lenovo.editorimageandtext.helper.SimpleSpringListener;
import com.example.lenovo.editorimageandtext.helper.Spring;
import com.example.lenovo.editorimageandtext.helper.SpringSystem;
import com.example.lenovo.editorimageandtext.helper.SpringUtil;
import com.example.lenovo.editorimageandtext.utils.DensityUtil;
import com.example.lenovo.editorimageandtext.utils.ImageCompressUtils;
import com.example.lenovo.editorimageandtext.view.InitializeView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者：xyl on 2017/3/1 11:52
 */

public class PostActivity extends BaseFragmentActivity implements InitializeView, View.OnClickListener, OnStartDragListener {

    private RecyclerView post_recycler;
    private TextView text_quit, text_save, text_send;
    private MyAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private ItemTouchHelper mItemTouchHelper;
    private List<MyBean> mItems = new ArrayList<>();

    private View headView, footView;
    private ImageView image_cover_button, image_set_cover, image_delete_cover;
    private SimpleDraweeView image_cover;
    private EditText edit_title, edit_title_place;
    private RelativeLayout post_header;

    private int pp = -1;
    private int addposition = -1;
    private int touchAnnotation = -1;
    private int touchPost = -1;
    private HashMap<Integer, MyAdapter.ViewHolder> holderMap = new HashMap<>();
    private List<MyAdapter.ViewHolder> closeList = new ArrayList<>();
    private Boolean keyBoardIsShow = false;
    private Boolean isMoveItem = false;
    private Boolean isCoverEditotTouch = false;
    private String coverPath = "";
    private Boolean in = true;
    private ImageCompressUtils imageCompress = new ImageCompressUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beegree_activity_post);
        //初始化Fresco
        Fresco.initialize(this);
        //防止软键盘将布局顶上去
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initViews();
        setDates();
    }

    @Override
    public void initViews() {
        post_recycler = (RecyclerView) findViewById(R.id.post_recycler);
        text_quit = (TextView) findViewById(R.id.text_quit);
        text_save = (TextView) findViewById(R.id.text_save);
        text_send = (TextView) findViewById(R.id.text_send);
        headView = LayoutInflater.from(this).inflate(R.layout.beegree_post_head_view, null);
        footView = LayoutInflater.from(this).inflate(R.layout.beegree_post_foot_view, null);
        image_cover_button = (ImageView) headView.findViewById(R.id.image_cover_button);
        edit_title = (EditText) headView.findViewById(R.id.edit_title);
        edit_title_place = (EditText) headView.findViewById(R.id.edit_title_place);
        post_header = (RelativeLayout) headView.findViewById(R.id.post_header);
        image_cover = (SimpleDraweeView) headView.findViewById(R.id.image_cover);
        image_set_cover = (ImageView) headView.findViewById(R.id.image_set_cover);
        image_delete_cover = (ImageView) headView.findViewById(R.id.image_delete_cover);
        adapter = new MyAdapter(PostActivity.this, this);
        linearLayoutManager = new LinearLayoutManager(this);
        edit_title.setOnClickListener(this);
        edit_title_place.setOnClickListener(this);
    }

    @Override
    public void setDates() {
        text_save.setOnClickListener(this);
        text_send.setOnClickListener(this);//需要把发布的参数传过去
        text_quit.setOnClickListener(this);
//        select_clum.setOnClickListener(this);
        image_cover_button.setOnClickListener(this);
        image_set_cover.setOnClickListener(this);

        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        post_recycler.setLayoutManager(linearLayoutManager);
        image_delete_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coverPath = "";
                image_cover.setImageBitmap(null);
                post_header.setVisibility(View.GONE);
                if (headView.getLayoutParams() == null) {
                    headView.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
                ViewGroup.LayoutParams params = headView.getLayoutParams();
                params.height = DensityUtil.dip2px(PostActivity.this, 100);
            }
        });


//        监听软键盘的状态
        new KeyBoardShowListener(PostActivity.this).setKeyboardListener(
                new KeyBoardShowListener.OnKeyboardVisibilityListener() {
                    @Override
                    public void onVisibilityChanged(boolean visible) {
                        keyBoard(visible);
                        //复制粘贴处理
                        if (visible) {
                            //软键盘已弹出
                            mItemTouchHelper.attachToRecyclerView(null);
                            keyBoardIsShow = true;
                            if (closeList.size() > 0) {
                                ExpandableViewHoldersUtil.closeH(closeList.get(0), closeList.get(0).getExpandView(), true, closeList.get(0).post_add, closeList.get(0).post_relative);
                            }
                        } else {
                            if (isCoverEditotTouch) {
                                //点击标题然后再取消弹出框加号的重新弹出设置
                                isCoverEditotTouch = false;
                                if (closeList.size() > 0) {
                                    ExpandableViewHoldersUtil.openH(closeList.get(0), closeList.get(0).getExpandView(), true, closeList.get(0).post_add, closeList.get(0).post_relative);
                                }
                            } else {
                                //重新弹出加号的设置
                                closeList.clear();
                                in = true;
                                pp = -1;
                            }
                            //软键盘未弹出
                            touchPost = -1;
                            keyBoardIsShow = false;
                            //解决复制粘贴冲突问题
                            mItemTouchHelper.attachToRecyclerView(post_recycler);

                            Boolean isRemove = false;
                            for (int i = 0; i < mItems.size(); i++) {
                                MyBean bean = mItems.get(i);
                                if (mItems.get(i).getType() == 2 && bean.getPlace() == null && TextUtils.isEmpty(bean.getPlace())) {
                                    isRemove = true;
                                    mItems.remove(i);
                                }
                            }

                            if (touchAnnotation != -1) {
                                MyBean touchBean = mItems.get(touchAnnotation);
                                if (TextUtils.isEmpty(touchBean.getAnnotation())) {
                                    touchBean.setOpen(false);
                                    if (holderMap.size() > 0 && holderMap.get(touchAnnotation) != null) {
                                        holderMap.get(touchAnnotation + 1).add_annotation.setText("添加注释");
                                        holderMap.get(touchAnnotation + 1).annotation_edit.setVisibility(View.GONE);
                                    }
                                } else {
                                    touchBean.setOpen(true);
                                    if (holderMap.size() > 0 && holderMap.get(touchAnnotation) != null) {
                                        holderMap.get(touchAnnotation + 1).add_annotation.setText("清空注释");
                                        holderMap.get(touchAnnotation + 1).annotation_edit.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                            if (isRemove)
                                adapter.notifyDataSetChanged();
                        }
                    }
                }, PostActivity.this);
        for (int i = 0; i < Bimp.drr.size(); i++) {
            MyBean myBean = new MyBean();
            myBean.setDrawe(Bimp.drr.get(i).getPath());
            myBean.setBitmap(Bimp.drr.get(i).getBitmap());
            myBean.setType(1);
            mItems.add(myBean);
        }
        post_recycler.setAdapter(adapter);

        //添加分割线
        post_recycler.addItemDecoration(new RecycleViewDivider(
                this, LinearLayoutManager.VERTICAL, 20, getResources().getColor(R.color.white)));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(post_recycler);
        adapter.setHeaderView(headView);
        adapter.setFooterView(footView);

        post_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int totalDy = 0;
            private double speed = 0;
            private long scrollTime = 0;
            private long scrollDistance = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //y轴滑动距离
                totalDy -= dy;
                if (scrollTime != 0 && scrollDistance != 0) {
                    speed = ((double) 1 / (System.currentTimeMillis() - scrollTime)) * (scrollDistance > totalDy ? scrollDistance - totalDy : totalDy - scrollDistance);
                }
                scrollTime = System.currentTimeMillis();
                scrollDistance = totalDy;
                if (speed < 0.3 && !keyBoardIsShow) {
                    if (linearLayoutManager.findFirstVisibleItemPosition() < 2) {
                        if (in) {
                            in = false;
                            pp = 0;
                            if (holderMap.size() > 0) {
                                if (closeList.size() > 0 && !closeList.get(0).equals(holderMap.get(pp))) {
                                    ExpandableViewHoldersUtil.closeH(closeList.get(0), closeList.get(0).getExpandView(), true, closeList.get(0).post_add, closeList.get(0).post_relative);
                                }
                                if (closeList.size() <= 0 || !closeList.get(0).equals(holderMap.get(pp))) {
                                    ExpandableViewHoldersUtil.openH(holderMap.get(pp), holderMap.get(pp).getExpandView(), true, holderMap.get(pp).post_add, holderMap.get(pp).post_relative);
                                    closeList.clear();
                                    closeList.add(holderMap.get(pp));
                                }
                            }
                        }
                    } else if (linearLayoutManager.findFirstVisibleItemPosition() < mItems.size() + 2) {
                        in = true;
                        if (pp != linearLayoutManager.findFirstVisibleItemPosition() - 1) {
                            /**这个判断是为了处理一个小bug，如果去掉当图片位置不恰当的时候再粘贴文字的时候输入框会突然消失*/
                            if (holderMap.size() == linearLayoutManager.findFirstVisibleItemPosition() || holderMap.get(linearLayoutManager.findFirstVisibleItemPosition()).post_linear.getTop() < DensityUtil.dip2px(PostActivity.this, 230)) {
                                pp = linearLayoutManager.findFirstVisibleItemPosition() - 1;
                                if (closeList.size() > 0 && !closeList.get(0).equals(holderMap.get(pp))) {
                                    ExpandableViewHoldersUtil.closeH(closeList.get(0), closeList.get(0).getExpandView(), true, closeList.get(0).post_add, closeList.get(0).post_relative);
                                }
                                if (closeList.size() <= 0 || !closeList.get(0).equals(holderMap.get(pp))) {
                                    ExpandableViewHoldersUtil.openH(holderMap.get(pp), holderMap.get(pp).getExpandView(), true, holderMap.get(pp).post_add, holderMap.get(pp).post_relative);
                                    closeList.clear();
                                    closeList.add(holderMap.get(pp));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    /**
     * 复制粘贴冲突问题解决
     */
    public void keyBoard(final boolean visible) {
        for (int i = 0; i < holderMap.size(); i++) {
            if (visible) {
                holderMap.get(i).post_edittext.setLongClickable(true);
                //如果不注释edittext将不可编辑
//                holderMap.get(i).post_edittext.setTextIsSelectable(true);
            } else {
                holderMap.get(i).post_edittext.setLongClickable(false);
//                holderMap.get(i).post_edittext.setTextIsSelectable(false);
            }
            holderMap.get(i).post_edittext.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    if (visible) {
                        return true;
                    } else {
                        return false;
                    }
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }
            });
            holderMap.get(i).post_edittext.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                initViews();
                setDates();
                break;
            case SELECTPHOTOFROMABLUM:
                for (int i = 0; i < Bimp.drr.size(); i++) {
                    MyBean myBean = new MyBean();
                    myBean.setDrawe(Bimp.drr.get(i).getPath());
                    myBean.setBitmap(Bimp.drr.get(i).getBitmap());
                    myBean.setType(1);//image
                    mItems.add(addposition + i, myBean);
                }
                adapter.notifyDataSetChanged();
                break;
            case SELECTPHOTOFROMABLUM_RESULTCODE://封面图片
                if (Bimp.drr.size() == 0) {
                    coverPath = "";
                    image_cover.setImageBitmap(null);
                    post_header.setVisibility(View.GONE);
                } else {
                    if (data == null) {
                        coverPath = "";
                        image_cover.setImageBitmap(null);
                        post_header.setVisibility(View.GONE);
                    } else {
                        coverPath = Bimp.drr.get(0).getPath();
                        post_header.setVisibility(View.VISIBLE);
                        setSimpleDraweeImage(Bimp.drr.get(0).getPath(), PostActivity.this, image_cover);
                        if (headView.getLayoutParams() == null) {
                            headView.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        }
                        ViewGroup.LayoutParams params = headView.getLayoutParams();
                        params.height = DensityUtil.dip2px(PostActivity.this, 400);
                        headView.setLayoutParams(params);
                    }
                }
                break;
            case VIDEOTYPE:
                for (int i = 0; i < Bimp.drr.size(); i++) {
                    MyBean myBean = new MyBean();
                    myBean.setDrawe(Bimp.drr.get(i).getPath());
                    myBean.setBitmap(Bimp.drr.get(i).getBitmap());
                    myBean.setDuration(Bimp.drr.get(i).getDuration());
                    myBean.setType(4);//video
                    mItems.add(addposition + i, myBean);
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.text_quit) {
            final AlertDialog myDialog = new AlertDialog.Builder(
                    PostActivity.this).create();
            myDialog.show();
            WindowManager.LayoutParams params = myDialog.getWindow().getAttributes();
            params.width = DensityUtil.dip2px(PostActivity.this, 280);
            myDialog.getWindow().setAttributes(params);
            Window window = myDialog.getWindow();
            window.setContentView(R.layout.beegree_layout_quit);
            TextView text_sure = (TextView) myDialog.getWindow()
                    .findViewById(R.id.text_sure);
            TextView text_continue = (TextView) myDialog.getWindow().findViewById(
                    R.id.text_continue);
            text_sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                    finish();
                }
            });
            text_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            });

        } else if (i == R.id.text_save) {
        } else if (i == R.id.text_send) {
            Toast.makeText(PostActivity.this, "发布了", Toast.LENGTH_SHORT).show();
        } else if (i == R.id.image_cover_button || i == R.id.image_set_cover) {
            Intent intentOne = new Intent(PostActivity.this, BucketPicActivity.class);
            intentOne.putExtra("type", SELECTPHOTOFROMABLUM_RESULTCODE);
            startActivityForResult(intentOne, SELECTPHOTOFROMABLUM_RESULTCODE);
            edit_title_place.setText(edit_title.getText());

        } else if (i == R.id.edit_title || i == R.id.edit_title_place) {
            isCoverEditotTouch = true;
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements ItemTouchHelperAdapter {

        public static final int TYPE_HEADER = 0;
        public static final int TYPE_NORMAL = 1;
        public static final int TYPE_FOOTER = 2;
        private Context mContext;
        private KeyBoardShowListener keyBoard;
        private final BaseSpringSystem mSpringSystem = SpringSystem.create();
        private Spring mScaleSpring;
        private final OnStartDragListener mDragStartListener;


        private View mHeaderView, mFooterView;

        public MyAdapter(Context context, OnStartDragListener dragStartListener) {
            this.mContext = context;
            mScaleSpring = mSpringSystem.createSpring();
            keyBoard = new KeyBoardShowListener(mContext);
            mDragStartListener = dragStartListener;
        }

        public void setHeaderView(View headerView) {
            mHeaderView = headerView;
            //这里是headview防止为空加的处理
            if (TextUtils.isEmpty(coverPath)) {
                if (mHeaderView.getLayoutParams() == null) {
                    mHeaderView.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
                ViewGroup.LayoutParams params = mHeaderView.getLayoutParams();
                params.height = DensityUtil.dip2px(PostActivity.this, 100);
                mHeaderView.setLayoutParams(params);
            }
            notifyItemInserted(0);
        }

        public void setFooterView(View footerView) {
            mFooterView = footerView;
            notifyItemInserted(mItems.size() + 3);
        }

        public View getHeaderView() {
            return mHeaderView;
        }

        public View getFooterView() {
            return mFooterView;
        }

        @Override
        public int getItemViewType(int position) {
            if (mHeaderView == null) return TYPE_NORMAL;
            if (position == 0) return TYPE_HEADER;
            if (position == mItems.size() + 2) return TYPE_FOOTER;
            return TYPE_NORMAL;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (mHeaderView != null && viewType == TYPE_HEADER) return new ViewHolder(mHeaderView);
            if (mFooterView != null && viewType == TYPE_FOOTER) return new ViewHolder(mFooterView);
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.beegree_layout_post_item, parent, false);
            return new ViewHolder(layout);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            edit_title.setCursorVisible(false);
            //取消光标闪烁
            keyBoard.setKeyboardListener(new KeyBoardShowListener.OnKeyboardVisibilityListener() {
                @Override
                public void onVisibilityChanged(boolean visible) {
                    if (holder.post_edittext != null) {
                        //EditText 获得焦点时hint消失，失去焦点时hint显示
                        if (edit_title_place.isFocused()) {
                            edit_title_place.setHint("");
                        } else {
                            edit_title_place.setHint(R.string.place_edittext);
                        }
                        if (visible) {
                            keyBoardIsShow = true;
                            holder.post_edittext.setCursorVisible(true);
                            holder.annotation_edit.setCursorVisible(true);
                            edit_title_place.setCursorVisible(true);
                            edit_title.setCursorVisible(true);
                        } else {
                            keyBoardIsShow = false;
                            holder.post_edittext.setCursorVisible(false);
                            holder.annotation_edit.setCursorVisible(false);
                            edit_title_place.setCursorVisible(false);
                            edit_title.setCursorVisible(false);
                            if (TextUtils.isEmpty(edit_title_place.getText()))
                                edit_title_place.setHint(R.string.place_edittext);
                        }
                    }

                }
            }, (PostActivity) mContext);

            if (getItemViewType(position) == TYPE_HEADER) return;
            if (getItemViewType(position) == TYPE_FOOTER) return;
            final int pos = getRealPosition(holder);
            if (holderMap.get(pos) == null || holderMap.get(pos) != holder) {
                holderMap.put(pos, holder);
            }
            holder.annotation_edit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
            holder.annotation_edit.setSingleLine(true);
            holder.annotation_edit.setImeOptions(EditorInfo.IME_ACTION_SEND);
            if (pos != 0) {
                final MyBean bean = mItems.get(pos - 1);
                if (bean.isOpen()) {
                    holder.annotation_edit.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(bean.getAnnotation())) {
                        holder.annotation_edit.setText("");
                    } else {
                        holder.annotation_edit.setText(bean.getAnnotation());
                    }
                } else {
                    holder.annotation_edit.setVisibility(View.GONE);
                }
                holder.post_relative_image.setVisibility(View.VISIBLE);
                holder.video_icon.setVisibility(View.GONE);
                if (mItems.get(pos - 1).getType() == 2) {
                    //文字
                    holder.post_edittext.setVisibility(View.VISIBLE);
                    holder.post_relative_image.setVisibility(View.GONE);
                    holder.point_relative.setVisibility(View.GONE);

                    if (holder.post_edittext.getTag() instanceof TextWatcher) {
                        holder.post_edittext.removeTextChangedListener((TextWatcher) (holder.post_edittext.getTag()));
                    }
                    holder.post_edittext.setTag(bean);//这里是为了防止recycleview的item中放置edittext造成的数据混乱
//                    holder.post_edittext.addTextChangedListener(null); //清除上个item的监听，防止oom
                    holder.post_edittext.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            //获得Edittext所在position里面的Bean，并设置数据
                            MyBean editBean = (MyBean) holder.post_edittext.getTag();
                            if (TextUtils.isEmpty(s)) {
                                editBean.setPlace(null);
                            } else {
                                editBean.setPlace(String.valueOf(s));
                            }
                        }
                    });

                    if (TextUtils.isEmpty(bean.getPlace())) {
                        holder.post_edittext.setText("");
                    } else {
                        holder.post_edittext.setText(bean.getPlace());
                    }
                    //获取焦点
                    if (touchPost == pos)
                        showSoftInputFromWindow(holder.post_edittext);
                } else if (mItems.get(pos - 1).getType() == 3) {
                    //小点
                    holder.point_relative.setVisibility(View.VISIBLE);
                    holder.post_edittext.setVisibility(View.GONE);
                    holder.post_relative_image.setVisibility(View.GONE);
                } else if (mItems.get(pos - 1).getType() == 1) {
                    //图片
                    holder.point_relative.setVisibility(View.GONE);
                    holder.post_edittext.setVisibility(View.GONE);
                    holder.post_relative_image.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(mItems.get(pos - 1).getDrawe())) {
                        setSimpleDrawee(mItems.get(pos - 1).getImagePath(), holder.post_image);
                    } else {
                        holder.post_image.setImageBitmap(imageCompress.showCacheBitmap(mItems.get(pos - 1).getDrawe()));
                    }
                    if (!TextUtils.isEmpty(mItems.get(pos - 1).getAnnotation())) {
                        holder.annotation_edit.setVisibility(View.VISIBLE);
                        holder.annotation_edit.setText(mItems.get(pos - 1).getAnnotation());
                    }
                } else if (mItems.get(pos - 1).getType() == 4) {
                    //视频
                    holder.point_relative.setVisibility(View.GONE);
                    holder.post_edittext.setVisibility(View.GONE);
                    holder.post_relative_image.setVisibility(View.VISIBLE);
                    holder.video_icon.setVisibility(View.VISIBLE);
                    setSimpleDraweeImage(mItems.get(pos - 1).getDrawe(), PostActivity.this, holder.post_image);
                }
                holder.annotation_edit.setTag(bean);
//                holder.annotation_edit.addTextChangedListener(null);
                holder.annotation_edit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        MyBean editBean = (MyBean) holder.annotation_edit.getTag();
                        if (TextUtils.isEmpty(s)) {
                            editBean.setAnnotation(null);
                        } else {
                            editBean.setAnnotation(String.valueOf(s));
                        }
                    }
                });
                //清除焦点
                holder.annotation_edit.clearFocus();

            } else {
                holder.post_relative_image.setVisibility(View.GONE);
                holder.point_relative.setVisibility(View.GONE);
                holder.post_edittext.setVisibility(View.GONE);
            }

            holder.post_linear_select.setVisibility(View.GONE);

            holder.post_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlphaAnimation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(500);//设置动画持续时间
                    holder.post_linear_select.setAnimation(animation);
                    holder.post_linear_select.setVisibility(View.VISIBLE);
                }
            });

            holder.post_minus_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlphaAnimation animation = new AlphaAnimation(1, 0);
                    animation.setDuration(500);//设置动画持续时间
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            holder.post_linear_select.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    holder.post_linear_select.startAnimation(animation);
                }
            });

            holder.image_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addposition = pos;
                    Intent intentOne = new Intent(PostActivity.this, BucketPicActivity.class);
                    intentOne.putExtra("type", TAKE_PICTURE);
                    startActivityForResult(intentOne, SELECTPHOTOFROMABLUM);
                }
            });
            holder.image_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addposition = pos;
                    Intent intentOne = new Intent(PostActivity.this, BucketPicActivity.class);
                    intentOne.putExtra("type", VIDEOTYPE);
                    startActivityForResult(intentOne, VIDEOTYPE);
                }
            });
            holder.image_three.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyBean myBean = new MyBean();
                    myBean.setType(2);//text
                    mItems.add(pos, myBean);
                    touchPost = pos + 1;
                    adapter.notifyDataSetChanged();
                }
            });
            holder.video_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, VideoActivity.class);
                    intent.putExtra("path", mItems.get(pos - 1).getDrawe());
                    intent.putExtra("type", "local");
                    mContext.startActivity(intent);
                }
            });
            holder.delete_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItems.remove(pos - 1);
                    adapter.notifyDataSetChanged();
                }
            });
            holder.delete_text_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItems.remove(pos - 1);
                    adapter.notifyDataSetChanged();
                }
            });
            if (holder.annotation_edit.getVisibility() == View.VISIBLE) {
                holder.add_annotation.setText("清空注释");
            } else {
                holder.add_annotation.setText("添加注释");
            }
            holder.add_annotation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final MyBean bean = mItems.get(pos - 1);
                    if (holder.annotation_edit.getVisibility() == View.VISIBLE) {
                        bean.setOpen(false);
                        bean.setAnnotation(null);
                        holder.add_annotation.setText("添加注释");
                        holder.annotation_edit.setVisibility(View.GONE);

                        //注释edittext隐藏软键盘
                        holder.annotation_edit.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(holder.annotation_edit.getWindowToken(), 0);
                    } else {
                        if (!bean.isOpen()) {
                            holder.annotation_edit.setText("");
                        }
                        bean.setOpen(true);
                        touchAnnotation = pos - 1;
                        holder.add_annotation.setText("清空注释");
                        holder.annotation_edit.setVisibility(View.VISIBLE);
                        //注释edittext弹出软键盘
                        holder.annotation_edit.setFocusable(true);
                        holder.annotation_edit.setFocusableInTouchMode(true);
                        holder.annotation_edit.requestFocus();
                        InputMethodManager inputManager =
                                (InputMethodManager) holder.annotation_edit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.showSoftInput(holder.annotation_edit, 0);
                    }
                }
            });
            holder.post_linear.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mDragStartListener.onStartDrag(holder);
                    return false;
                }
            });
        }

        /**
         * EditText获取焦点并显示软键盘
         */
        public void showSoftInputFromWindow(EditText editText) {
            //添加文字edittext弹出软键盘
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) PostActivity.this
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                }

            }, 100);//这里的时间大概是自己测试的
        }

        public int getRealPosition(RecyclerView.ViewHolder holder) {
            int position = holder.getLayoutPosition();
            return mHeaderView == null ? position : position - 1;
        }

        @Override
        public int getItemCount() {
            return mHeaderView == null ? mItems.size() + 2 : mItems.size() + 3;
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            if (toPosition - 2 >= 0) {
                isMoveItem = true;
                Collections.swap(mItems, fromPosition - 2, toPosition - 2);
                notifyItemMoved(fromPosition, toPosition);
            }
            return true;
        }

        @Override
        public void onItemDismiss(int position) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder, ExpandableViewHoldersUtil.Expandable {

            private final ExampleSpringListener mSpringListener = new ExampleSpringListener();
            private SimpleDraweeView post_image;
            private ImageView video_icon, image_one, image_two, image_three, delete_image, delete_text_image;
            private ImageView post_add, post_minus_image;
            private LinearLayout post_linear, post_linear_select;
            private RelativeLayout post_relative, post_relative_image, point_relative;
            private EditText post_edittext, annotation_edit;
            private TextView add_annotation, post_point_text;

            public ViewHolder(View itemView) {
                super(itemView);
                if (itemView == mHeaderView) return;
                post_relative = (RelativeLayout) itemView.findViewById(R.id.post_relative);
                post_relative_image = (RelativeLayout) itemView.findViewById(R.id.post_relative_image);
                post_image = (SimpleDraweeView) itemView.findViewById(R.id.post_image);
                video_icon = (ImageView) itemView.findViewById(R.id.video_icon);
                image_one = (ImageView) itemView.findViewById(R.id.post_image_one);
                image_two = (ImageView) itemView.findViewById(R.id.post_image_two);
                image_three = (ImageView) itemView.findViewById(R.id.post_image_three);
                delete_image = (ImageView) itemView.findViewById(R.id.delete_image);
                delete_text_image = (ImageView) itemView.findViewById(R.id.delete_text_image);
                post_minus_image = (ImageView) itemView.findViewById(R.id.post_minus_image);
                post_add = (ImageView) itemView.findViewById(R.id.post_add);
                post_linear = (LinearLayout) itemView.findViewById(R.id.post_linear);
                post_linear_select = (LinearLayout) itemView.findViewById(R.id.post_linear_select);
                annotation_edit = (EditText) itemView.findViewById(R.id.annotation_edit);
                post_edittext = (EditText) itemView.findViewById(R.id.post_edittext);
                add_annotation = (TextView) itemView.findViewById(R.id.add_annotation);
                point_relative = (RelativeLayout) itemView.findViewById(R.id.point_relative);
                post_point_text = (TextView) itemView.findViewById(R.id.post_point_text);
            }

            @Override
            public void onItemSelected() {
                isMoveItem = false;
                mScaleSpring.removeAllListeners();
                mScaleSpring.addListener(mSpringListener);
                mScaleSpring.setEndValue(1);
                if (closeList.size() > 0) {
                    ExpandableViewHoldersUtil.closeH(closeList.get(0), closeList.get(0).getExpandView(), true, closeList.get(0).post_add, closeList.get(0).post_relative);
                }
            }

            @Override
            public void onItemClear() {
                mScaleSpring.setEndValue(0);
                if (isMoveItem) {
                    isMoveItem = false;
                    adapter.notifyDataSetChanged();
                }
                int openNum = pp;
                if (holderMap.get(openNum) != null) {
                    closeList.clear();
                    closeList.add(holderMap.get(openNum));
                    ExpandableViewHoldersUtil.openH(holderMap.get(openNum), holderMap.get(openNum).getExpandView(), true, holderMap.get(openNum).post_add, holderMap.get(openNum).post_relative);
                }
            }

            @Override
            public View getExpandView() {
                return post_relative;
            }

            private class ExampleSpringListener extends SimpleSpringListener {
                @Override
                public void onSpringUpdate(Spring spring) {
                    float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 2.8, 1, 0.5);
                    if (post_linear != null) {
                        post_linear.setScaleX(mappedValue);
                        post_linear.setScaleY(mappedValue);
                    }
                }
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Bimp.drr != null)
            Bimp.drr.clear();
    }

}
