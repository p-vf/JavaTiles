package Client;

import java.io.IOException;
import java.io.InputStream;

public class InThread implements Runnable {
  InputStream in;
  public InThread(InputStream in) {
    this.in = in;
  }
  public void run() {
    int len;
    byte[] b = new byte[100];
    try {
      while (true) {
        if ((len = in.read(b)) == -1) {
          break;
        }
        String message =new String(b, 0, len);
        // Comment the line below to display PING messages
        if(!message.trim().equals("PING")||!message.trim().equals("+PING")){ //trim(), to remove the leading and trailing whitespace
          System.out.write(b,0,len);
        }
      }
    } catch (IOException e) {
      System.err.println(e.toString());
      System.exit(1);
    }
  }
}
