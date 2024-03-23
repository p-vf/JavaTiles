package Server;

import Client.EchoClient;

import java.io.IOException;
import java.net.SocketException;

import static java.lang.System.currentTimeMillis;

public class PingThread implements Runnable {
  private EchoClientThread parent;
  private long maximalResponseTimeMillis;
  private long lastRequestTimeMillis;
  public PingThread(EchoClientThread parent, int maximalResponseTimeMillis) {
    this.parent = parent;
    this.maximalResponseTimeMillis = maximalResponseTimeMillis;
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
          wait(maximalResponseTimeMillis - 5000); // TODO remove -5000
        }
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
