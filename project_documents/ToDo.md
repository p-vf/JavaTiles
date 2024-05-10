# ToDo
- (Istref) Funktionalität vom Client und vom Server beschreiben/nachfragen im Tutorat (Achievement "Networking")
- (Pascal) Requirement Analysis nachfragen
- (jemand oder alle?) Networking Code implementieren (Achievement: "Ahead of Schedule and Unter Budget!")
- Im Tutorat nachfragen, wie man Planen muss (Achievement "Who? What? When?" und "About a Game")
- Name der Gruppe festlegen

# Meilenstein 2: 25.03
- gitignore(Pascal)
- Server.Protocol definieren(alle) 
- Client Chat(alle)
- Nickname(alle) auf Client(muss Namen wählen) und Server(überprüft ob duplikat falls ja ändert)
- Client schlägt namen vor, falls kein Name eingegeben wurde(mit whoami)
- Login, Logout beliebig viele Leute
- Ping(Ping message regularly vom Server zu Client), Pong(Pong message regurlarly vom Client zu Server)
- !PingPong connection losses properly handled, Serververbindung abgebrochen. (Client zeigt an Verbindung abgebrochen)
- Bonus: Already have a gradle build script producing one executible jar
- (QA concept) <-- Fragen was das ist

# Fragen bis Tutorat 18.3.:
- Wo müssen die Decks gespeichert werden?
- Muss in der Chat-Nachricht im Protokoll auch der Sender angegeben werden?
- Wann muss der Ping austausch starten?
- Wie kann man von mehreren Threads auf einen OutputStream schreiben / von einem InputStream lesen?
- (follow up auf vorherige Frage) oder muss das problem anders gelöst werden?

# Fragen Tutorat 8.4.:
- Müssen wir accounts machen für das Scoreboard?

# TODOs bis 19.4.:
- networkprotocol.md aktualisieren (mit JOND)
Server:
    - LEFT command senden (wenn ein Spieler die Lobby verlässt)
    - NAMS command senden (am Anfang des Spiels und jedes mal, wenn sich die Namensliste ändert)
    - JOND command senden (wenn ein Spieler einer Lobby beitritt)
    - LLOB command abhandeln (vom Client, wenn er die Lobby verlassen will)
    - Das Game state sollte nicht in einem Broadcast geschickt werden, sondern nur an die Clients in einer Lobby.


Client:
    - Client nicht mehr nach username fragen (entweder das dritte Argument oder den Systemnamen nehmen)
    - LEFT command abhandeln (siehe networkprotocol.md)
    - LLOB command senden (siehe networkprotocol.md)
    - NAMS command abhandeln (siehe networkprotocol.md)
    - JOND command abhandeln (siehe networkprotocol.md)

# aktuelle TODOs:

was noch zu tun ist beim GUI:
    Das Netzwerkprotokoll betreffend:
    - Alle Highscores sollten angezeigt werden können. (mit scrolling, auch wenn man noch nicht in einer lobby ist)

Client:
    - Anweisung im Chat updaten (anstatt /all den broadcast toggle button zum Broadcast in die Anweisung nehmen)
    - (Tutorial einbauen (aktualisiertes Manual einfügen))
    - list open/run/finished games
    - revert changes of spectator

- Manual updaten
- QA Report machen (Diskussion und Graph aktualisieren)
- Video (zwischen 20 Sekunden und 2 Minuten) und Screenshot machen
- debug statements entfernen


Präsentation:
    - About a Game
    - Demo einüben
    - Logo
    - QA
    - Technology!
    - We are smarter now (lessons learned)

nicht so wichtig beim Client:
    - title vom Window auf "JavaTiles" setzen
    - anstatt FX-alien unser logo als icon in der Taskleiste

Montag in Übungsstunde:
    - (Wichtig!!!!!!!) Connection loss überprüfen!!
    - Reconnect testen
