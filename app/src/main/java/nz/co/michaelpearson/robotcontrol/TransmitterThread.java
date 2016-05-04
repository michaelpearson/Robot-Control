package nz.co.michaelpearson.robotcontrol;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TransmitterThread extends Thread {
    private static final byte[] data = new byte[6];

    private int period;
    private boolean stop = false;
    private String ipAddress;
    private int port;
    private int speed;
    private int buttons;

    private EventCallback<String> errorCallback;
    private Handler handler;
    private JoystickView[] joysticks;

    public TransmitterThread(String ipAddress,
                             int port,
                             int frequency,
                             EventCallback<String> errorCallback,
                             JoystickView[] joysticks,
                             int speed,
                             int buttons) {
        this.period = 1000 / frequency;
        this.ipAddress = ipAddress;
        this.port = port;
        this.errorCallback = errorCallback;
        this.handler = new Handler();
        this.joysticks = joysticks;
        this.speed = speed;
        this.buttons = buttons;
    }

    @Override
    public void run() {
        OutputStream outputStream;
        Socket socket;
        try {
            socket = new Socket(ipAddress, port);
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            raiseError("Could not connect to robot");
            return;
        }

        while(!isInterrupted() && !stop) {
            data[0] = (byte)((int)(joysticks[0].getJoystickX() * -127 + 127) & 0xFF);
            data[1] = (byte)((int)(joysticks[0].getJoystickY() * 127 + 127) & 0xFF);
            data[2] = (byte)((int)(joysticks[1].getJoystickX() * -127 + 127) & 0xFF);
            data[3] = (byte)((int)(joysticks[1].getJoystickY() * 127 + 127) & 0xFF);
            data[4] = (byte)(speed & 0xFF);
            data[5] = (byte)(buttons & 0xFF);
            buttons = 0;
            try {
                outputStream.write(data);
                outputStream.flush();
                sleep(period);
            } catch (IOException | InterruptedException e) {
                raiseError("Connection ended");
                stopTransmitting();
            }
        }
        try {
            socket.getOutputStream().close();
            socket.getInputStream().close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setButtons(int buttons) {
        this.buttons |= (byte)buttons;
    }

    private void raiseError(final String message) {
        if(!stop) {
            Log.e("Transmitter", message);
            if(handler != null && errorCallback != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        errorCallback.callback(message);
                    }
                });
            }
        }
    }

    public void stopTransmitting() {
        stop = true;
        handler = null;
        interrupt();
    }
}