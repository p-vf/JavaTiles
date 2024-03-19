import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){

        try{
            while (!serverSocket.isClosed()) {
               Socket sock = serverSocket.accept(); //Programm wird angehalten bis Client sich verbindet, danach wird Socket returned.
               System.out.println("New client connected.");
               Clienthandler clientHandler = new Clienthandler(sock);
               Thread thread = new Thread(clientHandler); //Parameter von Threads sind Klassen, die runnable implementieren.
               thread.start();
            }}catch(IOException ioe){

            }
        
    }
    public void CloseServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();

    }
}
