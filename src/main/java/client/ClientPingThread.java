package client;

import java.io.IOException;
import java.net.SocketException;

import static java.lang.System.currentTimeMillis;

/**
 * The PingThread class represents a thread responsible for sending periodic PING messages
 * to the server to check for responsiveness.
 * If no response is received within the specified maximum response time, the client is logged out.
 * This class extends Thread and overrides the run method to define the thread's behavior.
 *
 * @author Pascal von Fellenberg
 */

public class ClientPingThread extends Thread {
  private final Client parent; // the Client associated with this PingThread
  private final long maxResponseTimeMillis; // the maximum response time allowed for a PING message, in milliseconds
  private static final long PING_INTERVALL = 1000; // the time between PING messages, in milliseconds
  public long timeLastResponse; // the time of the last PING message, in milliseconds
  public volatile boolean receivedResponse; // indicates whether a response has been received

  /**
   * Constructs a new PingThread instance.
   *
   * @param parent                the EchoClient associated with this PingThread
   * @param maxResponseTimeMillis the maximum response time allowed for a PING message, in milliseconds
   */

  public ClientPingThread(Client parent, int maxResponseTimeMillis) {
    this.parent = parent;
    this.maxResponseTimeMillis = maxResponseTimeMillis;
    timeLastResponse = currentTimeMillis();
  }

  /**
   * The run method of the PingThread.
   * This method defines the behavior of the thread, which involves sending PING messages
   * to the server at regular intervals and handling responses.
   * If no response is received within the specified maximum response time, the client is logged out.
   */
  @Override
  public void run() {
    try {
      while (true) {
        try {
          synchronized(this) {
            receivedResponse = false;
          }
          parent.send("PING");
        } catch (SocketException e) { // passiert wahrscheinlich, wenn das Socket geschlossen worden ist..
          System.out.println("the socket has been closed");
          break;//TODO Handle this exception
        }
        Thread.sleep(PING_INTERVALL);
        if (receivedResponse) {
          timeLastResponse = currentTimeMillis();
          continue;
        }
        if (timeLastResponse - currentTimeMillis() >= maxResponseTimeMillis) {
          System.out.println("timeout of " + (double) maxResponseTimeMillis / 1000.0 + " seconds has been exceeded. ");
          parent.logout();
        }
      }
    } catch (IOException | InterruptedException e) {
      System.out.println("Your connection to the server has been lost");
      System.exit(0);
    }
  }
}
