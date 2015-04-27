package com.hongxi.androidloadingviews.loadingviews;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.hongxi.androidloadingviews.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Hongxi on 2015/4/16.
 */
public class LoadingView_BallPulse extends LinearLayout {
    //default color of balls
    private final int DEFAULT_COLOR = 0XFFFFFFFF;
    private final int PULSE_DELAY = 200;

    private int ballsColor = DEFAULT_COLOR;
    private float ballRadius;

    private Paint paint;

    private Ball[] balls = new Ball[3];

    private AnimatorSet animatorSet;
    private ArrayList<Animator> animatorList;




    public LoadingView_BallPulse(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView_Pacman);
        ballsColor = typedArray.getColor(R.styleable.LoadingView_BallGridPulse_ballgridpulse_color, DEFAULT_COLOR);
        typedArray.recycle();

        paint = new Paint();
        paint.setColor(ballsColor);

        balls[0] = new Ball(context);
        balls[1] = new Ball(context);
        balls[2] = new Ball(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minSideSize = Math.min(getMeasuredWidth(), getMeasuredHeight());

        int left = (getMeasuredWidth() - minSideSize) >> 1;
        int top = ((getMeasuredHeight() - minSideSize) >> 1);
        int right = left + minSideSize;
        int bottom = top + minSideSize;

        float gridWidth = minSideSize / 3 / 2;
        ballRadius = gridWidth * 4 / 5;

        removeAllViews();
        animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        animatorList = new ArrayList<>();
        for(int i=0;i<3;i++){
            LayoutParams layoutParams_balls = new LayoutParams(minSideSize/3,minSideSize/3);
            balls[i].setLayoutParams(layoutParams_balls);
            balls[i].setX(left+i*(minSideSize/3));
            balls[i].setY((bottom+top)/2);
            addView(balls[i]);

            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(balls[i], "ScaleX", 0.1f, 1f);
            scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            scaleXAnimator.setRepeatMode(ObjectAnimator.REVERSE);
            scaleXAnimator.setStartDelay(i * PULSE_DELAY);
            animatorList.add(scaleXAnimator);
            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(balls[i], "ScaleY", 0.1f, 1.0f);
            scaleYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            scaleYAnimator.setRepeatMode(ObjectAnimator.REVERSE);
            scaleYAnimator.setStartDelay(i * PULSE_DELAY);
            animatorList.add(scaleYAnimator);
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(balls[i], "Alpha", 0.7f, 1.0f);
            alphaAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            alphaAnimator.setRepeatMode(ObjectAnimator.REVERSE);
            alphaAnimator.setStartDelay(i * PULSE_DELAY);
            animatorList.add(alphaAnimator);
        }
        animatorSet.playTogether(animatorList);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animatorSet.cancel();
    }

    class Ball extends View{

        public Ball(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int radius = (Math.min(getWidth(), getHeight())) / 2;
            canvas.drawCircle(radius, radius, radius*7/8, paint);
        }
    }

}
