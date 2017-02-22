package com.vice.bossrefreshlibrary;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

/**
 * Created by vice on 2017/2/17 0017.
 */
public class BossRefresh extends View {
    private float percent = 0;
    private Paint mPaint;
    private float radius = DensityUtils.Dp2Px(getContext(), 5);
    private float distance = radius;
    private float centerX = 0;
    private float centerY = DensityUtils.Dp2Px(getContext(), 20);
    private int refreshHeight = DensityUtils.Dp2Px(getContext(), 50);
    private int current_state = STATE_IDLE;
    public static final int STATE_IDLE = 0;
    public static final int STATE_DRAG = 1;
    public static final int STATE_REFRESHING = 2;
    private int progress = 0;
    private int circleTopColor = Color.BLUE;
    private int circleRightColor = Color.GREEN;
    private int circleBottomColor = Color.YELLOW;
    private int circleLeftColor = Color.RED;
    private float xTop;
    private float yTop;
    private float xRight;
    private float yRight;
    private float xBottom;
    private float yBottom;
    private float xLeft;
    private float yLeft;
    private ValueAnimator animator;

    public BossRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BossRefresh(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BossRefresh(Context context) {
        super(context);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.UNSPECIFIED) {
            centerX = measureWidth / 2;
        }
        if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.UNSPECIFIED) {
            centerY = DensityUtils.Dp2Px(getContext(), 20);
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            measureHeight = this.refreshHeight;
            setMeasuredDimension(measureWidth, measureHeight);
            centerX = measureWidth / 2;
            centerY = DensityUtils.Dp2Px(getContext(), 20);
        }
        xTop = centerX;
        yTop = centerY - distance * 2;
        xRight = centerX + 2 * distance;
        yRight = centerY;
        xBottom = centerX;
        yBottom = 2 * centerY - yTop;
        xLeft = 2 * centerX - xRight;
        yLeft = centerY;
    }

    public void setPercent(float percent) {
        this.percent = percent;
        invalidate();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(radius * 2);
        mPaint.setAntiAlias(true);
        animator = ValueAnimator.ofInt(0, 360);
        animator.setDuration(1000);
        animator.setRepeatMode(Animation.RESTART);
        animator.setRepeatCount(-1);
        animator.setInterpolator(new LinearInterpolator());
        animator.setEvaluator(new FloatEvaluator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    public void setBackGroundColor(int color) {
        setBackgroundColor(color);
    }

    public void setCircleTopColor(int color) {
        circleTopColor = color;
    }

    public void setCircleLeftColor(int color) {
        circleLeftColor = color;
    }

    public void setCircleBottomColor(int color) {
        circleBottomColor = color;
    }

    public void setCircleRightColor(int color) {
        circleRightColor = color;
    }

    public void setCircleRadius(int radius) {
        this.radius = radius;
    }

    /**
     * 设置两个圈之间最短距离的1/2
     *
     * @param distance
     */
    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setRefreshHeight(int refreshHeight) {
        this.refreshHeight = refreshHeight;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }


    public void reset() {
        setCurrentState(STATE_IDLE);
        invalidate();
    }

    public void startAnimation() {
        if (animator != null && !animator.isStarted()) {
            animator.start();
        }
    }

    private void cancelAnimation() {
        if (animator != null && animator.isStarted()) {
            animator.cancel();
        }
    }

    public void setCurrentState(int currentState) {
        current_state = currentState;
        invalidate();
    }

    public int getCurrentState() {
        return current_state;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (current_state == STATE_DRAG) {
            drawDrag(canvas);
        } else if (current_state == STATE_REFRESHING) {
            startAnimation();
            drawRotate(canvas);
        } else if (current_state == STATE_IDLE) {
            cancelAnimation();
            distance = radius;
            progress = 0;
            percent = 0;
        }
    }

    private void drawTopCircle(Canvas canvas, int color) {
        mPaint.setColor(color);
        canvas.drawCircle(xTop, yTop, radius, mPaint);
    }

    private void drawTopCircle(Canvas canvas, int color, int alpha) {
        mPaint.setColor(color);
        mPaint.setAlpha(alpha);
        canvas.drawCircle(xTop, yTop, radius, mPaint);
    }

    private void drawLeftCircle(Canvas canvas, int color) {
        mPaint.setColor(color);
        canvas.drawCircle(xLeft, yLeft, radius, mPaint);
    }

    private void drawBottomCircle(Canvas canvas, int color) {
        mPaint.setColor(color);
        canvas.drawCircle(xBottom, yBottom, radius, mPaint);
    }

    private void drawRightCircle(Canvas canvas, int color) {
        mPaint.setColor(color);
        canvas.drawCircle(xRight, yRight, radius, mPaint);
    }

    private void drawDrag(Canvas canvas) {
        if (percent > 0 && percent < (float) 10 / 30) {
            drawTopCircle(canvas, circleTopColor, (int) (percent * 3 * 255));
        } else if (percent >= (float) 10 / 30 && percent < (float) 12 / 30) {
            float l = (float) 2 / 30;
            drawTopCircle(canvas, circleTopColor);
            canvas.drawCircle((float) (xTop - radius * percent / (float) 2 / 3), (float) (yTop + radius * percent / (float) 2 / 3), radius, mPaint);
            canvas.drawLine(xTop, yTop, (float) (xTop - radius * percent / (float) 2 / 3), (float) (yTop + radius * percent / (float) 2 / 3), mPaint);
        } else if (percent >= (float) 12 / 30 && percent <= (float) 13 / 30) {
            drawTopCircle(canvas, circleTopColor);
            drawLeftCircle(canvas, circleTopColor);
            canvas.drawLine(xTop, yTop, xTop - radius * 2, yTop + radius * 2, mPaint);
        } else if (percent > (float) 13 / 30 && percent < (float) 15 / 30) {
            float l = (float) 2 / 30;
            float x = (float) (xTop - radius - radius * (percent - (float) 13 / 30) / (float) 2 / 3);
            float y = (float) (yTop + radius + radius * (percent - (float) 13 / 30) / (float) 2 / 3);
            drawTopCircle(canvas, circleTopColor);
            canvas.drawCircle(x, y, radius, mPaint);
            drawLeftCircle(canvas, circleTopColor);
            canvas.drawLine(x, y, xLeft, yLeft, mPaint);
        } else if (percent >= (float) 15 / 30 && percent <= (float) 17 / 30) {
            float l = (float) 2 / 30;
            drawTopCircle(canvas, circleTopColor);
            drawLeftCircle(canvas, circleLeftColor);
            float x = (float) (xTop - radius * 2 + radius * (percent - (float) 15 / 30) / (float) 2 / 3);
            float y = (float) (yTop + radius * 2 + radius * (percent - (float) 15 / 30) / (float) 2 / 3);
            canvas.drawCircle(x, y, radius, mPaint);
            canvas.drawLine(xLeft, yLeft, x, y, mPaint);
        } else if (percent > (float) 17 / 30 && percent <= (float) 18 / 30) {
            drawTopCircle(canvas, circleTopColor);
            drawLeftCircle(canvas, circleLeftColor);
            drawBottomCircle(canvas, circleLeftColor);
            canvas.drawLine(xLeft, yLeft, xBottom, yBottom, mPaint);
        } else if (percent > (float) 18 / 30 && percent < (float) 20 / 30) {
            float l = (float) 2 / 30;
            drawTopCircle(canvas, circleTopColor);
            drawLeftCircle(canvas, circleLeftColor);
            drawBottomCircle(canvas, circleLeftColor);
            float x = (float) (xTop - radius * ((float) 2 / 3 - (percent - (float) 18 / 30) / (float) 2 / 3));
            float y = (float) (yTop + radius * 4 - radius * ((float) 2 / 3 - (percent - (float) 18 / 30) / (float) 2 / 3));
            canvas.drawCircle(x, y, radius, mPaint);
            canvas.drawLine(x, y, xBottom, yBottom, mPaint);
        } else if (percent >= (float) 20 / 30 && percent <= (float) 22 / 30) {
            float l = (float) 2 / 30;
            drawTopCircle(canvas, circleTopColor);
            drawLeftCircle(canvas, circleLeftColor);
            drawBottomCircle(canvas, circleBottomColor);
            float x = (float) (xTop + radius * (percent - (float) 20 / 30) / (float) 2 / 3);
            float y = (float) (yTop + radius * 4 - radius * (percent - (float) 20 / 30) / (float) 2 / 3);
            canvas.drawCircle(x, y, radius, mPaint);
            canvas.drawLine(xBottom, yBottom, x, y, mPaint);
        } else if (percent > (float) 22 / 30 && percent <= (float) 23 / 30) {
            drawTopCircle(canvas, circleTopColor);
            drawLeftCircle(canvas, circleLeftColor);
            drawBottomCircle(canvas, circleBottomColor);
            drawRightCircle(canvas, circleBottomColor);
            canvas.drawLine(xBottom, yBottom, xTop + radius * 2, yTop + radius * 2, mPaint);
        } else if (percent > (float) 23 / 30 && percent < (float) 25 / 30) {
            drawTopCircle(canvas, circleTopColor);
            drawLeftCircle(canvas, circleLeftColor);
            drawBottomCircle(canvas, circleBottomColor);
            drawRightCircle(canvas, circleBottomColor);
            float x = (float) (xTop + radius + radius * (percent - (float) 23 / 30) / (float) 2 / 30);
            float y = (float) (yTop + radius * 4 - radius - radius * (percent - (float) 23 / 30) / (float) 2 / 30);
            canvas.drawLine(x, y, xRight, yRight, mPaint);
            canvas.drawCircle(x, y, radius, mPaint);
        } else if (percent >= (float) 25 / 30 && percent <= (float) 27 / 30) {
            drawTopCircle(canvas, circleTopColor);
            drawLeftCircle(canvas, circleLeftColor);
            drawBottomCircle(canvas, circleBottomColor);
            drawRightCircle(canvas, circleRightColor);
            float x = (float) (xTop + 2 * radius - radius * (percent - (float) 25 / 30) / (float) 2 / 30);
            float y = (float) (yTop + radius * 2 - radius * (percent - (float) 25 / 30) / (float) 2 / 30);
            canvas.drawCircle(x, y, radius, mPaint);
            canvas.drawLine(xRight, yRight, x, y, mPaint);
        } else if (percent > (float) 27 / 30 && percent <= (float) 28 / 30) {
            drawTopCircle(canvas, circleRightColor);
            drawLeftCircle(canvas, circleLeftColor);
            drawBottomCircle(canvas, circleBottomColor);
            drawRightCircle(canvas, circleRightColor);
            canvas.drawLine(xRight, yRight, xTop, yTop, mPaint);
        } else if (percent > (float) 28 / 30 && percent < (float) 29 / 30) {
            drawTopCircle(canvas, circleRightColor);
            drawLeftCircle(canvas, circleLeftColor);
            drawBottomCircle(canvas, circleBottomColor);
            drawRightCircle(canvas, circleRightColor);
            float x = (float) (xTop + radius - (percent - (float) 28 / 30) / (float) (2 / 30) * radius);
            float y = (float) (yTop + radius - (percent - (float) 28 / 30) / (float) (2 / 30) * radius);
            canvas.drawCircle(x, y, radius, mPaint);
            canvas.drawLine(x, y, xTop, yTop, mPaint);
        } else if (percent >= (float) 29 / 30) {
            drawTopCircle(canvas, circleTopColor);
            drawLeftCircle(canvas, circleLeftColor);
            drawBottomCircle(canvas, circleBottomColor);
            drawRightCircle(canvas, circleRightColor);
        }
    }

    private void drawRotate(Canvas canvas) {
        int progress = this.progress % 360;
        if ((progress > 0 && progress <= 90) || (progress > 180 && progress <= 270)) {
            distance = radius * 3 / 4;
        } else if ((progress > 90 && progress <= 180) || (progress > 270 && progress <= 360)) {
            distance = radius;
        }
        xTop = centerX;
        yTop = centerY - distance * 2;
        xRight = centerX + 2 * distance;
        yRight = centerY;
        xBottom = centerX;
        yBottom = 2 * centerY - yTop;
        xLeft = 2 * centerX - xRight;
        yLeft = centerY;
        canvas.rotate(progress, centerX, centerY);
        mPaint.setColor(circleTopColor);
        canvas.drawCircle(xTop, yTop, radius, mPaint);
        mPaint.setColor(circleRightColor);
        canvas.drawCircle(xRight, yRight, radius, mPaint);
        mPaint.setColor(circleBottomColor);
        canvas.drawCircle(xBottom, yBottom, radius, mPaint);
        mPaint.setColor(circleLeftColor);
        canvas.drawCircle(xLeft, yLeft, radius, mPaint);

    }

    @Override
    protected void onDetachedFromWindow() {
        cancelAnimation();
        super.onDetachedFromWindow();
    }
}
