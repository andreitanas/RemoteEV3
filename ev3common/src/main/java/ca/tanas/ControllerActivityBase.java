package ca.tanas;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import lego.ev3.core.ArgumentException;
import lego.ev3.core.Brick;
import lego.ev3.core.Enums;
import lego.ev3.core.OutputPort;

/**
 * Created by Andrei Tanas on 14-11-25.
 */
public abstract class ControllerActivityBase extends Activity
        implements ControllerView.PowerChangeListener, SliderView.SliderViewChangeListener {

    protected String leftMotor;
    protected String rightMotor;
    protected String btAddress;
    protected boolean leftReverse;
    protected boolean rightReverse;
    protected boolean useOrientation;

    protected String slider1;
    protected String slider2;
    protected boolean slider1Sticky;
    protected boolean slider2Sticky;

    private TextView leftPowerView;
    private TextView rightPowerView;
    protected TextView leftChannelView;
    protected TextView rightChannelView;
    private ControllerView controllerView;
    protected SliderView slider1View;
    protected SliderView slider2View;

    private Ev3Connection connection;
    private Brick brick;
    private boolean stopped;
    private boolean retainedInstance;

    protected abstract void editSettings();

    protected abstract int getContentViewLayoutId();
    protected int getLeftMotorChannelViewId() { return 0; }
    protected int getRightMotorChannelViewId() { return 0; }
    protected int getLeftMotorPowerViewId() { return 0; }
    protected int getRightMotorPowerViewId() { return 0; }
    protected int getControllerViewId() { return 0; }
    protected int getSlider1ViewId() { return 0; }
    protected int getSlider2ViewId() { return 0; }
    protected void appLaunched() { }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());

        SharedPreferences settings = getSharedPreferences("ControllerSettings", MODE_PRIVATE);
        btAddress = settings.getString("BluetoothAddress", null);
        leftMotor = settings.getString("LeftMotor", null);
        rightMotor = settings.getString("RightMotor", null);
        leftReverse = settings.getBoolean("LeftReverse", false);
        rightReverse = settings.getBoolean("RightReverse", false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
            useOrientation = settings.getBoolean("UseOrientation", false);
        slider1 = settings.getString("Slider1", null);
        slider2 = settings.getString("Slider2", null);
        slider1Sticky = settings.getBoolean("Slider1Sticky", false);
        slider2Sticky = settings.getBoolean("Slider2Sticky", false);

        leftChannelView = findViewById(getLeftMotorChannelViewId());
        rightChannelView = findViewById(getRightMotorChannelViewId());
        leftPowerView = findViewById(getLeftMotorPowerViewId());
        rightPowerView = findViewById(getRightMotorPowerViewId());

        if (leftChannelView != null)
            leftChannelView.setText(leftMotor);
        if (rightChannelView != null)
            rightChannelView.setText(rightMotor);

        Object tmp = getLastNonConfigurationInstance();
        if (tmp instanceof ControllerActivityBase) {
            ControllerActivityBase retainedInstance = (ControllerActivityBase)tmp;
            connection = retainedInstance.connection;
            brick = retainedInstance.brick;
            stopped = retainedInstance.stopped;
        }

        if (connection == null) {
            connection = new Ev3Connection(this, btAddress);
            brick = null;
        }

        try {
            if (brick == null)
                brick = new Brick(connection);
        } catch (ArgumentException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

        controllerView = findViewById(getControllerViewId());
        controllerView.setPowerChangeListener(this);
        setUseOrientation();

        slider1View = findViewById(getSlider1ViewId());
        slider2View = findViewById(getSlider2ViewId());
        slider1View.setChangeListener(this);
        slider2View.setChangeListener(this);
        if (slider1 == null)
            slider1View.setEnabled(false);
        if (slider2 == null)
            slider2View.setEnabled(false);
        slider1View.setSticky(slider1Sticky);
        slider2View.setSticky(slider2Sticky);

        if (settings.getBoolean("NotConfigured", true))
            editSettings();
        else
            appLaunched();
    }

    @Override
    protected void onResume() {
        super.onResume();
        controllerView.setDisplayRotation(getWindowManager().getDefaultDisplay().getRotation());
        Connect();
        controllerView.setUseOrientation(this, useOrientation);
    }

    @Override
    protected void onPause() {
        super.onPause();
        controllerView.setUseOrientation(this, false);
        stopControl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // retained instance set: will restart (e.g. orientation change)
        if (!retainedInstance && btAddress != null)
            Disconnect();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        this.retainedInstance = true;
        return this;
    }

    protected void Connect() {
        if (btAddress == null || connection.isConnected() && !connection.getAddress().equals(btAddress)) {
            Disconnect();
            if (btAddress != null) {
                connection = new Ev3Connection(this, btAddress);
                try {
                    brick = new Brick(connection);
                } catch (ArgumentException e) {
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }

        if (btAddress != null) {
            try {
                if (!connection.isConnected()) {
                    connection = new Ev3Connection(this, btAddress);
                    brick = new Brick(connection);
                    brick.Connect();
                    brick.getDirectCommand().SetLedPattern(Enums.LedPattern.Black);
                    brick.getDirectCommand().PlayTone(50, (short)1000, (short)200);
                }
                resumeControl();
            } catch (ArgumentException e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
            }
        }
    }

    private void Disconnect() {
        try {
            if (connection.isConnected())
                brick.getDirectCommand().StopMotor(OutputPort.All, true);
            brick.Disconnect();
        } catch (ArgumentException e) {
            e.printStackTrace();
        }
    }

    protected void setUseOrientation() {
        if (useOrientation)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        controllerView.setUseOrientation(this, useOrientation);
    }

    protected void stopControl() {
        onPowerChanged(0, 0);
        stopped = true;
    }

    private void resumeControl() {
        stopped = false;
    }

    @Override
    public void onPowerChanged(float leftPower, float rightPower) {
        if (stopped)
            return;

        leftPowerView.setText(String.valueOf(leftPower));
        rightPowerView.setText(String.valueOf(rightPower));

        OutputPort left = translatePort(leftMotor);
        OutputPort right = translatePort(rightMotor);

        if (leftReverse)
            leftPower = -leftPower;
        if (rightReverse)
            rightPower = -rightPower;

        try {
            if (connection.isConnected()) {
                if (leftPower == 0 && rightPower == 0)
                    brick.getDirectCommand().StopMotor(left.Also(right), false);
                else if (leftPower == rightPower)
                    brick.getDirectCommand().TurnMotorAtSpeed(left.Also(right), (int)(leftPower * 100));
                else {
                    brick.getDirectCommand().TurnMotorAtSpeed(left, (int)(leftPower * 100));
                    brick.getDirectCommand().TurnMotorAtSpeed(right, (int)(rightPower * 100));
                }
            }
        } catch (ArgumentException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
        }
    }

    private OutputPort translatePort(String leftMotor) {
        if ("A".equals(leftMotor))
            return OutputPort.A;
        else if ("B".equals(leftMotor))
            return OutputPort.B;
        else if ("C".equals(leftMotor))
            return OutputPort.C;
        else if ("D".equals(leftMotor))
            return OutputPort.D;
        else
            return OutputPort.None;
    }

    @Override
    public void onSliderPositionChanged(SliderView eventSource) {
        if (stopped)
            return;

        OutputPort port;
        if (eventSource == slider1View)
            port = translatePort(slider1);
        else if (eventSource == slider2View)
            port = translatePort(slider2);
        else
            return;

        try {
            if (connection.isConnected())
                brick.getDirectCommand().TurnMotorAtPower(port, (int)(eventSource.position * 100));
        } catch (ArgumentException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
        }
    }
}
