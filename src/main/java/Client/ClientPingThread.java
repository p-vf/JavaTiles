package Client;

import java.io.IOException;

public class ClientPingThread implements Runnable {
    private SyncOutputStreamHandler syncOut;
    private int interval;
    public ClientPingThread(SyncOutputStreamHandler syncOut, int interval) {
        this.syncOut = syncOut;
        this.interval = interval;
    }

    @Override
    public void run() {
        try {
            while (true) {
                syncOut.writeData("PING\r\n".getBytes());
                Thread.sleep(interval);
            }

        } catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
