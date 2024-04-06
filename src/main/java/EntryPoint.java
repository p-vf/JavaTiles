import client.Client;

/**
 * This class is used to start either the client or the server, depending on the arguments given to the main method.
 * The arguments have to be in one of the following forms: either {@code {"client", "<ipaddress>:<port>"}} or {@code {"server", "<port>"}}.
 * Depending on the arguments, a server or a client is started.
 */
public class EntryPoint {
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
