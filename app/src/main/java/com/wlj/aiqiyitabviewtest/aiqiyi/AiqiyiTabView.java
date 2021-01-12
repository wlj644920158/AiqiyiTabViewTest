package com.wlj.aiqiyitabviewtest.aiqiyi;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.wlj.aiqiyitabviewtest.R;

public class AiqiyiTabView extends View {

    private float padding;
    private Paint paint;
    private String[] titles = {"首页", "资讯", "发现", "我的"};
    private int[] iconRes = {R.mipmap.icon_home, R.mipmap.icon_news, R.mipmap.icon_discovery, R.mipmap.icon_mine};
    private Bitmap[] iconBitmaps;
    private int iconHeight;
    private Paint.FontMetrics fontMetrics = new Paint.FontMetrics();

    private int selectIndex = 0;
    private ValueAnimator valueAnimator;
    private float animationValue = 2.0f;
    private static float C = 0.55194f;//这个常数可以用贝塞尔来绘制不规则圆形以及规则圆形

    private Path circlePath = new Path();
    private Path bgPath = new Path();

    public AiqiyiTabView(Context context) {
        this(context, null);
    }

    public AiqiyiTabView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AiqiyiTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iconBitmaps = new Bitmap[iconRes.length];
        for (int i = 0; i < iconRes.length; i++) {
            iconBitmaps[i] = BitmapFactory.decodeResource(getResources(), iconRes[i]);
        }
        iconHeight = iconBitmaps[0].getHeight();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(sp2px(12));
        paint.getFontMetrics(fontMetrics);
        padding = dp2px(8);
    }

    private float sp2px(int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    private float dp2px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int maxHeight = (int) (padding * 5 + iconHeight + fontMetrics.descent - fontMetrics.ascent);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), maxHeight);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float unitWidth = getWidth() * 1.0f / iconRes.length;
        float circleMaxRadius = iconHeight / 2 + padding;
        float bgLeft = 0;
        float bgTop = padding * 2;
        float bgRight = getWidth();
        float bgBottom = getHeight();

        paint.setColor(Color.BLACK);

        if (animationValue < 1.0f) {
            canvas.drawRect(bgLeft, bgTop, bgRight, bgBottom, paint);
        } else {

            float animationValue2 = animationValue - 1.0f;
            float deltY = (1.0f - animationValue2) * padding;

            float selectTabCenterX = unitWidth * selectIndex + unitWidth / 2;
            float maxMaxCircleRadius = circleMaxRadius + padding;
            float bgLeftPaddingPointX = selectTabCenterX - maxMaxCircleRadius - padding;
            float bgLeftPointX = selectTabCenterX - maxMaxCircleRadius;
            float bgRightPaddingPointX = selectTabCenterX + maxMaxCircleRadius + padding;
            float bgRightPointX = selectTabCenterX + maxMaxCircleRadius;
            float bgCenterLeft = selectTabCenterX - maxMaxCircleRadius * C;
            float bgCenterRight = selectTabCenterX + maxMaxCircleRadius * C;
            bgPath.reset();
            bgPath.moveTo(bgLeft, bgTop);
            bgPath.lineTo(bgLeftPaddingPointX, bgTop);
            bgPath.cubicTo(bgLeftPointX, bgTop,
                    bgCenterLeft, deltY,
                    selectTabCenterX, deltY);
            bgPath.cubicTo(bgCenterRight, deltY,
                    bgRightPointX, bgTop,
                    bgRightPaddingPointX, bgTop);
            bgPath.lineTo(bgRight, bgTop);
            bgPath.lineTo(bgRight, bgBottom);
            bgPath.lineTo(bgLeft, bgBottom);
            bgPath.close();
            canvas.drawPath(bgPath, paint);
        }


        for (int i = 0; i < iconRes.length; i++) {
            float tabCenterX = unitWidth * i + unitWidth / 2;
            float iconTop = padding * 3;
            float iconLeft = tabCenterX - iconHeight / 2;
            float iconCenterY = iconTop + iconHeight / 2;
            float tabTxtBaseLine = getHeight() - padding - fontMetrics.descent;


            if (selectIndex == i) {
                paint.setColor(Color.RED);

                if (animationValue < 1.0f) {
                    //目前动画第一阶段
                    float curRadius = animationValue * circleMaxRadius;
                    canvas.drawCircle(tabCenterX, iconCenterY, curRadius, paint);
                    canvas.drawBitmap(iconBitmaps[i], iconLeft, iconTop, null);
                } else {
                    float animationValue2 = animationValue - 1.0f;

                    float iconCenterY2 = iconCenterY - animationValue2 * padding;
                    float iconTop2 = iconTop - animationValue2 * padding;
                    float deltaR;
                    if (animationValue2 < 0.5) {
                        deltaR = padding * animationValue2;
                    } else {
                        deltaR = (0.5f - (animationValue2 - 0.5f)) * padding;
                    }

                    circlePath.reset();
                    circlePath.moveTo(tabCenterX - circleMaxRadius, iconCenterY2);
                    circlePath.addArc(tabCenterX - circleMaxRadius, iconCenterY2 - circleMaxRadius,
                            tabCenterX + circleMaxRadius, iconCenterY2 + circleMaxRadius, 180, 180);
                    circlePath.cubicTo(tabCenterX + circleMaxRadius, iconCenterY2 + circleMaxRadius * C,
                            tabCenterX + circleMaxRadius * C, iconCenterY2 + circleMaxRadius + deltaR,
                            tabCenterX, iconCenterY2 + circleMaxRadius + deltaR);
                    circlePath.cubicTo(tabCenterX - circleMaxRadius * C, iconCenterY2 + circleMaxRadius + deltaR,
                            tabCenterX - circleMaxRadius, iconCenterY2 + circleMaxRadius * C,
                            tabCenterX - circleMaxRadius, iconCenterY2);

                    canvas.drawPath(circlePath, paint);
                    canvas.drawBitmap(iconBitmaps[i], iconLeft, iconTop2, null);
                }

                canvas.drawText(titles[i], tabCenterX, tabTxtBaseLine, paint);
            } else {
                paint.setColor(Color.WHITE);
                canvas.drawText(titles[i], tabCenterX, tabTxtBaseLine, paint);
                canvas.drawBitmap(iconBitmaps[i], iconLeft, iconTop, null);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float unitWidth = getWidth() * 1.0f / iconRes.length;
            int index = (int) (x / unitWidth);
            if (index == selectIndex) return false;
            selectIndex = index;
            if (valueAnimator != null && valueAnimator.isRunning()) {
                valueAnimator.cancel();
                valueAnimator = null;
            }

            valueAnimator = ValueAnimator.ofFloat(0.0f, 2.0f);
            valueAnimator.setDuration(1000);
            valueAnimator.setInterpolator(new BounceInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    animationValue = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            valueAnimator.start();
        }
        return true;
    }
}
