package Server;

import java.io.IOException;
import java.io.OutputStream;

public class SyncOutputStreamHandler {
  private OutputStream out;

  public SyncOutputStreamHandler(OutputStream out) {
    this.out = out;
  }
  public synchronized void writeData(byte[] data) throws IOException {
    out.write(data);
  }
  public void close() throws IOException {
    out.close();
  }
}


