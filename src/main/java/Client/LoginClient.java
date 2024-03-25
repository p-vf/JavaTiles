package Client;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The LoginClient class enables user login functionality by prompting the user to set a username and lobby number.
 *
 * @author Boran Gökcen
 * @author Robin Gökcen
 */
public class LoginClient {
    private final Scanner scanner = new Scanner(System.in);


    /**
     * Prompts the user to set a username.
     * If the username is longer than 15 characters, it prompts the user again until a valid username is provided.
     * If the username is empty, it sets the username to the system's username (whoami).
     *
     * @return the username set by the user
     */
    public String setUsername() {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        while (username.length() > 15) {
            System.out.println("Name zu lang:");
            username = scanner.nextLine();
        }
        if (username.isEmpty()) {
            username = System.getProperty("user.name"); //whoami


        }
        return username;
    }

    /**
     * Prompts the user to set a lobby number.
     * It validates whether the input is a valid number and prompts the user again until a valid number is provided.
     *
     * @return the lobby number set by the user
     */
    public int setLobbyNumber() {
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
        return lobbyNumber;

    }
}




