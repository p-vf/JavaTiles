package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import static client.Client.handleRequest;
import static client.Client.handleResponse;


/**
 * The InThread class represents a thread responsible for handling incoming messages from the server.
 * It reads messages from the input stream and processes them accordingly.
 *
 * @author Boran Gökcen
 * @author Pascal von Fellenberg
 */
public class InThread implements Runnable {

  public static final Logger LOGGER = LogManager.getLogger();

  public Client client; // The associated client object

  public GUIThread guiThread;
  InputStream in; // The input stream to read messages from


  /**
   * Constructs a new InThread with the given input stream and client.
   *
   * @param in     the input stream to read messages from
   * @param client the associated client object
   */
  public InThread(InputStream in, Client client, GUIThread guiThread) {
    this.in = in;
    this.client = client;
    this.guiThread = guiThread;
  }

  /**
   * Runs the thread to continuously read messages from the input stream and handle them.
   * It delegates the handling of messages to the handleRequest method in EchoClient class.
   */
  public void run() {
    while (true) {
      String message;
      BufferedReader bufferRead = new BufferedReader(new InputStreamReader(in));
      try {
        message = bufferRead.readLine();
        // for debugging purposes: if you want to see pings, comment the if statement out
        if (!(message.equals("PING") || message.equals("+PING"))) {
          //LOGGER.debug("received: " + message);
        }
        if (message.charAt(0) != '+') {
          handleRequest(message, client, guiThread);

        } else {
          handleResponse(message, client, guiThread);
        }

      } catch (IOException e) {
        System.out.println("Sie wurden ausgeloggt.");
        System.exit(0); // Terminate the program on I/O error
      }
    }
  }
}