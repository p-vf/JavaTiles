# Tagebuch Gruppe 5
## 5.3.2024:
Heute haben wir ein Brainstorming gemacht und unser Spiel provisorisch festgelegt.
Wir werden voraussichtlich ein Spiel machen, welches gleich oder ähnlich wie Okey funktioniert. 
## 9.3.2024
Arbeit am ersten Meilenstein:
- Mockup kreieren
- requirement analysis und Networking Achievements angefangen
- Festlegung auf einen Namen: JavaTiles
- erste Ideen fürs Design
- PowerPoint-Präsentation angefangen
## 12.3.2024
- Powerpoint beendet
- requirement analysis und Networking Achievements weiterentwickelt
- Gruppenname: vonUken
## 13.3.2024
- weitere Bearbeitung an der Präsentation
- Gant-Project ausgebaut
- Gruppeneinteilung: Client: Boran, Robin Server: Istref, Pascal
## 15.3.2024
- Syntax fürs Netzwerkprotokoll festgelegt und erste Einträge gemacht
- genaue Arbeitsteilung(spezifizierung der To-Do-Liste)
- Besprechung des 2ten Meilensteins
## 16.3.2024
- das Netzwerkprotokoll weitergeführt 
- am Server-Client-Code gearbeitet(Ping-Pong)
## 17.3.2024
- Client-Login definiert
- Ping Funktion auf Server und Client integriert(erste Version)
- Client handling
- Client Chat-Funktion.
## 19.3.2024
- Beim Clientlogin eine get-Methode für den Username und einige Felder hinzugefügt, um Daten an den Server weiter zu schicken.
- Beim Server die Methode logClientOut hinzugefügt, um bei Fehlverbindungen den Client rauszuwerfen.
- Beim EchoClientThread die Methode logout implementiert und bei Fehlerbehandlungen eingefügt.
- Die Klasse SyncOutputStreamHandler hinzugefügt um sicherzustellen, dass die Threads nicht in einen Konflikt geraten, sondern synchronisiert sind.
- Die run-Methode von Echoclientthread verbessert, weil sie nicht richtig funktioniert hat.
- Es wurde ein ClientPingThread erstellt um Pings an den Server zu schicken, welcher im EchoClient aktiviert wird.
- Eine erste Implementation des Chats durch die Klasse Clienthandler.
- Der SyncOutputStreamHandler auf der Client Seite wurde auch implementiert aus dem gleichen Grund wie es auf der Serverseite geschehen ist.
- Eine erste Implementation des switch Case LOGI wurde erstellt, bei der auch eine getNicknames Methode im Server erstellt wurde.

## 21.3.2024
- Clientsite: Input von Consoler wird nun durch Handler bearbeitet:
 - CATC: Chat für die Clientseite implementiert.
 - LOGIN wurde implementiert.
 - Funktion NAME implementiert um den nickname zu ändern.
 - in InThread if-Block eingeführt, der dafür sorgt, dass bei Bedarf PING nicht mehr in der Console ausgegeben wird.

## 22.3.2024
- PING-Funktion optimiert, sodass auf PINGS mit einem PONG gewantwortet wird und die Erfassung einer PING nachricht ausschlaggebend für den Erhalt der Verbindung ist.
- Im Client wurde der Request-handler eingeführt, der dafür sorgt, dass Anfragen vom Server bearbeitet werden können.
- messages und whispermessages können nun in vollen Texten verschickt werden.
- Logout Funktion auf der Client Seite implementiert.
- Möglichkeit hinzugefügt PING-messages auf der Console zu verbergen, als Hilfe bei Tests.
- Login-Funktion auf Server wurde auf Server Seite optimiert.

## 23.3.2024
- Auf der Server Seite wurde das vorgehen beim Ouput geändert, nämlich haben wir die Klasse SyncOutputStreamHandler gelöscht und dafür die Methode
send hinzugefügt.
- Nickname change wurde implementiert und verbessert auf beiden Seiten.
- PingThread Verbesserung für den Fall, dass man die Verbindung verliert.
- Unnötige Klassen wurden gelöscht.
- Parserequest Methode wurde auf Client Seite verbessert, weil sie vorher nicht wie gewünscht funktioniert hat.
- Auf der Serverseite wurde die Javadoc erstellt.

## 24.3.2024
- ParseRequest Methode auf Seiten des Servers so implementiert, dass leere Strings ignoriert werden.
- handleRequest Methode ausgebaut auf dem Client, weil einige noch fehlten.
- Javadoc leicht ausgebaut auf Server Seite
- Readme erstellt.
- QA Konzept haben wir geschrieben

## 25.3.2024
- Beide PING Klassen wurden überarbeitet, da sie noch bugs enthielten.
- Javadoc auch auf Clientseite erstellt.

## 28.3.2024 Meeting (Robin, Pascal, Boran)
  - Besprechung der Bewertung des letzten Meilensteins
  - weitere Vorgehensweisen:
 - Besprechung wie unsere eigenen Änderungen gemerged werden können.
   - Auf der Clientseite werden nun die requests mit Enums geregelt.
   - Commands werden nun von einer Klasse decoded und encoded (package utils.NetworkUtils).
 - Besprechung des QA Konzepts:
   - Unit Tests implementieren
   - Erste Versuche mit Metrics Reloaded
 - Erste Schritte zur Implementation der Spiellogik besprochen

 - wie könnten wir chronologisch vorgehen (erste Implementation):
    - Serverseite:
      - Lobbies
      - Broadcast
      - Gamestate 
      - Spiellogik implementation

    - Clientseite:   
      - JLOB Join lobby Funktionalität
      - LLOB zeige Lobbies an
      - /all Command für broadcast
      - Spiellogik implementation 
      - Chat GUI

 ## 29.3.2024 Meeting und Programmieren (Robin, Pascal, Boran, Istref)     
- Wir haben beschlossen, wie die lobbies auf dem server gehandhabt werden sollen und dies grösstenteils implementiert. 
- Der Lobbychat wurde implementiert.
- Chat commands in der netzwerkprotokolldatei wurden aktualisiert.
- List commands wurden auf der Serverseite noch nicht implementiert, das ist unser nächstes TODO.
- Arbeitsteilung für weiteres Vorgehen: Robin, soll nachdem die Lobbies richtig implementiert wurden, sich an die Spiellogik machen.
- Boran kümmert sich um das ChatGUI
- Istref und Pascal arbeiten an der Spiellogik auf der Serverseite.

## 1.4.2024 Besprechung in der GGG über die Umsetzung der Spiellogik (Robin, Pascal, Boran, Istref)
- REDY als neuen Command im Netzwerkprotokoll:
  - Client schickt REDY an den Server. Sobald alle Spieler "ready" sind wird das Spiel gestartet.
- neuer Command STRT:
  - Die Decks werden jeweils als Hash-set?(noch nicht einig) an den Client geschickt.
  - Auf dem Client wird das Deck jeweils als zweidimensionales Array gespeichert.
  - Dieses Deck wird somit auf der Client-Seite und auf der Server-Seite gespeichert.
  - Ein Spieler bekommt 15 Tiles, die anderen 14. 
- PUTT von der Client-Seite
  - 24 Plätze auf dem Deck
  - Der Server überprüft ob das Deck gewinnend ist und valid, falls valid wird Serverdeck und exchange stack geupdated. 
  - Die Änderung wird mit einem STAT command an die Clients geschickt.
    - Wer ist an der Reihe.
    - Exchange-Stacks. 
    - Jeder Spieler hat einen Index. Der jeweils oberste Tile der vier exchange stacks werden an die Spieler gesendet. Es wird ein Index, vom Server geschickt, der bestimm, welcher Spieler an der Reihe ist. 
    - Server überprüft ob main Stack leer ist.
    - DRAW:
    - Spieler wählt vom Exchange oder Main-stack:
      - Der User entscheided je nach command draw m(main stack) oder draw e(exchange stack) geht als Anfrage an den Server
      - Je nach Fall nimmt der Serve Stein aus jeweiligen stack und überleifert diesen Stein an den Client
    - Der Spieler sortiert nach Bedarf sein Deck und legt wieder einen Stein ab: Somit kommt fängt alles wieder von vorne an.

- Im Fall von PWIN:
 - Server gibt Info zurück das wer gewonnen hat, das Spiel wird beendet. Lobby wird zu finished games hinzugefügt. 

- Im Fall von EMPT:
 - den Spielern wird mitgeteilt, dass es zu einem Unentschieden 

## 2.4.2024 Besprechung im Lernoullianum (Robin, Pascal, Boran, Istref)
- Wir haben uns an diesem Tag getroffen, um die Spiellogik weiter zu implementieren.
- Auf der Clientseite wurde die Bekanntgabe des Gewinners implementiert.
- Auf der Serverseite wurden die fehlenden Enums hinzugefügt.
- Das Netwerkprotokoll wurde aktualisiert.
- Mehrere kleine Methoden, die wichtig für die Spiellogik sind wurden angefangen oder implementiert.
- Am Schluss des Meetings haben wir die Aufgaben aufgeteilt, sodass wir morgen selbstständig weiter arbeiten können.

## 3.4.2024 Einzelarbeit
- Bei der Tile-Klasse wurden die Methoden um von einer Tile zu einer String zu konvertieren und anders herum implementiert.
- Eine Deck-Klasse wurde auf der Clientseite implementiert.
- VaildateMove Methode auf der Serverseite implementiert.
- STRT DRAW und PUTT funktionalität auf der Clientseite implementiert
- Unordered Deck Klasse mit Methoden indexToTile und toStringArray ausgebaut.
- PUTT und REDY funktionalität auf Serverseite implementiert.
- Wir haben über unsere Whatsapp Gruppe beschlossen, morgen uns um 16:30 im Lernoullianum zu treffen und 
- letzte Implementierungen der Spiellogik zu implementieren und diese anschliessend zu testen.

## 4.4.2024 Besprechung im Lernoullianum (Robin, Pascal, Boran, Istref)
- DRAW command wurde auf der Serverseite implementiert
- getVisibleTiles Methode im GameState implementiert um die Exchangestacks anzeigen zu können.
- erste Version des GUI
- Edgecases auf Protokollebene werden jetzt korrekt abgehandelt
- Durch das Testen sind einige kleine Bugs uns aufgefallen, von welchen wir welche beheben konnten,
wie zum Beispiel dass beim schicken des PUTT commands der Client die übergebenen Parameter vertauscht hat.
- Bei der Spiellogik bleibt uns noch zu tun: Spieler von illegalen Spielzügen abhalten, wie zum Beispiel zweimal eine Karte ziehen.
- Wir haben abgemacht, morgen um 13:00 Uhr weiter an der Spiellogik und am GUI zu arbeiten. 


