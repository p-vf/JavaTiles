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

