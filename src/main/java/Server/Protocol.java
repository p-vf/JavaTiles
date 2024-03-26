package Server;

/**
 * This enum represents the possible response and request types in the server-client protocol that the server must be able to handle.
 *
 * @author Pascal von Fellenberg
 * @author Istref Uka
 */
public class Protocol {
  // for a more detailed description of the commands, see product_documents/networkprotocol.md

  // these are the requests the server can get according to the protocol
  public enum Request {
    // log in:
    LOGI,

    // log out:
    LOGO,

    // fetch gamestate:
    STAT,

    // draw tile:
    DRAW,

    // put tile on stack:
    PUTT,

    // send message to Chat:
    CATC,

    // check if connection is still working:
    PING,

    // change name:
    NAME,

    // list lobbies:
    LLOB,

    // join lobby:
    JLOB,

  }

  // these are the responses the server can get according to the protocol
  public enum Response {
    // when the game ends due to a player win:
    PWIN,

    // when the game ends due to a draw:
    EMPT,

    // chat message distribution:
    CATS,

    // check if connection is still working:
    PING,
  }
}
