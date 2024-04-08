package client;

import utils.NetworkUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A graphical user interface (GUI) thread that extends JFrame and implements Runnable.
 * This class manages the GUI components for a chat interface within a JavaTiles application.
 * It provides methods to update the chat display and handle user input events.
 */
public class GUIThread extends JFrame implements Runnable{

            private JFrame frame;
            private JTextArea chat;
            private JTextField textField;
            private JScrollPane scroll;
            private Border border;

            private Client client;


    /**
     * Constructs a GUIThread object associated with a specific client instance.
     *
     * @param client The Client object used for sending messages and handling input.
     */
 public GUIThread(Client client){
     this.client = client;
 }


    /**
     * Appends a message to the chat display area.
     *
     * @param message The message to append to the chat.
     */
    public void updateChat(String message) {
        chat.append(message + "\n");
    }

    /**
     * Sets up and displays the graphical user interface (GUI) components for the chat.
     * This method is invoked when the GUI thread is started.
     */
    public void run() {


                frame = new JFrame("JavaTiles");
                chat = new JTextArea(20,50);
                textField = new JTextField();
                scroll = new JScrollPane(chat, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                border = BorderFactory.createLineBorder(Color.BLACK,1,true);
                chat.setEditable(false);
                chat.setSize(540, 400);
                chat.setLocation(30,5);
                textField.setSize(540, 30);
                textField.setLocation(18, 500);
                frame.setResizable(false);
                frame.setSize(600, 600);
                frame.add(textField);
                textField.setBorder(border);
                frame.add(scroll);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
                chat.append("Chats: \n  \n");
                textField.setText("");


                textField.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        String gtext = textField.getText();
                        System.out.println(gtext);
                        String message = "/chat"+" "+gtext;
                        try {
                            client.send(client.handleInput(message));
                        } catch (IOException e) {
                            chat.append("invalid input");
                        }
                        textField.setText("");
                        if(gtext.equals("QUIT")) {
                            sleep(500);
                            System.exit(0);
                        }
                        String category = "";
                        try {

                            System.out.println(category);
                        }
                        catch (Exception e) {
                            System.out.println("Exception thrown.");
                        }
                    }
                });


            }

    /**
     * Pauses the current thread for a specified duration.
     *
     * @param x The duration to sleep in milliseconds.
     */
            private void sleep(int x) {
                try {
                    Thread.sleep(x);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }}




