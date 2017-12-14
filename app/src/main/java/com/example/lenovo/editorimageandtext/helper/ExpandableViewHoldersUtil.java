package com.example.lenovo.editorimageandtext.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ExpandableViewHoldersUtil {

    public static void openH(final RecyclerView.ViewHolder holder, final View expandView, final boolean animate, final ImageView myText, RelativeLayout rl_item) {
        if (animate) {
            expandView.setVisibility(View.VISIBLE);
            myText.setVisibility(View.INVISIBLE);
            final Animator animator = ViewHolderAnimator.ofItemViewHeight(holder, rl_item);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //初始化
                    Animation translateAnimation = new TranslateAnimation(-100.0f, 0.1f, 0.1f, 0.1f);
                    //设置动画时间
                    translateAnimation.setDuration(200);
                    expandView.startAnimation(translateAnimation);
                    myText.setVisibility(View.VISIBLE);
                }
            });
            animator.start();
        } else {
            expandView.setVisibility(View.VISIBLE);
            myText.setVisibility(View.VISIBLE);
        }
    }

    public static void closeH(final RecyclerView.ViewHolder holder, final View expandView, final boolean animate, final ImageView myText, RelativeLayout rl_item) {
        if (animate) {
            expandView.setVisibility(View.GONE);
            myText.setVisibility(View.GONE);
            final Animator animator = ViewHolderAnimator.ofItemViewHeight(holder, rl_item);
            expandView.setVisibility(View.VISIBLE);
            myText.setVisibility(View.VISIBLE);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //初始化
                    Animation translateAnimation = new TranslateAnimation(0.1f, -100.0f, 0.1f, 0.1f);
                    //设置动画时间
                    translateAnimation.setDuration(200);
                    expandView.startAnimation(translateAnimation);
                    expandView.setVisibility(View.GONE);
                    myText.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    expandView.setVisibility(View.GONE);
                    myText.setVisibility(View.GONE);
                }
            });
            animator.start();
        } else {
            expandView.setVisibility(View.GONE);
            myText.setVisibility(View.GONE);
        }
    }

    public static interface Expandable {
        public View getExpandView();
    }

    public static class KeepOneH<VH extends RecyclerView.ViewHolder & Expandable> {
        private int _opened = -1;

        public void bind(VH holder, int pos, ImageView myText, RelativeLayout rl_item) {
            if (pos == _opened)
                ExpandableViewHoldersUtil.openH(holder, holder.getExpandView(), false, myText, rl_item);
            else
                ExpandableViewHoldersUtil.closeH(holder, holder.getExpandView(), false, myText, rl_item);
        }

        @SuppressWarnings("unchecked")
        public void toggle(VH holder, ImageView myText, RelativeLayout rl_item, int position, Boolean show) {
            if (show) {
                _opened = -1;
                ExpandableViewHoldersUtil.closeH(holder, holder.getExpandView(), true, myText, rl_item);
            } else {
                int previous = _opened;
                _opened = position;
                ExpandableViewHoldersUtil.openH(holder, holder.getExpandView(), true, myText, rl_item);

//                final VH oldHolder = (VH) ((RecyclerView) holder.itemView.getParent()).findViewHolderForPosition(previous);
//                if (oldHolder != null){
//                    ExpandableViewHoldersUtil.closeH(oldHolder, oldHolder.getExpandView(), true,myText,rl_item);
//                }
            }
        }
    }

}
