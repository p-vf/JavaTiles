package Server;

import java.io.IOException;
import java.net.SocketException;

import static java.lang.System.currentTimeMillis;

/**
 * The PingThread class represents a thread responsible for sending periodic PING messages
 * to a client to check for responsiveness.
 * 
 * @author Pascal von Fellenberg
 * @author Istref Uka
 */
public class PingThread extends Thread {
  private final EchoClientThread parent;
  private final long maxResponseTimeMillis;
  private static final long PING_INTERVALL = 1000;
  private long lastRequestTimeMillis;

  /**
   * Constructs a new PingThread instance.
   * @param parent The parent EchoClientThread associated with this PingThread.
   * @param maxResponseTimeMillis The maximum response time allowed for a PING message, in milliseconds.
   */
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
          System.out.println("Socket wurde geschlossen");
          break;//TODO Handle this exception
        }

        lastRequestTimeMillis = currentTimeMillis();
        synchronized (this) {
          wait(maxResponseTimeMillis);
        }
        long timeWaited = currentTimeMillis() - lastRequestTimeMillis;
        if (timeWaited >= maxResponseTimeMillis) {
          System.out.println("Logging Client " + parent.id + " out due to timeout.");
          parent.logout();
        }
        Thread.sleep(PING_INTERVALL);
      }
    } catch(IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
