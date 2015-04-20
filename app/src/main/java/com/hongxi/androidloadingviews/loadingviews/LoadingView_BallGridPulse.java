package com.hongxi.androidloadingviews.loadingviews;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.hongxi.androidloadingviews.R;

import java.util.Random;

/**
 * Created by Hongxi on 2015/4/16.
 */
public class LoadingView_BallGridPulse extends View {
    //default color of balls
    private final int DEFAULT_COLOR = 0XFFFFFFFF;

    private int ballsColor = DEFAULT_COLOR;
    private float ballRadius;


    private Ball[] balls = new Ball[9];

    class Ball {
        public float x;
        public float y;
        public float radius;
        public Paint paint;
    }


    public LoadingView_BallGridPulse(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView_Pacman);
        ballsColor = typedArray.getColor(R.styleable.LoadingView_BallGridPulse_ballgridpulse_color, DEFAULT_COLOR);
        typedArray.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mViewWidth = getMeasuredWidth();
        int mViewHeight = getMeasuredHeight();
        int minSideSize = mViewWidth < mViewHeight ? mViewWidth : mViewHeight;

        int left = (mViewWidth - minSideSize) >> 1;
        int top = ((mViewHeight - minSideSize) >> 1);
        int right = left + minSideSize;
        int bottom = top + minSideSize;

        float gridWidth = minSideSize / 3 / 2;
        ballRadius = gridWidth * 4 / 5;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                balls[i * 3 + j] = new Ball();
                balls[i * 3 + j].paint = new Paint();
                balls[i * 3 + j].paint.setColor(ballsColor);
                balls[i * 3 + j].x = left + (right - left) / 3 * (j + 1) - gridWidth;
                balls[i * 3 + j].y = top + (bottom - top) / 3 * (i + 1) - gridWidth;

                initAnimation(i * 3 + j);
            }
        }
    }

    private void initAnimation(final int ballIndex) {
        int delay = getRandom(1000);
        int duration = getRandom(1000) + 600;

        ObjectAnimator animator = ObjectAnimator.ofFloat(new Object(), "", 1.0f, 0.5f);
        animator.setEvaluator(new FloatEvaluator());
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                balls[ballIndex].radius = (ballRadius * (float) animation.getAnimatedValue());
                balls[ballIndex].paint.setAlpha((int) (255 * (0.7 + 0.3 * ((float) animation.getAnimatedValue() - 0.5) / 0.5)));

                invalidate();
            }
        });
        animator.setStartDelay(delay);
        animator.setDuration(duration);
        animator.start();
    }

    private int getRandom(int max) {
        return new Random().nextInt(max);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < 9; i++) {
            canvas.drawCircle(balls[i].x, balls[i].y, balls[i].radius, balls[i].paint);
        }
    }

}
