package nz.co.michaelpearson.robotcontrol;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class TransmitterThread extends Thread {
    private Socket socket;
    private OutputStream outputStream;
    private DataReference dataReference;
    private static final byte[] data = new byte[6];
    private int period;
    private boolean stop = false;

    public TransmitterThread(Socket socket, DataReference dataReference, int frequency) throws IOException {
        this.dataReference = dataReference;
        this.socket = socket;
        this.outputStream = socket.getOutputStream();
        this.period = 1 / frequency;
    }

    @Override
    public void run() {
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