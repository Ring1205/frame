package com.jzxfyun.manager.wifi;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.jzxfyun.manager.R;

/**
 * https://www.jianshu.com/p/10c75feeccb3
 */
public class LoadingStatusView extends View {
    private int loadingColor;    //进度颜色
    private int loadSuccessColor;    //成功的颜色
    private int loadFailureColor;   //失败的颜色
    private float progressWidth;    //进度宽度
    private float progressRadius;   //圆环半径
    private boolean resultWithCircle;   //成功失败时是否显示外部圈

    private Paint mPaint;
    private StatusEnum mStatus;     //状态

    private int startAngle = -90;//开始角度
    private int minAngle = -90;//最小角度
    private int sweepAngle = 120;//扫描角度
    private int curAngle = 0;//当前角度

    //追踪Path的坐标
    private PathMeasure mPathMeasure;
    //画圆的Path
    private Path mPathCircle;
    //截取PathMeasure中的path
    private Path mPathCircleDst;
    private Path successPath;
    private Path failurePathLeft;
    private Path failurePathRight;

    private ValueAnimator circleAnimator;
    private float circleValue;
    private float successValue;
    private float failValueRight;
    private float failValueLeft;

    public LoadingStatusView(Context context) {
        this(context, null);
    }

    public LoadingStatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadingStatusView, defStyleAttr, 0);
        loadingColor = array.getColor(R.styleable.LoadingStatusView_loading_color, ContextCompat.getColor(context, R.color.load_failure));
        loadSuccessColor = array.getColor(R.styleable.LoadingStatusView_load_success_color, ContextCompat.getColor(context, R.color.load_success));
        loadFailureColor = array.getColor(R.styleable.LoadingStatusView_load_failure_color, ContextCompat.getColor(context, R.color.load_failure));
        progressWidth = array.getDimension(R.styleable.LoadingStatusView_progress_width, 6);//3
        progressRadius = array.getDimension(R.styleable.LoadingStatusView_progress_radius, 100);//40
        resultWithCircle = array.getBoolean(R.styleable.LoadingStatusView_result_with_circle, true);//40
        array.recycle();
        init();
    }

    private void init() {
        initPaint();
        initPath();
        initAnim();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(loadingColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(progressWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);    //设置画笔为圆角笔触
    }

    private void initPath() {
        mPathCircle = new Path();
        mPathMeasure = new PathMeasure();
        mPathCircleDst = new Path();
        successPath = new Path();
        failurePathLeft = new Path();
        failurePathRight = new Path();
    }

    private void initAnim() {
        circleAnimator = ValueAnimator.ofFloat(0, 1);
        circleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                circleValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        progressRadius = Math.min(width, height) * 9 / 20.0f;
//        int width;
//        int height;
//        int mode = MeasureSpec.getMode(widthMeasureSpec);
//        int size = MeasureSpec.getSize(widthMeasureSpec);
//
//        if (mode == MeasureSpec.EXACTLY) {
////            width = size;
//            width = 30;
//        } else {
//            //直径
//            width = (int) (2 * progressRadius + progressWidth + getPaddingLeft() + getPaddingRight());
//        }
//
//        mode = MeasureSpec.getMode(heightMeasureSpec);
//        size = MeasureSpec.getSize(heightMeasureSpec);
//        if (mode == MeasureSpec.EXACTLY) {
//            height = size;
//        } else {
//            height = (int) (2 * progressRadius + progressWidth + getPaddingTop() + getPaddingBottom());
//        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将当前画布的点移到getPaddingLeft,getPaddingTop,后面的操作都以该点作为参照点
        canvas.translate(getPaddingLeft() + progressRadius / 5, getPaddingTop() + progressRadius / 5);
        if (mStatus == StatusEnum.Loading) {//正在加载
            mPaint.setColor(loadingColor);
            if (startAngle == minAngle) {
                sweepAngle += 6;
            }
            if (sweepAngle >= 300 || startAngle > minAngle) {
                startAngle += 6;
                if (sweepAngle > 20) {//保持结束位置不变
                    sweepAngle -= 6;
                }
            }
            if (startAngle > minAngle + 300) {
                //startAngle = startAngle % 360;
                //216%360=216
                startAngle %= 360;
                minAngle = startAngle;
                sweepAngle = 20;
            }
            canvas.rotate(curAngle += 4, progressRadius, progressRadius);  //旋转的弧长为4
            canvas.drawArc(new RectF(0, 0, progressRadius * 2, progressRadius * 2), startAngle, sweepAngle, false, mPaint);
            invalidate();
        } else if (mStatus == StatusEnum.LoadSuccess) {//加载成功
            if (resultWithCircle) {
                mPaint.setColor(loadSuccessColor);
                mPathCircle.addCircle(getWidth() / 2f, getWidth() / 2f, progressRadius, Path.Direction.CW);//Path.Direction.CW 顺时针
                mPathMeasure.setPath(mPathCircle, false);
                mPathMeasure.getSegment(0, circleValue * mPathMeasure.getLength(), mPathCircleDst, true);//截取path并保存到mPathCircleDst中
                canvas.drawPath(mPathCircleDst, mPaint);//mPathCircleDst是一段长度变化的path

                if (circleValue == 1) {//表示圆画完了,可以钩了
                    successPath.moveTo(getWidth() / 8f * 3, getWidth() / 2f);
                    successPath.lineTo(getWidth() / 2f, getWidth() / 5f * 3);
                    successPath.lineTo(getWidth() / 3f * 2, getWidth() / 5f * 2);
                    mPathMeasure.nextContour();//下一个path
                    mPathMeasure.setPath(successPath, false);
                    mPathMeasure.getSegment(0, successValue * mPathMeasure.getLength(), mPathCircleDst, true);
                    canvas.drawPath(mPathCircleDst, mPaint);
                }
            } else {
                mPaint.setColor(loadSuccessColor);
                if (circleValue == 1) {//表示圆画完了,可以钩了
                    successPath.moveTo(getWidth() / 4f, getWidth() / 3f * 1);
                    successPath.lineTo(getWidth() / 2f, getWidth() / 3f * 2);
                    successPath.lineTo(getWidth() / 8f * 7, getWidth() / 5f * 1);
                    mPathMeasure.nextContour();//下一个path
                    mPathMeasure.setPath(successPath, false);
                    mPathMeasure.getSegment(0, successValue * mPathMeasure.getLength(), mPathCircleDst, true);
                    canvas.drawPath(mPathCircleDst, mPaint);
                }
            }
        } else {//加载失败
            if (resultWithCircle) {
                mPaint.setColor(loadFailureColor);
                mPathCircle.addCircle(getWidth() / 2f, getWidth() / 2f, progressRadius, Path.Direction.CW);
                mPathMeasure.setPath(mPathCircle, false);
                mPathMeasure.getSegment(0, circleValue * mPathMeasure.getLength(), mPathCircleDst, true);
                canvas.drawPath(mPathCircleDst, mPaint);

                if (circleValue == 1) {  //表示圆画完了,可以画叉叉的右边部分
                    failurePathRight.moveTo(getWidth() / 3f * 2, getWidth() / 3f);
                    failurePathRight.lineTo(getWidth() / 3f, getWidth() / 3f * 2);
                    mPathMeasure.nextContour();
                    mPathMeasure.setPath(failurePathRight, false);
                    mPathMeasure.getSegment(0, failValueRight * mPathMeasure.getLength(), mPathCircleDst, true);
                    canvas.drawPath(mPathCircleDst, mPaint);
                }

                if (failValueRight == 1) {    //表示叉叉的右边部分画完了,可以画叉叉的左边部分
                    failurePathLeft.moveTo(getWidth() / 3f, getWidth() / 3f);
                    failurePathLeft.lineTo(getWidth() / 3f * 2, getWidth() / 3f * 2);
                    mPathMeasure.nextContour();
                    mPathMeasure.setPath(failurePathLeft, false);
                    mPathMeasure.getSegment(0, failValueLeft * mPathMeasure.getLength(), mPathCircleDst, true);
                    canvas.drawPath(mPathCircleDst, mPaint);
                }
            } else {
                mPaint.setColor(loadFailureColor);
                if (circleValue == 1) {  //表示圆画完了,可以画叉叉的右边部分
                    failurePathRight.moveTo(getWidth() / 4f * 3, getWidth() / 4f);
                    failurePathRight.lineTo(getWidth() / 4f, getWidth() / 4f * 3);
                    mPathMeasure.nextContour();
                    mPathMeasure.setPath(failurePathRight, false);
                    mPathMeasure.getSegment(0, failValueRight * mPathMeasure.getLength(), mPathCircleDst, true);
                    canvas.drawPath(mPathCircleDst, mPaint);
                }

                if (failValueRight == 1) {    //表示叉叉的右边部分画完了,可以画叉叉的左边部分
                    failurePathLeft.moveTo(getWidth() / 4f, getWidth() / 4f);
                    failurePathLeft.lineTo(getWidth() / 4f * 3, getWidth() / 4f * 3);
                    mPathMeasure.nextContour();
                    mPathMeasure.setPath(failurePathLeft, false);
                    mPathMeasure.getSegment(0, failValueLeft * mPathMeasure.getLength(), mPathCircleDst, true);
                    canvas.drawPath(mPathCircleDst, mPaint);
                }
            }
        }
    }

    private void setStatus(StatusEnum status) {
        mStatus = status;
    }

    private void clearState() {
        startAngle = -90;
        minAngle = -90;
        sweepAngle = 120;
        curAngle = 0;
        circleValue = 0;
        successValue = 0;
        failValueRight = 0;
        failValueLeft = 0;

        initPath();
    }

    /**
     * 加载中状态
     */
    public void loadLoading() {
        clearState();
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }
        setStatus(StatusEnum.Loading);
        invalidate();
    }

    /**
     * 加载成功
     */
    public void loadSuccess() {
        setStatus(StatusEnum.LoadSuccess);
        startSuccessAnim();
    }

    /**
     * 加载失败
     */
    public void loadFailure() {
        setStatus(StatusEnum.LoadFailure);
        startFailAnim();
    }

    private void startSuccessAnim() {
        ValueAnimator success = ValueAnimator.ofFloat(0f, 1.0f);
        success.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                successValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        //组合动画,一先一后执行
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(success).after(circleAnimator);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    private void startFailAnim() {
        ValueAnimator failLeft = ValueAnimator.ofFloat(0f, 1.0f);
        failLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                failValueRight = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        ValueAnimator failRight = ValueAnimator.ofFloat(0f, 1.0f);
        failRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                failValueLeft = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        //组合动画,一先一后执行
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(failLeft).after(circleAnimator).before(failRight);
        animatorSet.setDuration(500);
        animatorSet.start();
    }
}