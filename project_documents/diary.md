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

## 2.4.2024 Treffen im Lernoullianum (Robin, Pascal, Boran, Istref)
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

## 4.4.2024 Treffen im Lernoullianum (Robin, Pascal, Boran, Istref)
- DRAW command wurde auf der Serverseite implementiert
- getVisibleTiles Methode im GameState implementiert um die Exchangestacks anzeigen zu können.
- erste Version des GUI
- Edgecases auf Protokollebene werden jetzt korrekt abgehandelt
- Durch das Testen sind einige kleine Bugs uns aufgefallen, von welchen wir welche beheben konnten,
wie zum Beispiel dass beim schicken des PUTT commands der Client die übergebenen Parameter vertauscht hat.
- Bei der Spiellogik bleibt uns noch zu tun: Spieler von illegalen Spielzügen abhalten, wie zum Beispiel zweimal eine Karte ziehen.
- Wir haben abgemacht, morgen um 13:00 Uhr weiter an der Spiellogik und am GUI zu arbeiten. 

## 5.4.2024 Treffen im Pharmazentrum (Robin, Pascal, Boran, Istref)
- Wir haben weiter an der Spiellogik gearbeitet und uns sind weitere kleine Fehler aufgefallen.
Wir haben bemerkt, dass beim Client beim PUTT command nicht berücksichtigt wird, ob man an der Reihe ist oder nicht.
Ausserdem ist beim GUI-Chat etwas noch nicht ganz in Ordnung, denn das Versenden von Nachrichten funktioniert nicht.
- /listplayers und /listlobbies command im client implementiert
- edgecases im Username input behandelt vom Client 
- size Methode in UnorderedDeck Klasse hinzugefügt, um zu überprüfen, dass keine illegalen Spielzüge durchgeführt werden können.
- size Methode vom UnorderedDeck im Case DRAW und PUTT auf Serverseite hinzugefügt.
- Wir haben beschlossen, am Sonntag, 07.04.2024 um 13:00 Uhr gemeinsam weiter zu arbeiten.

## 6.4.2024 Einzelarbeitstag
- Ein paar Korrekturen in der Spiellogik auf der Clientseite, wie z.B. die Putt- und Draw-Funktion, wurden aufeinander abgestimmt. Zudem wurde das Chat-GUI größtenteils fertiggestellt.
- Kleine Optimierungen auf der Serverseite
- Die Darstellung der Tiles wurde verbessert.
- Wir haben mit dem Manual begonnen.
- Optimierungen für die User Eingaben wie beispielsweise Games auflisten, sowie einige edge cases für andere Eingaben behandelt.
- Die .jar file funktioniert jetzt

## 7.4.2024 Treffen im Lernoullianum (Robin, Pascal, Boran, Istref)
- Als erstes haben wir auf der Clientseite die DRAW und PUTT Methode so implementiert, dass man erst wenn man
an der Reihe ist den Befehl wirklich ausführen kann.
- Danach haben wir das Spiel getestet und uns kam es doch komisch vor, dass der Sender seine eigene
Nachricht nicht sieht, was wir dann angepasst haben. Ausserdem war es etwas schwer das spiel zu spielen, da die
Darstellung der Tiles auf dem Terminal nur aus zwei Arrays bestand, woraufhin wir entschieden, die Tiles schöner 
zu gestalten. Deshalb haben wir sie nach der entsprechenden Farbe gefärbt.
- Das Manual wurde überarbeitet und grundsätzlich fertiggestellt.
- Wir haben zusammen den Projektplan besprochen und überarbeitet, sodass wir wissen was zu tun ist bis zum schluss
vom Projekt.
- Als nächsten Termin haben wir die morgige Übungsstunde festgelegt.

## 8.4.2024 Weiterarbeiten in der Übungsstunde (Robin, Pascal, Boran, Istref)
- Beim Spielen in der Übungsstunde hatten wir grosse Freude, da man mit der überarbeiteten Darstellung nun das 
Spielgeschehen gut mitverfolgt werden konnte. Uns ist aufgefallen, dass die Whisper funktionalität mangelhaft ist
und auch, dass wir gerne einen Cheatcode einfügen würden.
- Das QA Konzept haben wir nochmals überdacht und auf diesen Meilenstein angepasst
- Das Netwerkprotokoll wurde angepasst.
- Später haben wir uns im Lernoullianum getroffen und:
- Javadoc aktualisiert
- Den Cheatcode tatsächlich implementiert.
- Das Netwerkprotokoll angepasst.
- Am ende des Tages haben wir beschlossen, uns um 13:15 im Lernoullianum zu treffen.

## 9.4.2024 Fertigstellung MS3 Lernoullianum (Robin, Pascal, Boran, Istref)
- Wir haben uns getroffen und haben zusammen hauptsächlich über die Präsentation gesprochen, wer was präsentiert und wie wir uns alles Zeitlich aufteilen. 
- Ausserdem haben wir alle Dokumente nochmals gründlich überprüft und Veränderungen vorgenommen, wo welche nötig waren. Jetzt sollte alles bereit sein für den 3.Meilenstein.

## 13.4.2024 Treffen im Lernoullianum (Pascal, Boran, Istref)
- Besprechung Massnahmen vor dem 4. Meilenstein:
  - Spezifikation wie commit-messages geschrieben werden sollen muss als nächstes in einem Dokument festgelegt.
  - Die Tile-Konfigurationen sollen als ganze Decks rübergeschickt werden und demnach so auf dem Client gehandhabt werden.
  - Lobby-Spielerliste: 
    - NAMS: Wenn ein Spieler in der Lobby den Namen ändert wird das geschickt wenn er joined oder leaved.
    - JOND: Anzeige, dass ein Spieler gejoined ist. 
    - LEFT: Anzeige, dass ein Spieler das Spiel verlassen hat. 
  - Highscore-Liste: Der Spieler, der am wenigsten Züge braucht steigt im Highscore.

## 14.4.2024 Treffen im Lernoullianum (Pascal, Boran, Robin)
- An diesem Tag haben wir uns zusammengesetzt, um die Commit Conventions festzulegen. Da Istref nicht kommen konnte, haben wir 
ihm nach der Erstellung des Files bescheid gegeben, sodass er sich auch daran halten kann und selbstverständlich haben wir ihm 
gesagt, dass er auch Verbesserungsvorschläge bringen kann.

 ## 15.04.2024 Übungsstunde zum 3.Meilenstei
 - Wir waren sehr zufrieden mit unserer Präsentation und haben das Gefühl, dass unsere Arbeit den gegebenen Anforderungen entsprach.
 - Zudem hatten wir noch eine Frage bezüglich den Unit-Tests:
   - Welcher Teil des Projekts kann/soll getestet werden?
   - Fazit: Diese Entscheidung liegt bei uns, jedoch wurde uns die Spiellogik auf der Serverseite oder auch die Server-Client Verbindung zum testen empfohlen. Zudem wurde uns die Programmbibliothek "Mockito" vorgestellt.



## 14.04-18.04.2024 Einzelarbeit
- Implementation von:
- JOND
- LEFT
- NAMS
- Zusätzlich über Whatsapp besprochene Funktion LLOB welche uns helfen soll, die Lobby zu verlassen ohne den Server verlassen zu müssen. 
- Auch hat das Client-Team (Robin und Boran) am GUI gearbeitet. 



## 19.4.2024 Treffen Spiegelgasse (Boran, Robin, Istref, Pascal)
- Wir haben uns dafür entschieden, die Spiellogik auf der Serverseite zu testen.
- Besprechung über weiteres Vorgehen bis zum 4. Meilenstein; Zuständigkeiten:
  - Istref: Unittests schreiben zu der Spiellogik.
  - Pascal: Refactoring von den Klassen auf dem Server
  - Boran: Gameplay-GUI
  - Robin: restliches GUI (Login, logout, Namensänderung, etc.)
- Entspannter, produktiver Austausch

## 19.4-21.4.2024 Einzelarbeit
- Highscore Klasse erstellt (Serverseite)
- HIGH: Funktion um die Highscores anzuzeigen.
- Erste Unit-Tests implemetiert
- Weiterarbeiten am GUI

## 22.4.2024 Übungsstunde (Boran, Robin, Istref, Pascal)
- Fragen, die wir in der Übungsstunde gestellt haben: 
  - GUI: Wie können wir dafür sorgen, dass das GUI auf verschiedene Grösseneinstellungen des Fensters entsprechend reagiert.
    - Fazit: Wir könnten mit verschiedenen Panes innerhalb eines Borderpanes arbeiten und dem mittleren Pane eine feste Grösse geben, während die anderen Panes beliebig ihre Grösse ändern können.
  - Unittests: Wie sollen wir die Tests gliedern? 
    - Fazit: Die Tests kann man Klassenweise gliedern. Alle zu prüfenden Methoden der gleichen Klasse werden somit auch in der selben Testklasse geprüft.


## 23.4 - 26.4.2024 Einzelarbeit 
- Weiterarbeiten am GUI und am an den Unit Tests.
- Aufteilung der Arbeit am GUI.
  - Robin ist nun primär für den Login und die Lobbyauswahl zuständig, während sich Boran auf den Spielablauf konzentriert.
  - erste Erfolge: 26.04.2024 Das Spiel konnte komplett auf dem GUI, durchgespielt werden. 
- Die letzten Unit tests wurden am 26.04.2024 implementiert und konnten erfolgreich ausgeführt werden.

## 27.4.2024 Einzelarbeit unterbrochen von kurzen Besprechungen (Boran, Istref, Pascal)
- JavaDoc wurde weitergeführt
- Refactors auf dem Server (public Felder eliminiert)
- Neuer Netzwerkcommand RNAM wurde auf Server- sowie auf Clientseite implementiert nachdem dies in der kurzen Besprechung vereinbart wurde.
- Das nächste Treffen wurde für morgen um 21:00 im Lernoullianum geplant.

## 28.4.2024 Treffen Lernoullianum (Boran, Robin, Pascal, Istref)
- Beim Treffen haben wir uns gegenseitig ausgetauscht, wie alles läuft und auch schon spezifische TODOs in unser TODO-Dokument eingetragen für den nächsten Meilenstein.
- Was in Einzelarbeit erledigt wurde:
  - Javadoc wurde vervollständigt
  - Auf dem GUI wurden viele Funktionalitäten verbessert und Fehler wurden behoben.
  - Wir sollten nun bereit sein unser Spiel morgen vorzustellen.

## 2.5.2024 Treffen Lernoullianum (Robin, Pascal, Istref)
- Wir haben uns an diesem Tag getroffen, um die Peer Review für das Spiel Javatzy zu schreiben.
- Die Vereinbarung an diesem Tag die Peer Review für Javatzy zu schreiben hat über 
WhatsApp stattgefunden.
- Das nächste Treffen wurde für morgen festgelegt in der Mathefakultät, jedoch 
ohne Robin, da wir fanden, dass es nicht viel Sinn macht zu dritt daran zu arbeiten.

## 3.5.2024 Treffen in der Mathefakultät (Pascal, Istref)
- An diesem Tag haben wir dann die Peer Review zum Spiel Hol's der Compiler geschrieben und zusammen mit der Peer Review von Gestern abgegeben.

## 6.5.2024 Übungsstunde (Istref, Boran, Pascal)
- ToDos aktualisiert
- noch zu klären: Müssen die Spieler vom Server während dem Spiel aufgelistet werden können?

## 7.-9.5.2024 Einzelarbeit
- Wir haben am Manual und am QA-Konzept gearbeitet.
- Wir haben über WhatsApp besprochen, dass wir versuchen werden einen Zuschauer Modus einzufügen, falls dieser uns
nicht zu viel Zeit kostet.
- Einige Funktionalitäten und Designs wurden dem GUI hinzugefügt.
- Ein Treffen wurde ausgemacht für den 10.05.2024 im Lernoullianum.

## 10.05.2024 Treffen im Lernoullianum (Boran, Robin, Pascal, Istref)
- Beim Treffen haben wir als erstes Besprochen, ob wir den Zuschauer Modus nun implementieren oder nicht
und sind zu dem Entschluss gekommen, dass es zeitlich etwas knapp wird und der Zuschauer Modus auch die anderen bereits
vorhandenen Funktionen beeinträchtigen könnte und haben uns dagegen entschieden.
- Danach haben wir unsere TODO-Liste aktualisiert.

## 13.05.2024 Übungsstunde (Boran, Pascal)
- Das GUI wurde intensiv getestet und einige Fehler wurden noch gefunden. Diese wurden behoben. 
- Letzte Optische Änderungen am GUI wurden in Einzelarbeit hinzugefügt. 

## 14.05.2024 Treffen im Lernoullianum (Boran, Robin, Pascal, Istref)
- Das Spiel ist nun fertiggestellt. 
- An diesem Tag waren wir mit den letzten Aufnahmen unseres Spiels, wie auch mit der Erarbeitung der Presentation für den kommenden Freitag beschäftigt. 
- Die Aufnahmen beinhalten einserseits einige Screenshots des Spiels, wie auch das Videomaterial für unser gameplay. 
- Wir entschieden uns dazu, in der Demo den Cheat-code zu verwenden um das Spielgeschehen zu beschleunigen. 
- Ein Unit test war nicht mehr erfolgreich, diesen Fehler hat Istref behoben. 
- Zum generellen Abschluss kann man sagen, dass wir sehr zufrieden mit unserem Spiel sind und der Art wie wir zusammengearbeitet haben. 