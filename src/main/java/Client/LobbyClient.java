import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class LobbyClient {

    private Socket socket;
    private BufferedReader bReader;
    private BufferedWriter bWriter;
    private String username;
    private int lobbyNumber;

    public LobbyClient(Socket socket, String username, int lobbyNumber) {
        try {
            this.socket = socket;
            this.bWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
            this.lobbyNumber = lobbyNumber;

            // Send the username and lobby number to the server
            bWriter.write(username);
            bWriter.newLine();
            bWriter.flush();
            bWriter.write(Integer.toString(lobbyNumber));
            bWriter.newLine();
            bWriter.flush();

            // Inform the server about the new user in the lobby
            System.out.println("Logged in as: " + username + " in Lobby " + lobbyNumber);

        } catch (IOException e) {
            closeEverything(socket, bReader, bWriter);
        }
    }

    public void sendMessage() {
        try {
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bWriter.write(username + ": " + messageToSend);
                bWriter.newLine();
                bWriter.flush();
            }

        } catch (IOException e) {
            closeEverything(socket, bReader, bWriter);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            String msgFromServer;
            try {
                while (socket.isConnected()) {
                    msgFromServer = bReader.readLine();
                    System.out.println(msgFromServer);
                }
            } catch (IOException e) {
                closeEverything(socket, bReader, bWriter);
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        LoginClient login = new LoginClient();
        login.setUsername();
        login.setLobbyNumber();

        Socket socket = null;
        try {
            socket = new Socket("localhost", 1234); // Assuming the server is running locally
        } catch (IOException e) {
            e.printStackTrace();
        }
        LobbyClient client = new LobbyClient(socket, login.getUsername(), login.getLobbyNumber());
        client.listenForMessage();
        client.sendMessage();
    }
}

class LoginClient {
    private final Scanner scanner = new Scanner(System.in);
    private static String username = System.getProperty("user.name");
    private int lobbyNumber = 0;

    public void closeScanner() {
        scanner.close();
    }

    public static String getUsername() {
        return username;
    }

    public int getLobbyNumber() {
        return this.lobbyNumber;
    }

    public void setUsername() {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        while (username.length() > 15) {
            System.out.println("Name too long:");
            username = scanner.nextLine();
        }
        if (username.isEmpty()) {
            username = System.getProperty("user.name");
        }
        this.username = username;
    }

    public void setLobbyNumber() {
        System.out.println("Enter lobby number:");
        int lobbyNumber = 0;
        try {
            lobbyNumber = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Enter a number.");
        }
        this.lobbyNumber = lobbyNumber;
    }
}
