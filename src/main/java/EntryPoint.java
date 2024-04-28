import client.Client;

/**
 * This class is used to start either the client or the server, depending on the arguments given to the main method.
 * The arguments have to be in one of the following forms: either {@code {"client", "<ipaddress>:<port>"}} or {@code {"server", "<port>"}}.
 * Depending on the arguments, a server or a client is started.
 */
public class EntryPoint {

  /**
   * This method is the main method of the application. It takes an array of strings as input, and depending on the first element of the array, it starts the client or the server.
   * If the input array is empty or has less than 2 elements, an error message is printed to the console and the application exits with a status code of 1.
   * If the first element of the input array is "client", the method splits the second element of the array (which represents the IP address and port) at the colon (:) character, and passes the resulting array to the client main method. If there are 3 elements in the input array, the third element is used as the nickname.
   * If the first element of the input array is "server", the method passes the second element of the array (which represents the port) to the server main method.
   * If the first element of the input array is neither "client" nor "server", an error message is printed to the console and the application exits with a status code of 1.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    if (args.length < 2) {
      System.err.println("Not enough arguments given");
      System.exit(1);
    }
    switch (args[0]) {
      case "client" -> {
        String[] arguments = args[1].split(":");
        // for debugging only
        if (args.length == 3) {
          arguments = new String[] { arguments[0], arguments[1], args[2] };
        }
        Client.main(arguments);
      }
      case "server" -> server.Server.main(new String[] {args[1]});
      default -> {
        System.err.println("The first argument has to either be \"client\" or \"server\".");
        System.exit(1);
      }
    }
    // TODO maybe handle incorrect IP-address and port
  }
}
