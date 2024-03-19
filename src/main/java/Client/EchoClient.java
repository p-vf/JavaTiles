package Client;

import java.io.*;
import java.net.Socket;

public class EchoClient {

  private static final int PING_TIMEOUT=15000;
  public static SyncOutputStreamHandler syncOut;

  public static void main(String[] args) {
    try {
      Socket sock = new Socket(args[0], Integer.parseInt(args[1]));
      InputStream in = sock.getInputStream();
      OutputStream out = sock.getOutputStream();
      syncOut = new SyncOutputStreamHandler(out);
      InThread th = new InThread(in);
      Thread iT = new Thread(th);
      iT.start();

      Thread cpthread = new Thread(new ClientPingThread(syncOut, 10000));
      cpthread.start();

      sock.setSoTimeout(PING_TIMEOUT);



      BufferedReader conin = new BufferedReader(new InputStreamReader(System.in));
      String line = " ";
      while (true) {
        line = conin.readLine();
        if (line.equalsIgnoreCase("QUIT")) {
          break;
        }
        syncOut.writeData(line.getBytes());
        syncOut.writeData("\r\n".getBytes());

      }

      System.out.println("terminating...");
      in.close();
      out.close();
      sock.close();
    } catch(IOException e) {

        System.err.println(e.toString());
        System.exit(1);
      }
    }
  }


