import java.io.*;
import java.net.Socket;

public class EchoClient {
  public static void main(String[] args) {
    try {
      Socket sock = new Socket(args[0], Integer.parseInt(args[1]));
      InputStream in = sock.getInputStream();
      OutputStream out = sock.getOutputStream();
      InThread th = new InThread(in);
      Thread iT = new Thread(th);
      iT.start();

      BufferedReader conin = new BufferedReader(new InputStreamReader(System.in));
      String line = " ";
      while (true) {
        line = conin.readLine();
        if (line.equalsIgnoreCase("QUIT")) {
          break;
        }
        out.write(line.getBytes());
        out.write("\r\n".getBytes());
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
