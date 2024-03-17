package Client;
import java.util.Scanner;
public class LoginClient {
    private final Scanner scanner = new Scanner(System.in);


    public void closeScanner(){
        scanner.close();
    }

    public String enterUsername() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter username:");
        return scanner.nextLine();
    }
    public int enterLobbyNumber(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter lobby number:");
        return scanner.nextInt();

    }

    public static void main(String[] args){
        LoginClient login = new LoginClient();
        System.out.println(login.enterUsername());
        System.out.println(login.enterLobbyNumber());
        login.closeScanner();
    }



}
