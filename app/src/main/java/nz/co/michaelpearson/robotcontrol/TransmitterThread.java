package nz.co.michaelpearson.robotcontrol;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class TransmitterThread extends Thread {
    private DataReference dataReference;
    private static final byte[] data = new byte[6];
    private int period;
    private boolean stop = false;
    private String ipAddress;
    private int port;
    private EventCallback<String> errorCallback;
    private Handler handler;

    public TransmitterThread(String ipAddress, int port, int frequency, DataReference dataReference, EventCallback<String> errorCallback, Handler handler) {
        this.dataReference = dataReference;
        this.period = 1 / frequency;
        this.ipAddress = ipAddress;
        this.port = port;
        this.errorCallback = errorCallback;
        this.handler = handler;
    }

    @Override
    public void run() {
        Socket socket;
        OutputStream outputStream;
        try {
            socket = new Socket(ipAddress, port);
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.e("Transmitter", "Could not connect");

            handler.post(new Runnable() {
                @Override
                public void run() {
                    errorCallback.callback("Could not connect to robot");
                }
            });
            return;
        }
        data[0] = dataReference.getX1();
        data[1] = dataReference.getY1();
        data[2] = dataReference.getX2();
        data[3] = dataReference.getY2();
        data[4] = dataReference.getSpeed();
        data[5] = dataReference.getButtons();
        while(socket.isConnected() && !isInterrupted() && !stop) {
            try {
                outputStream.write(data);
                sleep(period);
            } catch (IOException | InterruptedException e) {
                return;
            }
        }
    }

    public void stopTransmitting() {
        stop = true;
    }
}