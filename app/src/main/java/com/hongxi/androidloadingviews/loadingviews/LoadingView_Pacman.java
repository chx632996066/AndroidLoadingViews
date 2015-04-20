package com.hongxi.androidloadingviews.loadingviews;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.hongxi.androidloadingviews.R;

/**
 * Created by Hongxi on 2015/4/16.
 */
public class LoadingView_Pacman extends View {
    //default color of pacman and peas
    private final int DEFAULT_COLOR = 0XFF0099CC;
    //default color of pacman's eye
    private final int DEFAULT_EYE_COLOR = 0XFFFFFFFF;
    //default max angel of pacman's mouth
    private final float DEFAULT_MAX_ANGEL = 60;
    //the time of eating a pea
    private final int DEFAULT_DURATION = 600;
    //times between pacman's radius and pea's radius
    private final int DEFAULT_RADIUS_TIMES = 6;


    private int mainColor = DEFAULT_COLOR;
    private int eyeColor = DEFAULT_EYE_COLOR;
    private float maxMouthAngel = DEFAULT_MAX_ANGEL;
    private int duration = DEFAULT_DURATION;

    //Pacman
    private RectF pacmanRectF;
    private float pacmanRadius = 0;
    private float currentMouthAngel = 60;
    private float eye_X = 0;
    private float eye_Y = 0;
    //3 peas
    private float peaRadius;
    private float pea1_X, pea2_X, pea3_X;
    private float peaDistance;
    private float peas_Y;
    private float move_X = 0;


    private Paint paint;
    private Paint eyePaint;

    public LoadingView_Pacman(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView_Pacman);
        mainColor = typedArray.getColor(R.styleable.LoadingView_Pacman_pacman_main_color, DEFAULT_COLOR);
        eyeColor = typedArray.getColor(R.styleable.LoadingView_Pacman_pacman_eye_color, DEFAULT_EYE_COLOR);
        duration = typedArray.getInt(R.styleable.LoadingView_Pacman_pacman_duration, DEFAULT_DURATION);
        maxMouthAngel = typedArray.getFloat(R.styleable.LoadingView_Pacman_pacman_max_mouth_angel, DEFAULT_MAX_ANGEL);
        typedArray.recycle();

        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mainColor);

        eyePaint = new Paint();
        eyePaint.setAntiAlias(true);
        eyePaint.setColor(eyeColor);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mViewWidth = getMeasuredWidth();
        int mViewHeight = getMeasuredHeight();
        int minSideSize = mViewWidth < mViewHeight ? mViewWidth : mViewHeight;
        pacmanRadius = minSideSize >> 2;

        //calculate the position of pacman and his eye
        float left = (mViewWidth - minSideSize) >> 1;
        float top = (mViewHeight >> 1) - pacmanRadius;
        float right = left + (pacmanRadius * 2);
        float bottom = top + (pacmanRadius * 2);
        pacmanRectF = new RectF(left, top, right, bottom);
        eye_X = (right + left) / 2;
        eye_Y = top + ((bottom - top) / 4);

        peaRadius = pacmanRadius / DEFAULT_RADIUS_TIMES;
        // distance between 2 peas
        peaDistance = pacmanRadius;

        //calculate the original position of 3 peas
        peas_Y = (mViewHeight >> 1);
        pea1_X = right + (peaDistance / 2);
        pea2_X = pea1_X + peaDistance;
        pea3_X = pea2_X + peaDistance;

        startAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(pacmanRectF, currentMouthAngel / 2, 360 - currentMouthAngel, true, paint);
        canvas.drawCircle(eye_X, eye_Y, peaRadius, eyePaint);

        canvas.drawCircle(pea1_X - move_X, peas_Y, peaRadius, paint);
        canvas.drawCircle(pea2_X - move_X, peas_Y, peaRadius, paint);
        canvas.drawCircle(pea3_X - move_X, peas_Y, peaRadius, paint);
    }

    private void startAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(new Object(), "", 0, maxMouthAngel * 2);
        animator.setEvaluator(new FloatEvaluator());
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentMouthAngel = (float) animation.getAnimatedValue();
                if (currentMouthAngel > maxMouthAngel) {
                    currentMouthAngel = maxMouthAngel * 2 - currentMouthAngel;
                }
                //calculate the x-axis displacement of peas by currentMouthAngel
                move_X = ((float) animation.getAnimatedValue()) * peaDistance / maxMouthAngel / 2;
                invalidate();
            }
        });
        animator.start();
    }
}
