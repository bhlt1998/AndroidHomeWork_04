package com.bytedance.clockapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;

import com.bytedance.clockapplication.MainActivity;

import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class Clock extends View {

    private final static String TAG = Clock.class.getSimpleName();

    private static final int FULL_ANGLE = 360;

    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;

    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;

    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;

    public final static int AM = 0;

    private static final int RIGHT_ANGLE = 90;

    private int mWidth, mCenterX, mCenterY, mRadius;


    /**
     * properties
     */
    private int centerInnerColor;
    private int centerOuterColor;

    private int secondsNeedleColor;
    private int hoursNeedleColor;
    private int minutesNeedleColor;

    private int degreesColor;

    private int hoursValuesColor;

    private int numbersColor;

    private boolean mShowAnalog = true;

    public Clock(Context context) {
        super(context);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        if (widthWithoutPadding > heightWithoutPadding) {
            size = heightWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context, AttributeSet attrs) {

        this.centerInnerColor = Color.LTGRAY;
        this.centerOuterColor = DEFAULT_PRIMARY_COLOR;

        this.secondsNeedleColor = DEFAULT_SECONDARY_COLOR;
        this.hoursNeedleColor = DEFAULT_PRIMARY_COLOR;
        this.minutesNeedleColor = DEFAULT_PRIMARY_COLOR;

        this.degreesColor = DEFAULT_PRIMARY_COLOR;

        this.hoursValuesColor = DEFAULT_PRIMARY_COLOR;

        numbersColor = Color.WHITE;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getHeight() > getWidth() ? getWidth() : getHeight();

        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;

        if (mShowAnalog) {
            drawDegrees(canvas);
            drawHoursValues(canvas);
            drawNeedles(canvas);
            drawCenter(canvas);
            postInvalidateDelayed(1000);
        } else {
            drawNumbers(canvas);
            postInvalidateDelayed(1000);
        }

    }

    private void drawDegrees(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        for (int i = 0; i < FULL_ANGLE; i += 6 /* Step */) {

            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0)
                paint.setAlpha(CUSTOM_ALPHA);
            else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));

            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, paint);

        }
    }

    /**
     * @param canvas
     */
    private void drawNumbers(Canvas canvas) {

        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(mWidth * 0.2f);
        textPaint.setColor(numbersColor);
        textPaint.setColor(numbersColor);
        textPaint.setAntiAlias(true);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int amPm = calendar.get(Calendar.AM_PM);

        String time = String.format("%s:%s:%s%s",
                String.format(Locale.getDefault(), "%02d", hour),
                String.format(Locale.getDefault(), "%02d", minute),
                String.format(Locale.getDefault(), "%02d", second),
                amPm == AM ? "AM" : "PM");

        SpannableStringBuilder spannableString = new SpannableStringBuilder(time);
        spannableString.setSpan(new RelativeSizeSpan(0.3f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent

        StaticLayout layout = new StaticLayout(spannableString, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.translate(mCenterX - layout.getWidth() / 2f, mCenterY - layout.getHeight() / 2f);
        layout.draw(canvas);
    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas
     */
    private void drawHoursValues(Canvas canvas) {
        // Default Color:
        // - hoursValuesColor
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.07f);
        textPaint.setColor(hoursValuesColor);
        textPaint.setTextAlign(Paint.Align.CENTER);

        float r0 = mCenterX-(int)(mWidth * 0.1f);
        String[] hours_str = {"03", "02", "01", "12", "11", "10", "09", "08", "07", "06", "05", "04"};
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;

        //int baseLineY = (int)(mCenterY - top / 2 - bottom / 2);
        //canvas.drawText("01",mCenterX,baseLineY,textPaint);
        for (int i = 0; i < 12; i += 1){
            int angle = i * 30;
            int center_x = (int)(mCenterX + r0 * Math.cos(Math.toRadians(angle)));
            int center_y = (int)(mCenterX - r0 * Math.sin(Math.toRadians(angle)));
            int baseLineY = (int)(center_y - top/2 - bottom/2);
            canvas.drawText(hours_str[i],center_x,baseLineY,textPaint);
        }
    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas
     */
    private void drawNeedles(final Canvas canvas) {
        // Default Color:
        // - secondsNeedleColor
        // - hoursNeedleColor
        // - minutesNeedleColor
        float secondsNeedleLength = mWidth * 0.38f;
        float minutesNeedleLength = mWidth * 0.28f;
        float hoursNeedleLength = mWidth * 0.18f;
        Paint paint_second = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_second.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_second.setStrokeCap(Paint.Cap.ROUND);
        paint_second.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint_second.setColor(secondsNeedleColor);
        paint_second.setAlpha(CUSTOM_ALPHA);

        Paint paint_minute = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_minute.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_minute.setStrokeCap(Paint.Cap.ROUND);
        paint_minute.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH * 2);
        paint_minute.setColor(hoursNeedleColor);
        paint_minute.setAlpha(FULL_ALPHA);

        Paint paint_hour = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_hour.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_hour.setStrokeCap(Paint.Cap.ROUND);
        paint_hour.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH * 2);
        paint_hour.setColor(hoursNeedleColor);
        paint_hour.setAlpha(FULL_ALPHA);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        double rr = 2 * Math.PI / 60;
        double rr2 = 2*Math.PI / 12;
        double rr3 = rr2 / 60;

        double secondAngle = rr * second;
        double minuteAngle = rr * minute;
        double hourAngle = rr2 * hour + rr3 * minute;

        int secondX = (int) (mCenterX + secondsNeedleLength * Math.sin(secondAngle));
        int secondY = (int) (mCenterX - secondsNeedleLength * Math.cos(secondAngle));
        int minuteX = (int) (mCenterX + minutesNeedleLength * Math.sin(minuteAngle));
        int minuteY = (int) (mCenterX - minutesNeedleLength * Math.cos(minuteAngle));
        int hourX = (int) (mCenterX + hoursNeedleLength * Math.sin(hourAngle));
        int hourY = (int) (mCenterX - hoursNeedleLength * Math.cos(hourAngle));

        canvas.drawLine(secondX, secondY, mCenterX, mCenterX, paint_second);
        canvas.drawLine(minuteX, minuteY, mCenterX, mCenterX, paint_minute);
        canvas.drawLine(hourX, hourY, mCenterX, mCenterX, paint_hour);

    }

    /**
     * Draw Center Dot
     *
     * @param canvas
     */
    private void drawCenter(Canvas canvas) {
        // Default Color:
        // - centerInnerColor
        // - centerOuterColor
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(centerOuterColor);
        paint.setStyle(Paint.Style.FILL);
        int centerOuterRadius = (int) (mWidth * 0.05f);
        canvas.drawCircle(mCenterX, mCenterX, centerOuterRadius, paint);

        paint.setColor(centerInnerColor);
        int centerInnerRadius = (int)(mWidth * 0.03f);
        canvas.drawCircle(mCenterX, mCenterX, centerInnerRadius, paint);
    }

    public void setShowAnalog(boolean showAnalog) {
        mShowAnalog = showAnalog;
        invalidate();
    }

    public boolean isShowAnalog() {
        return mShowAnalog;
    }

}