package Client;

import java.util.InputMismatchException;
import java.util.Scanner;

public class LoginClient {
    private final Scanner scanner = new Scanner(System.in);
    private String username = System.getProperty("user.name");
    private int lobbyNumber = 0;

    

    public void closeScanner(){
        scanner.close();
    }

    public String getUsername(){
        return this.username;
    }

    public int getLobbyNumber(){
        return this.lobbyNumber;
    }
    public void setUsername() {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        while(username.length()>15){
            System.out.println("Name zu lang:");
            username = scanner.nextLine();
        }
        if(username.isEmpty()){
            username = System.getProperty("user.name"); //whoami


        }
        this.username= username;
    }
    public void setLobbyNumber() {
        System.out.println("Enter lobby number:");
        int lobbyNumber = 0;
        try {
            lobbyNumber = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Geben Sie eine Zahl ein.");
        }
        this.lobbyNumber = lobbyNumber;

    }



    public static void main(String[] args){
        LoginClient login = new LoginClient();
        System.out.println(login.getUsername());
        System.out.println(login.getLobbyNumber());
        login.setUsername();
        login.setLobbyNumber();
        System.out.println(login.getUsername());
        System.out.println(login.getLobbyNumber());
    }



}
