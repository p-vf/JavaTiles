package Client;

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
public class PingThread implements Runnable {
    private final EchoClient client;
    private final long maxResponseTimeMillis;
    private long lastRequestTimeMillis;

    /**
     * Constructs a new PingThread instance.
     * @param client The parent EchoClientThread associated with this PingThread.
     * @param maxResponseTimeMillis The maximum response time allowed for a PING message, in milliseconds.
     */
    public PingThread(EchoClient client, int maxResponseTimeMillis) {
        this.client = client;
        this.maxResponseTimeMillis = maxResponseTimeMillis;
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    client.send("PING");
                } catch (SocketException e) { // passiert wahrscheinlich, wenn das Socket geschlossen worden ist..
                    break;//TODO Handle this exception
                }

                lastRequestTimeMillis = currentTimeMillis();
                synchronized(this) {
                    wait(maxResponseTimeMillis);
                }
                long timeWaited = currentTimeMillis() - lastRequestTimeMillis;
                if (timeWaited >= maxResponseTimeMillis) {
                    //client.logout();
                }
            }
        } catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
