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