package ca.tanas;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.HashSet;
import java.util.Set;

import tanas.ca.ev3controller.R;

/**
 * Created by Andrei Tanas on 2015-03-22.
 */
public class ControllerActivity extends ControllerActivityBase {
    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_controller;
    }

    @Override
    protected int getLeftMotorChannelViewId() {
        return R.id.left_motor_channel;
    }

    @Override
    protected int getRightMotorChannelViewId() {
        return R.id.right_motor_channel;
    }

    @Override
    protected int getLeftMotorPowerViewId() {
        return R.id.left_motor_power;
    }

    @Override
    protected int getRightMotorPowerViewId() {
        return R.id.right_motor_power;
    }

    @Override
    protected int getControllerViewId() {
        return R.id.touch_controller;
    }

    @Override
    protected int getSlider1ViewId() {
        return R.id.slider_1;
    }

    @Override
    protected int getSlider2ViewId() {
        return R.id.slider_2;
    }

    @Override
    protected void appLaunched() {
        RatingHelper.app_launched(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_controller, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            editSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void editSettings() {
        stopControl();

        final View addView = getLayoutInflater().inflate(R.layout.settings, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.settings_title);
        builder.setView(addView);

        int pad = (int)(24 * getResources().getDisplayMetrics().density);
        addView.setPadding(pad, 0, pad, 0);

        ArrayAdapter<BluetoothAddress> btListAdapter = new ArrayAdapter<BluetoothAddress>(
                this, android.R.layout.simple_spinner_dropdown_item);
        btListAdapter.add(new BluetoothAddress(null, null));
        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> btDevices =
                btAdapter == null
                        ? new HashSet<BluetoothDevice>()
                        : btAdapter.getBondedDevices();
        for (BluetoothDevice btDevice : btDevices)
            btListAdapter.add(new BluetoothAddress(btDevice.getName(), btDevice.getAddress()));

        final Spinner btSpinner = addView.findViewById(R.id.serial_dropdown);
        btSpinner.setAdapter(btListAdapter);

        final SharedPreferences settings = getSharedPreferences("ControllerSettings", MODE_PRIVATE);
        int pos = btListAdapter.getPosition(new BluetoothAddress(null, btAddress));
        if (pos >= 0) btSpinner.setSelection(pos);

        final Spinner leftMotorSpinner = addView.findViewById(R.id.left_motor_dropdown);
        if (leftMotor != null)
            leftMotorSpinner.setSelection(leftMotor.charAt(0) - 'A' + 1);

        final Spinner rightMotorSpinner = addView.findViewById(R.id.right_motor_dropdown);
        if (rightMotor != null)
            rightMotorSpinner.setSelection(rightMotor.charAt(0) - 'A' + 1);

        final CheckBox leftReverseCheckbox = addView.findViewById(R.id.left_motor_reverse);
        leftReverseCheckbox.setChecked(leftReverse);
        final CheckBox rightReverseCheckbox = addView.findViewById(R.id.right_motor_reverse);
        rightReverseCheckbox.setChecked(rightReverse);
        final CheckBox useOrientationCheckbox = addView.findViewById(R.id.use_orientation_sensor);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            useOrientation = false;
            useOrientationCheckbox.setVisibility(View.GONE);
        } else
            useOrientationCheckbox.setChecked(useOrientation);

        final Spinner slider1Spinner = addView.findViewById(R.id.slider_1_dropdown);
        if (slider1 != null)
            slider1Spinner.setSelection(slider1.charAt(0) - 'A' + 1);

        final Spinner slider2Spinner = addView.findViewById(R.id.slider_2_dropdown);
        if (slider2 != null)
            slider2Spinner.setSelection(slider2.charAt(0) - 'A' + 1);

        final CheckBox slider1StickyCheckbox = addView.findViewById(R.id.slider_1_sticky);
        slider1StickyCheckbox.setChecked(slider1Sticky);
        final CheckBox slider2StickyCheckbox = addView.findViewById(R.id.slider_2_sticky);
        slider2StickyCheckbox.setChecked(slider2Sticky);

        builder.setPositiveButton(R.string.settings_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                BluetoothAddress btSer = (BluetoothAddress)btSpinner.getSelectedItem();
                SharedPreferences.Editor settingsEditor = settings.edit();
                if (btSer != null && btSer.Address != null) {
                    btAddress = btSer.Address;
                    settingsEditor.putString("BluetoothAddress", btAddress);
                } else {
                    btAddress = null;
                    settingsEditor.remove("BluetoothAddress");
                }

                String lm = leftMotorSpinner.getSelectedItem().toString();
                if (lm == null || lm.length() != 1) {
                    leftMotor = null;
                    settingsEditor.remove("LeftMotor");
                } else {
                    leftMotor = lm;
                    settingsEditor.putString("LeftMotor", leftMotor);
                }
                leftChannelView.setText(leftMotor);

                String rm = rightMotorSpinner.getSelectedItem().toString();
                if (rm == null || rm.length() != 1) {
                    rightMotor = null;
                    settingsEditor.remove("RightMotor");
                } else {
                    rightMotor = rm;
                    settingsEditor.putString("RightMotor", rightMotor);
                }
                rightChannelView.setText(rightMotor);

                leftReverse = leftReverseCheckbox.isChecked();
                if (leftReverse)
                    settingsEditor.putBoolean("LeftReverse", true);
                else
                    settingsEditor.remove("LeftReverse");

                rightReverse = rightReverseCheckbox.isChecked();
                if (rightReverse)
                    settingsEditor.putBoolean("RightReverse", true);
                else
                    settingsEditor.remove("RightReverse");

                useOrientation = useOrientationCheckbox.isChecked();
                if (useOrientation)
                    settingsEditor.putBoolean("UseOrientation", true);
                else
                    settingsEditor.remove("UseOrientation");
                setUseOrientation();

                String s1 = slider1Spinner.getSelectedItem().toString();
                if (s1 == null || s1.length() != 1) {
                    slider1 = null;
                    settingsEditor.remove("Slider1");
                    slider1View.setEnabled(false);
                } else {
                    slider1 = s1;
                    settingsEditor.putString("Slider1", s1);
                    slider1View.setEnabled(true);
                }

                String s2 = slider2Spinner.getSelectedItem().toString();
                if (s2 == null || s2.length() != 1) {
                    slider2 = null;
                    settingsEditor.remove("Slider2");
                    slider2View.setEnabled(false);
                } else {
                    slider2 = s2;
                    settingsEditor.putString("Slider2", s2);
                    slider2View.setEnabled(true);
                    slider2View.setEnabled(true);
                }

                slider1Sticky = slider1StickyCheckbox.isChecked();
                if (slider1Sticky)
                    settingsEditor.putBoolean("Slider1Sticky", true);
                else
                    settingsEditor.remove("Slider1Sticky");
                slider1View.setSticky(slider1Sticky);

                slider2Sticky = slider2StickyCheckbox.isChecked();
                if (slider2Sticky)
                    settingsEditor.putBoolean("Slider2Sticky", true);
                else
                    settingsEditor.remove("Slider2Sticky");
                slider2View.setSticky(slider2Sticky);

                settingsEditor.putBoolean("NotConfigured", false);
                settingsEditor.commit();
                Connect();
            }
        });
        builder.setNegativeButton(R.string.settings_cancel, null);
        builder.show();
    }
}
