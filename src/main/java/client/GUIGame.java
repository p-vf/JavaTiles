package client;

import javax.swing.*;
import javax.swing.border.Border;
import java.io.IOException;

public class GUIGame extends JFrame implements Runnable {

    private JFrame frame;
    private JScrollPane scroll;
    private Border border;

    private Client client;

    public GUIGame(Client client) {
        this.client = client;
    }
    @Override
    public void run() {
        frame = new JFrame("JavaTiles");
    }


}
