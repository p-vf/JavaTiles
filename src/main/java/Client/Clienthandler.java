import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.Buffer;


public class Clienthandler implements Runnable{


    public static ArrayList<Clienthandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bReader;
    private BufferedWriter bWriter;
    private String clientUsername;

    public Clienthandler(Socket socket){
        try{
            this.socket = socket;
            this.bWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("Server: "+ clientUsername + " has entered the chat!");

        }catch(IOException e){
            closeEverything(socket, bReader, bWriter);

        }
    }


    @Override
    public void run() {
        String messageFromClient;

        while(socket.isConnected()){
            try {
                messageFromClient = bReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                closeEverything(socket, bReader, bWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend){
        for(Clienthandler clientHandler: clientHandlers){
            try{
                if(!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bWriter.write(messageToSend);
                    clientHandler.bWriter.newLine();
                    clientHandler.bWriter.flush();
                }

            }catch(IOException e){
                closeEverything(socket,bReader,bWriter);

            }
        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("Server: "+ clientUsername + " has left the chat");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
        try{
            if(bufferedReader != null ){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }

    }
}