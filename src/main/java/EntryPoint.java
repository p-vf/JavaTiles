import Client.Client;

public class EntryPoint {
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Fatal: zu wenig Argumente gegeben;");
      return;
    }
    switch (args[0]) {
      case "client" -> {
        String[] arguments = args[1].split(":");
        if (args.length == 3) {
          arguments = new String[] { arguments[0], arguments[1], args[2] };
        }
        Client.main(arguments);
      }
      case "server" -> Server.Server.main(new String[] {args[1]});
      default -> System.out.println("Fatal: Das erste Argument muss entweder \"client\" oder \"server\" sein.");
    }
    // TODO maybe handle incorrect IP-address and port
  }
}
