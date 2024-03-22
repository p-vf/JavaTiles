package Server;

import java.io.IOException;

import static java.lang.System.currentTimeMillis;

public class PingThread implements Runnable {
  private SyncOutputStreamHandler syncOut;
  private long maximalResponseTimeMillis;
  private long lastRequestTimeMillis;
  public PingThread(SyncOutputStreamHandler syncOut, int maximalResponseTimeMillis) {
    this.syncOut = syncOut;
    this.maximalResponseTimeMillis = maximalResponseTimeMillis;
  }

  @Override
  public void run() {
    try {
      while (true) {
        syncOut.writeData("PING\r\n".getBytes());
        lastRequestTimeMillis = currentTimeMillis();
        wait(maximalResponseTimeMillis);
        long timeWaited = currentTimeMillis() - lastRequestTimeMillis;
        if (timeWaited >= maximalResponseTimeMillis) {
          // TODO refactor in a way such that this thread can log out the client and log out here
          //logout();
        }
      }
    } catch(IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
