package Client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ClientPingThread implements Runnable {
    private OutputStream out;
    private int interval;
    public ClientPingThread(OutputStream out, int interval) {
        this.out = out;
        this.interval = interval;
    }

    @Override
    public void run() {
        try {
            while (true) {
                out.write("PING".getBytes());
                Thread.sleep(interval);
            }

        } catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

