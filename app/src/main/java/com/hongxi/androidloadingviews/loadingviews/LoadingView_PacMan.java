package com.hongxi.androidloadingviews.loadingviews;

import android.animation.IntEvaluator;
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
    //default pacmanAndPeasColor of pacman and peas
    private final int DEFAULT_COLOR = 0XFF0099CC;
    //default pacmanAndPeasColor of pacman's eye
    private final int DEFAULT_EYE_COLOR = 0XFFFFFFFF;
    //default max angel of pacman's mouth
    private final int DEFAULT_MAX_ANGEL = 60;
    //the time of eating a pea
    private final int DEFAULT_DURATION = 600;
    //times between pacman's radius and pea's radius
    private final int DEFAULT_RADIUS_TIMES = 6;

    private int mViewWidth;
    private int mViewHeight;





    private int pacmanAndPeasColor = DEFAULT_COLOR;
    private int eyeColor = DEFAULT_EYE_COLOR;
    private int maxMouthAngel = DEFAULT_MAX_ANGEL;
    private int duration = DEFAULT_DURATION;

    //Pacman
    private RectF pacManRectF;
    private int pacManRadius = 0;
    private int currentMouthAngel = 60;
    private int eye_X=0;
    private int eye_Y=0;
    //3 peas
    private int peaRadius;
    private int pea1_X, pea2_X, pea3_X;
    private int peaDistance;
    private int peas_Y;
    private int move_X = 0;


    private Paint paint;
    private Paint eyePaint;

    public LoadingView_Pacman(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView_Pacman);
        pacmanAndPeasColor = typedArray.getColor(R.styleable.LoadingView_Pacman_pacman_and_peas_color, DEFAULT_COLOR);
        eyeColor = typedArray.getColor(R.styleable.LoadingView_Pacman_eye_color, DEFAULT_EYE_COLOR);
        duration = typedArray.getColor(R.styleable.LoadingView_Pacman_duration, DEFAULT_DURATION);
        maxMouthAngel = typedArray.getColor(R.styleable.LoadingView_Pacman_max_mouth_angel, DEFAULT_MAX_ANGEL);
        typedArray.recycle();

        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(pacmanAndPeasColor);

        eyePaint = new Paint();
        eyePaint.setAntiAlias(true);
        eyePaint.setColor(eyeColor);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
        int minSideSize = mViewWidth < mViewHeight ? mViewWidth : mViewHeight;
        pacManRadius = minSideSize >>2;

        //calculate the position of pacman and his eye
        int left = (mViewWidth - minSideSize)>>1;
        int top = (mViewHeight >> 1) -  pacManRadius;
        int right = left +( pacManRadius <<1 );
        int bottom = top + (pacManRadius <<1);
        pacManRectF = new RectF(left, top ,right, bottom);
        eye_X = (right + left)>>1;
        eye_Y = top + ((bottom-top) >>2);


        peaRadius = pacManRadius / DEFAULT_RADIUS_TIMES;
        // distance between 2 peas
        peaDistance = pacManRadius ;

        //calculate the original position of 3 peas
        peas_Y = (mViewHeight >> 1);
        pea1_X = right + (peaDistance >> 1);
        pea2_X = pea1_X + peaDistance;
        pea3_X = pea2_X + peaDistance;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(pacManRectF, currentMouthAngel >> 1, 360 - currentMouthAngel, true, paint);

        canvas.drawCircle(eye_X, eye_Y,peaRadius,eyePaint );
        canvas.drawCircle(pea1_X - move_X, peas_Y, peaRadius, paint);
        canvas.drawCircle(pea2_X - move_X, peas_Y, peaRadius, paint);
        canvas.drawCircle(pea3_X- move_X, peas_Y, peaRadius, paint);
    }

    private void startAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofInt(new Object(), "", 0, maxMouthAngel*2);
        animator.setEvaluator(new IntEvaluator());
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);
        animator.setRepeatCount(Integer.MAX_VALUE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentMouthAngel = (int) animation.getAnimatedValue();
                if(currentMouthAngel > maxMouthAngel){
                    currentMouthAngel = maxMouthAngel * 2 -currentMouthAngel;
                }
                //calculate the x-axis displacement of peas by currentMouthAngel
                move_X = ((int)animation.getAnimatedValue())*peaDistance/maxMouthAngel/2;
                invalidate();
            }
        });

        animator.start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }
}
