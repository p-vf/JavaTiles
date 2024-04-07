package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class ServerPingThread extends Thread {
  public static final Logger LOGGER = LogManager.getLogger(ServerPingThread.class);
  private final ClientThread parent;
  private final long maxResponseTimeMillis;
  private static final long PING_INTERVALL = 1000;
  public long timeLastResponse;
  public volatile boolean receivedResponse;

  /**
   * Constructs a new PingThread instance.
   * @param parent The parent EchoClientThread associated with this PingThread.
   * @param maxResponseTimeMillis The maximum response time allowed for a PING message, in milliseconds.
   */
  public ServerPingThread(ClientThread parent, int maxResponseTimeMillis) {
    this.parent = parent;
    this.maxResponseTimeMillis = maxResponseTimeMillis;
    timeLastResponse = currentTimeMillis();
  }

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
          LOGGER.info("Socket has been closed");
          break; // TODO Handle this exception
        }

        Thread.sleep(PING_INTERVALL);
        if (receivedResponse) {
          timeLastResponse = currentTimeMillis();
          continue;
        }
        if (timeLastResponse - currentTimeMillis() >= maxResponseTimeMillis) {
          LOGGER.info("Client # " + parent.id + " with nickname \"" + parent.nickname + "\" is being logged out because the timeout of "+ (double)maxResponseTimeMillis/1000.0 + " seconds has been exceeded.");
          parent.logout();
        }
      }
    } catch(IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
