package Server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class PingThread implements Runnable {
  private SyncOutputStreamHandler syncOut;
  private int interval;
  public PingThread(SyncOutputStreamHandler syncOut, int interval) {
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
