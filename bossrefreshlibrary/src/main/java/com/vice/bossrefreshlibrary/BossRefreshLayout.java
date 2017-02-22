package com.vice.bossrefreshlibrary;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by vice on 2017/2/17 0017.
 */
public class BossRefreshLayout extends FrameLayout {

    private BossRefresh refresh;
    private ListView lv;
    private float x = 0;
    private float y = 0;
    private boolean intercept = false;
    private boolean isTop = false;
    private float refreshHeight;
    private onRefreshingListener mListener;

    public BossRefreshLayout(Context context) {
        super(context);
        init();
    }

    public BossRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BossRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BossRefreshLayout);
        refresh.setCircleTopColor(ta.getColor(R.styleable.BossRefreshLayout_circleTopColor, Color.BLUE));
        refresh.setCircleLeftColor(ta.getColor(R.styleable.BossRefreshLayout_circleLeftColor, Color.RED));
        refresh.setCircleBottomColor(ta.getColor(R.styleable.BossRefreshLayout_circleBottomColor, Color.YELLOW));
        refresh.setCircleRightColor(ta.getColor(R.styleable.BossRefreshLayout_circleRightColor, Color.GREEN));
        refresh.setCircleRadius(DensityUtils.Dp2Px(getContext(), ta.getInteger(R.styleable.BossRefreshLayout_circleRadius, 5)));
        refresh.setDistance(DensityUtils.Dp2Px(getContext(), ta.getInteger(R.styleable.BossRefreshLayout_distance, 5)));
        refresh.setRefreshHeight(DensityUtils.Dp2Px(getContext(), ta.getInteger(R.styleable.BossRefreshLayout_refreshHeight, 50)));
        refresh.setCenterX(ta.getFloat(R.styleable.BossRefreshLayout_centerX, 0));
        refresh.setCenterY(ta.getFloat(R.styleable.BossRefreshLayout_centerY, 20));
        refresh.setBackGroundColor(ta.getColor(R.styleable.BossRefreshLayout_backgroundColor, Color.TRANSPARENT));

        ta.recycle();
    }


    private void init() {
        refresh = new BossRefresh(getContext());
        refresh.setLayoutParams(new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        refresh.post(new Runnable() {
            @Override
            public void run() {
                refreshHeight = refresh.getHeight();
            }
        });
        addView(refresh);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount < 2) {
            return;
        } else if (childCount > 2) {
            throw new IllegalArgumentException("只能有一个子元素且只能是ListView");
        }
        View child = getChildAt(1);
        if (!(child instanceof ListView)) {
            throw new IllegalArgumentException("子元素只能是ListView");
        }
        lv = (ListView) child;
//        lv.addHeaderView(refresh);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View firstChild = lv.getChildAt(0);
                if (firstVisibleItem == 0 && firstChild != null && firstChild.getTop() == 0) {
                    isTop = true;
                } else {
                    isTop = false;
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = ev.getRawX();
                y = ev.getRawY();
                intercept = false;
                break;

            case MotionEvent.ACTION_MOVE:
                float newX = ev.getRawX();
                float newY = ev.getRawY();
                float deltX = newX - x;
                float deltY = newY - y;
                if (isTop && deltY > 0) {
                    intercept = true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return super.onInterceptTouchEvent(ev);

        return intercept;
    }

    public void setRefreshing() {
        lv.setTranslationY(refreshHeight);
        refresh.setCurrentState(BossRefresh.STATE_REFRESHING);
        refresh.setPercent(1.0f);
        if (mListener != null) {
            mListener.onRefreshing();
        }
    }

    public void setBackGroundColor(int color) {
        setBackgroundColor(color);
    }

    public void setCircleTopColor(int color) {
        refresh.setCircleTopColor(color);
    }

    public void setCircleLeftColor(int color) {
        refresh.setCircleLeftColor(color);
    }

    public void setCircleBottomColor(int color) {
        refresh.setCircleBottomColor(color);
    }

    public void setCircleRightColorColor(int color) {
        refresh.setCircleRightColor(color);
    }

    public void setCircleRadius(int radius) {
        refresh.setCircleRadius(radius);
    }

    /**
     * 设置两个圈之间最短距离的1/2
     *
     * @param distance
     */
    public void setDistance(int distance) {
        refresh.setDistance(distance);
    }


    public void setComplete() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            goTop();
        } else if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    goTop();
                }
            });
        } else {
            throw new IllegalArgumentException("请切换到主线程或者在Activity中调用该方法");
        }
    }

    private void goTop() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(lv, "translationY", 0).setDuration(500);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                refresh.reset();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getRawX();
                y = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:

                float newX = event.getRawX();
                float newY = event.getRawY();
                float deltX = newX - x;
                float deltY = newY - y;
                x = newX;
                y = newY;

                if (Math.abs(deltY)/ Math.abs(deltX)>=1.0f){
                    float translationY = lv.getTranslationY() + deltY;
                    if (translationY <= refreshHeight) {
                        lv.setTranslationY( translationY);
                        float percent = translationY / (refreshHeight);
                        refresh.setCurrentState(BossRefresh.STATE_DRAG);
                        refresh.setPercent(percent);
                    } else if (translationY > refreshHeight) {
                        lv.setTranslationY(refreshHeight);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (lv.getTranslationY() >= refreshHeight) {
                    refresh.setCurrentState(BossRefresh.STATE_REFRESHING);
                }
                int currentState = refresh.getCurrentState();
                if (currentState == BossRefresh.STATE_DRAG) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(lv, "translationY", 0).setDuration(500);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float animatedFraction = animation.getAnimatedFraction();
                            refresh.setPercent(animatedFraction == 1.0f ? 0 : lv.getTranslationY() / refreshHeight);
                        }
                    });
                    animator.start();
                } else if (currentState == BossRefresh.STATE_REFRESHING) {
                    if (mListener != null) {
                        mListener.onRefreshing();
                    }
                }
                break;
        }

        return true;
//        return super.onTouchEvent(event);
    }

    public void setOnRefreshingListener(onRefreshingListener listener) {
        mListener = listener;
    }

    public interface onRefreshingListener {
        void onRefreshing();
    }
}
