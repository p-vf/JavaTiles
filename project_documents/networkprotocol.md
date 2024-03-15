LOGI <n> <Nickname>             von Client
+OK                     
    Login auf Server in Lobby n mit Spielername Nickname.

LOGO <Nickname>                 von Client
+OK
    Logout vom Server des Spielers mit dem Nickname.

STAT <n>                        von Client
+OK
    Der Spielstatus wird dem Client übergeben.

DRAW <stack>                    von Client
+OK <tile>
    Eine Karte vom Stack aufheben, gibt die Tile zurück.

PUTT <tile> <deck>              von Client
+OK <won?>
    Nachdem der Spieler dran war, wird die Tile an den Nachbarstapel weitergegeben, 
    zudem wird das Deck an den Server geschickt, welcher dann dieses überprüft. 

CATC <whisper?> "<msg>" (<n>)?  von Client
+OK
    
CATS <whisper?> "<mug>" (<n>)? <Nickname> von Server
+OK
