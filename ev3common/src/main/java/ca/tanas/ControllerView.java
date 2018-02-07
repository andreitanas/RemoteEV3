package ca.tanas;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;

/**
 * Created by Andrei Tanas on 14-11-25.
 */
public class ControllerView extends View implements View.OnTouchListener, SensorEventListener {
    private int w;
    private int h;
    private Paint axisPaint;
    private Paint pointerPaint;
    private float leftPower;
    private float rightPower;
    private PowerChangeListener powerChangeListener;
    private float currentX;
    private float currentY;
    private boolean pressed;

    float[] rotationMatrix = new float[9];
    float[] rotationValues = new float[3];
    private int displayRotation;

    public ControllerView(Context context) {
        this(context, null);
    }

    public ControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        axisPaint = new Paint();
        axisPaint.setColor(Color.LTGRAY);

        pointerPaint = new Paint();
        pointerPaint.setARGB(255, 150, 200, 150);
        pointerPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        pointerPaint.setShadowLayer(5, 0, 2, Color.BLACK);

        setOnTouchListener(this);
    }

    public void setUseOrientation(Activity activity, boolean useOrientation) {
        SensorManager sensorManager = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.unregisterListener(this, sensor);

        if (useOrientation) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        else {
            currentX = w/2;
            currentY = h/2;
            setLeftRightPower();
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    interface PowerChangeListener {
        void onPowerChanged(float leftPower, float rightPower);
    }

    public void setPowerChangeListener(PowerChangeListener listener) {
        powerChangeListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        this.w = w;
        this.h = h;
        currentX = w/2;
        currentY = h/2;
        setLeftRightPower();
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.drawLine(0, h/2 - 0.5f, w, h/2 - 0.5f, axisPaint);
        canvas.drawLine(w/2 - 0.5f, 0, w/2 - 0.5f, h, axisPaint);

        canvas.drawCircle(currentX, currentY, (w + h)/40, pointerPaint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            pressed = false;
            currentX = w/2;
            currentY = h/2;
        }
        else {
            pressed = true;
            currentX = event.getX();
            currentY = event.getY();
        }
        setLeftRightPower();
        return true;
    }

    private void setLeftRightPower() {
        float turn = 1f - currentX / (w / 2);
        float move = 1f - currentY / (h / 2);

        float left = move - turn / 2;
        float right = move + turn / 2;

        if (Math.abs(left) > 1f) {
            right *= Math.abs(1f / left);
            left = Math.signum(left);
        }
        if (Math.abs(right) > 1f) {
            left *= Math.abs(1f / right);
            right = Math.signum(right);
        }
        if (leftPower != left || rightPower != right) {
            leftPower = left;
            rightPower = right;
            if (powerChangeListener != null)
                powerChangeListener.onPowerChanged(leftPower, rightPower);
        }
        invalidate();
    }

    public void setDisplayRotation(int rotation) {
        this.displayRotation = rotation;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!pressed && event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_X, SensorManager.AXIS_Y,
                    rotationMatrix);
            SensorManager.getOrientation(rotationMatrix, rotationValues);

            //Log.i("Ev3", String.format("%f %f %f", rotationValues[0], rotationValues[1], rotationValues[2]));

            float rx = rotationValues[2] * 2f;
            float ry = rotationValues[1] * 2f;

            float t;
            switch (displayRotation) {
                case Surface.ROTATION_0:
                    break;
                case Surface.ROTATION_90:
                    t = rx;
                    rx = -ry;
                    ry = t;
                    break;
                case Surface.ROTATION_180:
                    ry = -ry;
                    break;
                case Surface.ROTATION_270:
                    t = rx;
                    rx = ry;
                    ry = -t;
                    break;
            }

            // natural way of holding is tilted towards person
            ry += 0.5f;
            if (Math.abs(rx) > 1)
                rx = Math.signum(rx);
            if (Math.abs(ry) > 1)
                ry = Math.signum(ry);

            // each less than 0.05
            if (rx * rx + ry * ry < 0.05 * 0.05) {
                rx = 0;
                ry = 0;
            }

            currentX = w/2 * (1f + rx);
            currentY = h/2 * (1f - ry);

            setLeftRightPower();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
