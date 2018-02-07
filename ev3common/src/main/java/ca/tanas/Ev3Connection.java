package ca.tanas;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import ca.tanas.ev3common.R;
import lego.ev3.core.ICommunication;

/**
 * Created by Andrei Tanas on 14-11-27.
 */
public class Ev3Connection  implements ICommunication {
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final Activity parentActivity;
    private final String address;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    private IReportReceiver reportReceiver;

    public Ev3Connection(Activity parent, String address) {
        this.parentActivity = parent;
        this.address = address;
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
    }

    private void checkBTState() {
        if (btAdapter==null) {
            Toast.makeText(parentActivity, R.string.bluetooth_not_supported,
                    Toast.LENGTH_LONG).show();
        } else {
            if (!btAdapter.isEnabled()) {
                btAdapter.enable();
            }
        }
    }

    @Override
    public void SetReportReceiver(IReportReceiver receiver) {
        this.reportReceiver = receiver;
    }

    @Override
    public void Connect() {
        if (address == null)
            return;
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try {
            btSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
            // ??
            btAdapter.cancelDiscovery();
            btSocket.connect();
            outputStream = btSocket.getOutputStream();
            inputStream = btSocket.getInputStream();

            new Thread(new Runnable() {
                @Override
                public void run() {
                while (inputStream != null) {
                    try {
                        int len = inputStream.read() | inputStream.read() << 8;
                        byte[] buffer = new byte[len];
                        if (reportReceiver != null)
                            reportReceiver.ReceiveReport(buffer);
                    } catch (IOException e) {
                        if (inputStream != null)
                            Disconnect();
                    }
                }
                }
            }).start();
        } catch (Exception e) {
            Toast.makeText(parentActivity, R.string.could_not_connect,
                    Toast.LENGTH_LONG).show();
            Toast.makeText(parentActivity, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void Disconnect() {
        try {
            if (inputStream != null) {
                InputStream stream = inputStream;
                inputStream = null;
                stream.close();
            }
        } catch (Exception e) {
            Log.e("Ev3", "Close input stream: ", e);
        }
        try {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            Log.e("Ev3", "Close output stream", e);
        }
        finally {
            outputStream = null;
        }
        try {
            if (btSocket != null) {
                btSocket.close();
            }
        } catch (Exception e) {
            Log.e("Ev3", "Close socket", e);
        }
        finally {
            btSocket = null;
        }
    }

    public void Write(byte[] buffer) {
        try {
            if (outputStream != null)
            {
                outputStream.write(buffer);
                //StringBuilder sb = new StringBuilder();
                //sb.append(String.format("%d bytes: ", buffer.length));
                //for (byte b : buffer) {
                //    sb.append(String.format("%02X ", b));
                //}
                //Log.i("Ev3", sb.toString());
            }
        } catch (Exception e) {
            Log.e("Ev3", e.getMessage());
        }
    }

    public boolean isConnected() {
        return outputStream != null;
    }

    public String getAddress() { return address; }
}
