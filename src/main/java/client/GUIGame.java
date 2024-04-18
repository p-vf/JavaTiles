package client;

import server.Lobby;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import static utils.NetworkUtils.encodeProtocolMessage;

public class GUIGame {

    private JFrame frame;
    private JScrollPane scroll;
    private Border border;

    private Client client;

    public GUIGame(Client client) {
        this.client = client;
    }

    public void start() {
        frame = new JFrame("GameJavaTiles");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        JPanel panel = new JPanel();
        JButton button = new JButton("Click me");
        JButton button2 = new JButton("create Lobby");
        panel.add(button2);
        frame.add(panel);

        button2.addActionListener(e -> {
            try {

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        button.addActionListener(e ->{System.out.println("Button clicked");});
        panel.add(button);
        frame.add(panel);
        border = BorderFactory.createLineBorder(Color.BLACK, 1, true);
        frame.setVisible(true);
        frame.pack();
    }

    public void commandToServer(String input) throws IOException {
        client.handleInput(input);
    }

}

