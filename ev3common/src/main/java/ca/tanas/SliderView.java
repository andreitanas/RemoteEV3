package ca.tanas;

/**
 * Created by Andrei Tanas on 14-12-01.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SliderView extends View {
    static final float padding = 0f;
    public float position;
    public boolean sticky;
    private SliderViewChangeListener changeListener;
    private final Paint axisPaint;
    private Paint pointerPaint;
    private int w;
    private int h;

    public SliderView(Context context) {
        this(context, null, 0);
    }

    public SliderView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public SliderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        axisPaint = new Paint();
        axisPaint.setColor(Color.LTGRAY);

        pointerPaint = new Paint();
        pointerPaint.setARGB(255, 150, 200, 150);
        pointerPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        pointerPaint.setShadowLayer(5, 0, 2, Color.BLACK);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        this.w = w;
        this.h = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isEnabled())
            return;

        if (w < h) {
            canvas.drawLine(0, h/2 - 0.5f, w, h/2 - 0.5f, axisPaint);

            canvas.drawCircle(w / 2,
                    h - ((h - w - padding * 2f) * ((position + 1f) / 2) + w / 2 + padding),
                    w / 2 - padding, pointerPaint);
        } else {
            canvas.drawLine(w/2 - 0.5f, 0, w/2 - 0.5f, h, axisPaint);

            canvas.drawCircle(
                    (float)w - ((w - h - padding * 2f) * ((position + 1f) / 2) + h / 2 + padding),
                    h / 2,
                    h / 2 - padding, pointerPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled())
            return true;

        float oldPosition = position;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!sticky)
                position = 0;
        } else {
            if (w < h) {
                float y = event.getY();
                position = (h / 2 - y) / ((h - w - padding * 2f) / 2);
            } else {
                float x = event.getX();
                position = (w / 2 - x) / ((w - h - padding * 2f) / 2);
            }

            if (position > 1f)
                position = 1f;
            else if (position < -1f)
                position = -1f;
            else if (position >= -0.05f && position <= 0.05f)
                position = 0f;
        }

        if (oldPosition != position) {
            oldPosition = position;
            if (changeListener != null)
                changeListener.onSliderPositionChanged(this);
        }
        invalidate();
        return true;
    }

    public void setSticky(boolean value) {
        if (sticky != value) {
            position = 0;
            invalidate();
        }
        sticky = value;
    }
    public void setChangeListener(SliderViewChangeListener listener) {
        changeListener = listener;
    }

    public interface SliderViewChangeListener {
        void onSliderPositionChanged(SliderView eventSource);
    }
}
