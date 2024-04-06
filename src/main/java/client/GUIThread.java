package client;

import utils.NetworkUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;


public class GUIThread extends JFrame implements Runnable{

            private JFrame frame;
            private JTextArea chat;
            private JTextField textField;
            private JScrollPane scroll;
            private Border border;

            private Client client;


 public GUIThread(Client client){
     this.client = client;
 }

    public void updateChat(String message) {
        chat.append(message + "\n");
    }

    public void run() {


                frame = new JFrame("Chat");
                chat = new JTextArea(20,50);
                textField = new JTextField();
                scroll = new JScrollPane(chat, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2), // Outer border
                BorderFactory.createEmptyBorder(5, 5, 5, 5) );// Inner paddin

                chat.setSize(540, 400);
                chat.setLocation(30,5);
                textField.setSize(540, 30);
                textField.setLocation(18, 500);
                textField.setBorder(border);
                frame.setResizable(false);
                frame.setSize(600, 600);
                frame.add(textField);
                frame.add(scroll);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
                chat.append("Chats: \n");
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


            private void sleep(int x) {
                try {
                    Thread.sleep(x);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }}




