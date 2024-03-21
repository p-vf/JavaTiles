package Client;

import java.util.InputMismatchException;
import java.util.Scanner;

public class LoginClient {
    private final Scanner scanner = new Scanner(System.in);
    private String username = System.getProperty("user.name");
    private int lobbyNumber = 0;


    public void closeScanner() {
        scanner.close();
    }


    public String setUsername() {
        Scanner scanner2 = new Scanner(System.in);
        System.out.println("Enter username:");
        String username = scanner2.nextLine();
        while (username.length() > 15) {
            System.out.println("Name zu lang:");
            username = scanner2.nextLine();
        }
        if (username.isEmpty()) {
            username = System.getProperty("user.name"); //whoami


        }
        this.username = username;
        return username;
    }

    public int setLobbyNumber() {
        Scanner scanner3 = new Scanner(System.in);
        System.out.println("Enter lobby number:");
        int lobbyNumber = 0;
        while (true) {
            try {
                lobbyNumber = scanner.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Geben Sie eine Zahl ein.");
                scanner.nextLine();

            }
        }
        this.lobbyNumber = lobbyNumber;
        return lobbyNumber;

    }
}




