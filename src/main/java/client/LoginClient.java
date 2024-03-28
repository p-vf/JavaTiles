package client;

import java.util.Scanner;

/**
 * The LoginClient class enables user login functionality by prompting the user to set a username and lobby number.
 *
 * @author Boran Gökcen
 * @author Robin Gökcen
 */
public class LoginClient {
    /**
     * Prompts the user to set a username.
     * If the username is longer than 15 characters, it prompts the user again until a valid username is provided.
     * If the username is empty, it sets the username to the system's username (whoami).
     *
     * @return the username set by the user
     */
    public String setUsername() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        while (username.length() > 15) {
            System.out.println("Name zu lang:");
            username = scanner.nextLine();
        }
        if (username.isEmpty()) {
            username = System.getProperty("user.name"); //whoami

            scanner.close();


        }
        return username;
    }


}




