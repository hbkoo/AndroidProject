package com.example.acer.mynettest.Class;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.acer.mynettest.R;

/**
 * QQ图像下拉过度图片变大效果
 */

public class ScrollZoomListVIew extends ListView {

    private int mImageVIewHeight;//初始化高度
    private ImageView mImageView;

    public ScrollZoomListVIew(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
//        mImageVIewHeight = context.getResources()
//                .getDimensionPixelSize(R.dimen.size_defaultt_height);

    }


    public void setZoomImageVIew(ImageView iv) {
        mImageView = iv;
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

        //下拉过度的回调监听 dy
        /**
         * 两种情况：下拉的距离
         * deltaY：-  往下拉过度
         * deltaY：+  往上拉过度
         */

        if (deltaY < 0) {//往下拉过度
            //ImageView进行放大效果-------修改ImageView的高度+
            mImageView.getLayoutParams().height = mImageView.getHeight() - deltaY;
            mImageView.requestLayout();
        } else {
            //上滑过度
            mImageView.getLayoutParams().height = mImageView.getHeight() - deltaY;
            mImageView.requestLayout();
        }

        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        //往上推---图片缩小
        View header = (View) mImageView.getParent();
        //ListView会划出去的高度（负数）
        int deltaY = header.getTop();

        //只有当ImageView放大过，这里才会执行缩小
        if (mImageView.getHeight() > mImageVIewHeight) {
            mImageView.getLayoutParams().height = mImageView.getHeight() + deltaY;
            //由于划出去了一截，所以要让header父容器重新摆放Top为0
            header.layout(header.getLeft(), 0, header.getRight(), header.getHeight());
            mImageView.requestLayout();
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        //松手回弹效果
        if (action == MotionEvent.ACTION_UP) {
            //渐渐恢复到原来的高度----渐变动画：height
            ResetAnimation resatAnimator = new ResetAnimation();
            resatAnimator.setDuration(600);
            mImageView.startAnimation(resatAnimator);
        }
        return super.onTouchEvent(ev);
    }

    public class ResetAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            //执行动画百分比interpolatedTime:0f~1f
            /**
             *    0   ~     1
             * height ~ 初始的高度
             * 总的deltaY(变化的高度差) * interpolatedTime
             * 现在的高度 - 高度差*百分比
             */

            mImageView.getLayoutParams().height = (int) (mImageView.getHeight() -
                                (mImageView.getHeight() - mImageVIewHeight) * interpolatedTime);
            mImageView.requestLayout();

            super.applyTransformation(interpolatedTime, t);
        }
    }

}
