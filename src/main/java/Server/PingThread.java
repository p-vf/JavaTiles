package Server;

import java.io.IOException;
import java.net.SocketException;

import static java.lang.System.currentTimeMillis;

public class PingThread implements Runnable {
  private final EchoClientThread parent;
  private final long maxResponseTimeMillis;
  private long lastRequestTimeMillis;
  public PingThread(EchoClientThread parent, int maxResponseTimeMillis) {
    this.parent = parent;
    this.maxResponseTimeMillis = maxResponseTimeMillis;
  }

  @Override
  public void run() {
    try {
      while (true) {
        try {
          parent.send("PING");
        } catch (SocketException e) { // passiert wahrscheinlich, wenn das Socket geschlossen worden ist..
          break;
        }

        lastRequestTimeMillis = currentTimeMillis();
        synchronized(this) {
          wait(maxResponseTimeMillis);
        }
        long timeWaited = currentTimeMillis() - lastRequestTimeMillis;
        if (timeWaited >= maxResponseTimeMillis) {
          parent.logout();
        }
      }
    } catch(IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
