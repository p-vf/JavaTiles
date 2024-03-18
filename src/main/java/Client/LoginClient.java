package Client;
import java.util.InputMismatchException;
import java.util.Scanner;
public class LoginClient {
    private final Scanner scanner = new Scanner(System.in);


    public void closeScanner(){
        scanner.close();
    }

    public String enterUsername() {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        while(username.length()>15){
            System.out.println("Name zu lang:");
            username = scanner.nextLine();
        }
        if(username.isEmpty()){
            username = System.getProperty("user.name"); //whoami 


        }
        return username;
    }
    public int enterLobbyNumber() {
        System.out.println("Enter lobby number:");
        int lobbyNumber = 0;
        try{
        lobbyNumber = scanner.nextInt();}
        catch(InputMismatchException e){
            System.out.println("Geben Sie eine Zahl ein.");
        }
        return lobbyNumber;

    }



    public static void main(String[] args){
        LoginClient login = new LoginClient();
        System.out.println(login.enterUsername());
        System.out.println(login.enterLobbyNumber());
        login.closeScanner();
    }



}
