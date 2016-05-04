package nz.co.michaelpearson.robotcontrol;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
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
    private Socket socket;

    private EventCallback<String> errorCallback;
    private Handler handler;
    private JoystickView[] joysticks;

    public TransmitterThread(String ipAddress,
                             int port,
                             int frequency,
                             EventCallback<String> errorCallback,
                             Handler handler,
                             JoystickView[] joysticks,
                             int speed,
                             int buttons) {
        this.period = 1 / frequency;
        this.ipAddress = ipAddress;
        this.port = port;
        this.errorCallback = errorCallback;
        this.handler = handler;
        this.joysticks = joysticks;
        this.speed = speed;
        this.buttons = buttons;
    }

    @Override
    public void run() {
        OutputStream outputStream;
        try {
            socket = new Socket(ipAddress, port);
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            raiseError("Could not connect to robot");
            return;
        }

        while(!isInterrupted() && !stop) {
            data[0] = (byte)(joysticks[0].getxPosition() & 0xFF);
            data[1] = (byte)(joysticks[0].getyPosition() & 0xFF);
            data[2] = (byte)(joysticks[1].getxPosition() & 0xFF);
            data[3] = (byte)(joysticks[1].getyPosition() & 0xFF);
            data[4] = (byte)(speed & 0xFF);
            data[5] = (byte)(buttons & 0xFF);
            try {
                outputStream.write(data);
                sleep(period);
            } catch (IOException | InterruptedException e) {
                raiseError("Connection ended");
                stopTransmitting();
            }
        }
        try {
            Socket s = socket;
            if(s != null) {
                s.getOutputStream().close();
                s.getInputStream().close();
                s.close();
                socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void raiseError(final String message) {
        Log.e("Transmitter", message);
        handler.post(new Runnable() {
            @Override
            public void run() {
                errorCallback.callback(message);
            }
        });
    }

    public void stopTransmitting() {
        stop = true;
        interrupt();
    }
}