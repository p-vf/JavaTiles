package Server;

/**
 * This enum represents the possible request types in the server-client protocol.
 * It defines the different types of requests that the client can send to the server.
 *
 * @author Pascal von Fellenberg
 * @author Istref Uka
 */
public enum ProtocolRequest {
  LOGI,
  LOGO,
  STAT,
  DRAW,
  PUTT,
  PWIN,
  EMPT,
  CATC,
  CATS,
  PING,
  NAME,
}
