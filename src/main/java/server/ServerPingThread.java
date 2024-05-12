package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.SocketException;

import static java.lang.System.currentTimeMillis;

/**
 * This class provides a thread that is responsible for sending periodic PING messages
 * to a client and check for responsiveness.
 *
 * @author Pascal von Fellenberg
 * @author Istref Uka
 */
public class ServerPingThread extends Thread {
  private static final Logger LOGGER = LogManager.getLogger(ServerPingThread.class);
  private final ClientThread parent;
  private final long maxResponseTimeMillis;
  private static final long PING_INTERVALL = 1000;
  private long timeLastResponse;

  // TODO maybe change this field to private
  volatile boolean receivedResponse;

  /**
   * Constructs a new ServerPingThread instance that periodically sends PING messages to the associated client.
   * This thread helps in determining if the client connection is still alive based on the response time.
   * If the PING response from the client exceeds the specified maximum response time, appropriate action
   * is taken by the parent EchoClientThread.
   *
   * @param parent                The parent ClientThread that manages the connection to the client.
   * @param maxResponseTimeMillis The maximum time in milliseconds to wait for a response to a PING message.
   *                              If this time is exceeded, the corresponding client is logged out.
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
          synchronized (this) {
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
          LOGGER.info("Client # " + parent.getId() + " with nickname \"" + parent.getNickname() + "\" is being logged out because the timeout of " + (double) maxResponseTimeMillis / 1000.0 + " seconds has been exceeded.");
          parent.logout();
        }
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
