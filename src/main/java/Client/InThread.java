package Client;

import java.io.IOException;
import java.io.InputStream;

import static Client.EchoClient.handleRequest;

/**
 * The InThread class represents a thread responsible for handling incoming messages from the server.
 * It reads messages from the input stream and processes them accordingly.
 */
public class InThread implements Runnable {

  public EchoClient client; // The associated client object
  InputStream in; // The input stream to read messages from


  /**
   * Constructs a new InThread with the given input stream and client.
   *
   * @param in the input stream to read messages from
   * @param client the associated client object
   */
  public InThread(InputStream in, EchoClient client) {
    this.in = in;
    this.client = client;
  }

  /**
   * Runs the thread to continuously read messages from the input stream and handle them.
   * It delegates the handling of messages to the handleRequest method in EchoClient class.
   */
  public void run() {
    int len;
    byte[] b = new byte[100];
    try {
      while (true) {
        if ((len = in.read(b)) == -1) {
          break;
        }
        String request =new String(b, 0, len);
          handleRequest(request, client);
        }

    } catch (IOException e) {
      System.err.println(e.toString());
      System.exit(1); // Terminate the program on I/O error
    }
  }
}
