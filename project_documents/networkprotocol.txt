Oft verwendete Parameter und ihre Einschränkungen:
<nickname>,<whisperrecipient>: Spielername ohne Leerschlag, mit maximaler Länge 15. (stellt Spielername dar)
<msg>: Mit Anführungszeichen begrenzte Zeichenfolge, welche Anführungszeichen mit \" darstellt. (Stellt Nachricht dar)
<n>: Ganze Zahl in Dezimaldarstellung. (stellt Lobbynummer dar)
<drawstack>: entweder "m" oder "p" (stellt entweder Hauptstapel oder Nachbarsstapel dar)
<tile>: eine Repräsentation eines Spielsteins 
<gamestate>: eine Repräsentation des Spielzustandes
TODO spezifizieren der Darstellung von <tile> und <gamestate>
TODO entscheiden, wann Lobbynummer und Nickname wirklich gebraucht werden.

LOGI <n> <nickname>                       von Client
+OK                     
    Login auf Server in Lobby n mit Spielername nickname.

LOGO <nickname>                           von Client
+OK
    Logout vom Server des Spielers mit dem nickname.

STAT <n>                                  von Client
+OK <gamestate>
    Der Spielstatus wird dem Client übergeben. 

DRAW <drawstack>                              von Client
+OK <tile>
    Eine Karte vom Stack aufheben, gibt die Tile zurück.

PUTT <tile> <deck>                        von Client
+OK <won>
    Nachdem der Spieler dran war, wird die Tile an den Nachbarstapel weitergegeben, 
    zudem wird das Deck an den Server geschickt, welcher dann dieses überprüft. 
    Hat der Spieler gewonnen, wird in der Antwort der Parameter won auf "t" gesetzt 
    und das Spiel wird beendet, ansonsten auf "f" gesetzt. 
    Wird das Spiel durch einen Sieg beendet, muss der Server einen PWIN-Command an alle
    in der betroffenen Lobby schicken. 


PWIN <n>? <nickname>                      von Server
+OK
    Command wird gesendet, wenn das Spiel vom Spieler nickname gewonnen wird. 

EMPT <n>?                                 von Server
+OK
    Command wird vom Server gesendet, wenn der Hauptstapel leer ist, und somit das
    Spiel mit einem Unentschieden beendet werden muss. 


CATC <whisper?> "<msg>" <n> <nickname> [<whisperrecipient>]           von Client
+OK
    Der CATC-Command wird vom Client an den Server geschickt, wenn der User eine Nachricht in den Chat schicken will.
    Der Parameter nickname gibt an, von wem die Nachricht kommt. 
    Der Server muss dann einen CATS-Command an alle/einen Client/s schicken.  
    Sie enthält einen Parameter whisper, der entweder "t" oder "f" (für true oder false) ist. Falls whisper "t" ist, muss der
    optionale Parameter whisperrecipient definiert werden, der der Spieler ist, an den sie gerichtet ist. 
    der Parameter msg ist der Inhalt der Nachricht, dargestellt als String. 
    Anführungszeichen in msg müssen mit Backslash gekennzeichnet werden.
    Beispiel:
        CATC t "Tom hat mir folgendes gesagt: \"Ich bin dumm\"" 2 istref robin

CATS <whisper?> "<msg>" <n> <Nickname> [<whisperrecipient>] von Server
+OK
    In dieser Command wird die Nachricht eines Clients an einen oder mehrere Clients in der Lobby n weitergeleitet. 
    Die Nachricht wird nur an alle Clients in der Lobby weitergeleitet, wenn whisper "f" ist. 
    Die Nachricht in msg muss so aussehen wie bei CATC. 
    Beispiel:
        CATS t "Ich werde gewinnen heheheh" 2 robin boran

PING <timestamp> <ID>                     von Server / Client
PONG <timestamp> <ID>
    Die Ping-Nachricht wird gesendet, um Verbindungsunterbrüche zu testen. 
    Wird eine Ping-Nachricht erhalten, muss eine Pong-Nachricht zurückgeschickt werden. 
    Die Ping- und Pong-Nachrichten enthalten jeweils einen timestamp, in dem die Zeit des 
    Abschickens der Nachricht festgehalten wird, und eine ID, welche bei jeder Ping-Nachricht eindeutig ist. 
    Die Pong-Nachricht muss die gleiche ID, wie die Ping-Nachricht, auf die sie antwortet, haben. 
    Frage: Muss die PING-Nachricht vom Client und vom Server oder nur vom Server geschickt werden??