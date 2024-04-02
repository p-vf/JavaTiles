# Einige Eigenheiten unseres Protokolls
- Unser Protokoll ist Verbindungsbasiert, das heisst jeder Client hat eine feste Socket-Verbindung mit dem Server.  
Aus dieser Gegebenheit können viele Informationen von den Commands ausgelassen werden (z. B. wer der Absender ist), da sie sich jeweils aus dem Kontext ergeben. 
- Jede Request in unserem Netzwerkprotokoll sieht wie folgt aus:  
```<commandname> <arg_1> <arg_2> ... [<optionalarg_1> ...] [<debugmessage>]```  
wobei ``<commandname>`` aus vier Grossbuchstaben besteht, `<arg_N>` das N-te Argument und `<optionalarg_N>` das N-te optionale Argument ist. 
- Jedes Argument ist entweder eine Zeichenfolge ohne Leerschlag (` `) und ohne Anführungszeichen (`"`) oder eine Zeichenfolge,  
welche mit Anführungszeichen beginnt und endet und jedes Anführungszeichen in der Zeichenfolge mit einem vorhergehenden 
Backslash (`\`) gekennzeichnet wird. Zum Beispiel wird die Zeichenfolge `Hallo "Leute", wie gehts?` wie folgt dargestellt: `"Hallo \"Leute\", wie gehts?"`
- TODO verschachtelte Darstellung von Argumenten erklären.
- Jede Response hat den gleichen Aufbau wie eine Request, jedoch wird ein ``+`` am Anfang angehängt:  
``+<commandname> ...``
- Jedes Argument kann im Prinzip Leerschläge beinhalten (siehe Beschreibung des Parameters `<msg>`).
Da dies aber nur im Falle der Chat-Nachricht von Notwendigkeit ist, wird dies nur dort explizit erwähnt.


| Parameter                                                      | Beschreibung                                                                                                                                                                                                                     |
|----------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `<joinsuccessful>`, `<valid>`, `<won>`                         | entweder `t` oder `f`, stellt booleschen Wert dar (`true`/`false`)                                                                                                                                                               |  
| `<nickname>`, `<whisperrecipient>`, `<sender>`, `<actualname>` | Spielername ohne Leerschlag, mit maximaler Länge 30. (stellt Spielername dar)                                                                                                                                                    |
| `<msg>`                                                        | Zeichenfolge, die eine Nachricht darstellt.                                                                                                                                                                                      |
| `<n>`                                                          | Ganze Zahl in Dezimaldarstellung. (stellt Lobbynummer dar)                                                                                                                                                                       |
| `<drawstack>`                                                  | Entweder `m` oder `e` (stellt entweder Hauptstapel oder Austauschstapel dar)                                                                                                                                                     |
| `<tile>`                                                       | Repräsentation eines Spielsteins;<br/>`<zahl>:<farbe>`, wobei `<zahl>` eine Zahl zwischen `1` und `13` und `<farbe>` entweder `RED`, `BLUE`, `YELLOW` oder `BLACK` ist.                                                          |
| `<deck>` `<startdeck>`                                         | Repräsentation des Spielerdecks;<br/>`<tile_1> <tile_2> ... <tile_20>` wobei `<tile_N>` die gleiche Darstellung wie `<tile>` hat. Ist kein Stein an Stelle N, so wird sie mit `\"\"` ersetzt.                                    |
| `<exchangestacks>`                                             | Repräsentation des obersten Steins der vier Austauschstapel;<br>`"<tile_0> <tile_1> <tile_2> <tile_3>"` wobei `<tile_N>` der Austauschstapel des Spielers mit Index N ist.                                                       |
| `<games>`                                                      | Eine Repräsentation von Lobbys; <br/>`<n_1>[:[<s_1>]] <n_2>[:[<s_2>]] ...` wobei `<n_M>` die Lobbynummer und `<s_M>` die Anz. Spieler oder ein Spielername von Lobby M ist. <br>(Für genauere Beschreibung siehe `LGAM`-Command) |
| `<playerlist>`                                                 | Stellt Liste von Spielern dar; <br/>`<name_1> <name_2> ...`                                                                                                                                                                      |
| `<lobbieswithplayerlist>`                                      | Stellt Liste von Lobbies mit den jeweiligen Spielern dar; <br/>`<lobby_1> <lobby_2> ...` wobei `<lobby_M>` die M-te Lobby wie folgt darstellt: `"Lobby <lobbynum_M>" "<playerlist_M>"`                                           |
| `<messagetype>`                                                | Stellt Art einer Chat-Nachricht dar; <br/>`b`,`l`,`w` für jeweils broadcast, lobby, und whisper                                                                                                                                  |



🔴***TODO*** festlegen der Darstellung von `<tile>`, `<deck>`, `<playerlist>` und `<games>`  
🔴***TODO*** festlegen, was alles in `<gamestate>` gespeichert werden muss

---


# Login und Lobby Stuff 
## Login
| Command           | Response             | Sender     |
|-------------------|----------------------|------------|
| `LOGI <nickname>` | `+LOGI <actualname>` | Client     |
### Beschreibung
Login auf Server mit Spielername `<nickname>`.  
Als Antwort kommt ein Spielername `<actualname>`, welcher der Name ist, welcher der Spieler effektiv erhält.
### Beispiel

Client: ```LOGI Istref```  
Server: ```+LOGI Istref_1```

(in diesem Beispiel gibt es schon einen Spieler auf dem Server mit dem Namen ``Istref``, also wird ein Einzigartiger generiert)

---

## Namensänderung
| Command           | Response             | Sender |
|-------------------|----------------------|--------|
| `NAME <nickname>` | `+NAME <actualname>` | Client |
### Beschreibung
Wird gesendet, wenn der User seinen Spielernamen zu `<nickname>` verändern will.  
In der Antwort ist das Argument `<actualname>` enthalten, welches den  
effektiven Spielernamen enthält, welchen der Spieler erhalten hat.
Dieser Command funktioniert gleich wie der ``LOGI``-Command.

🔴***TODO*** vielleicht ist dieser Command redundant (weil kein bedeutender Unterschied zu LOGI)

---

## Spieler auf Server auflisten
| Command | Response             | Sender |
|---------|----------------------|--------|
| `LPLA`  | `+LPLA <playerlist>` | Client |
### Beschreibung
Listet die Spieler auf, die mit dem Server verbunden sind. 

---

## Spieler in Lobby auflisten
| Command | Response                        | Sender |
|---------|---------------------------------|--------|
| `LLPL`  | `+LLPL <lobbieswithplayerlist>` | Client |
### Beschreibung
Listet in `<lobbieswithplayerlist>` die Spieler auf, die in den lobbies sind.
🔴***TODO*** aktualisieren, sobald klar ist, ob nur spieler von der eigenen Lobby aufgelistet werden sollen.

---

## Laufende und beendete Spiele / Lobbys auflisten
| Command             | Response                     | Sender     |
|---------------------|------------------------------|------------|
| `LGAM <gamestatus>` | `+LGAM <gamestatus> <games>` | Client     |
### Beschreibung
Wird geschickt, wenn der Client bestimmte Lobbies (games) auflisten will.
``<gamestatus>`` ist entweder `o`, `r` oder `f`, was jeweils 
offen (**o**pen), laufend (**r**unning) oder beendet (**f**inished) bedeuten soll. 
Dieses Argument bestimmt, welche Spiele angezeigt werden.  
Hat `<gamestatus>` den Wert `o`, so hat `<games>` folgende Form: `<lobbynumber_1>:<playercount_1> <lobbynumber_2>:<playercount_2> ...`.  
Hat `<gamestatus>` den Wert `r`, so hat `<games>` folgende Form: `<lobbynumber_1> <lobbynumber_2> ...`.  
Hat `<gamestatus>` den Wert `f`, so hat `<games>` folgende Form: `<lobbynumber_1>:[<winner_1>] <lobbynumber_2>:[<winner_2>] ...`. 
(zu beachten: nicht bei jedem Spiel gibt es einen Gewinner, in diesem Fall darf kein `<winner_N>` angegeben werden)

### Beispiel
Client: ```LGAM o```  
Server: ``+LGAM o "23:4 12:2 3:3"``  
Client: ```LGAM r```  
Server: ``+LGAM r "51 23"``  
Client: ```LGAM f```  
Server: ``+LGAM f "22:robin 1:nick 4:"``

---

## Lobbyauswahl
| Command    | Response                 | Sender |
|------------|--------------------------|--------|
| `JLOB <n>` | `+JLOB <joinsuccessful>` | Client |
### Beschreibung
Wenn sich ein Spieler eingeloggt hat, kann dieser Command an den Server geschickt werden.
Gibt es keine Lobby mit der Lobbynummer ``<n>``, so wird eine neue erstellt.
Ist die Lobby mit Lobbynummer `<n>` voll, Bzw. hat das Spiel in dieser Lobby schon angefangen, 
so wird `<joinsuccessful>` den Wert `f` haben und der Client konnte der Lobby nicht beitreten. 

---

## Logout
| Command | Response | Sender |
|---------|----------|--------|
| `LOGO`  | `+LOGO`  | Client |
### Beschreibung

Logout vom Server.

---

# Die Spiellogik / den Spielablauf betreffende Commands

## Bereitschaft erklären
| Command | Response | Sender |
|---------|----------|--------|
| `REDY`  | `+REDY`  | Client |
### Beschreibung
Wird vom Client geschickt, wenn er parat zum Spielen ist.

---

## Spiel beginnen
| Command                        | Response | Sender |
|--------------------------------|----------|--------|
| `STRT <startdeck> <playeridx>` | `+STRT`  | Server |
### Beschreibung
Wird vom Server geschickt, wenn alle Spieler in einer Lobby den `REDY`-Command an den Server geschickt haben,  
und 4 Spieler in der Lobby sind. In `<startdeck>` ist das Startdeck des Spielers enthalten.  
Sind in diesem Deck 15 Steine, ist der empfangende Client der Startspieler.  
In `<playeridx>` wird eine Zahl übergeben, die den Index des empfangenden Clients darstellt, welcher für die Identifikation des Spielers wichtig ist. 


---

## Gamestate anfragen
| Command                                    | Response | Sender |
|--------------------------------------------|----------|--------|
| `STAT <exchangestacks> <currentplayeridx>` | `+STAT`  | Server |
### Beschreibung
Der Server schickt den obersten Stein der Austauschstapel in `<exchangestacks>`   
und der Index des Spielers, der an der Reihe ist in `<currentplayeridx>`. 

### Beispiel
🔴***TODO***

---

## Tile ziehen
| Command            | Response       | Sender |
|--------------------|----------------|--------|
| `DRAW <drawstack>` | `+DRAW <tile>` | Client |
### Beschreibung
Der Client gibt einen Stapel in `<drawstack>` an, von welchem er einen Stein haben will,  
der Server gibt den Stein in `<tile>` zurück.

### Beispiel
🔴***TODO***


---

## Stein ablegen
| Command              | Response              | Sender |
|----------------------|-----------------------|--------|
| `PUTT <tile> <deck>` | `+PUTT <valid> <won>` | Client |
### Beschreibung
Nachdem der Spieler dran war, wird der Stein ``<tile>`` an den Nachbarstapel weitergegeben, 
zudem wird sein Deck ``<deck>`` an den Server geschickt, welcher dann dieses überprüft. 
Die Flag `<won>` beschreibt, ob der Spieler mit diesem Zug gewonnen hat, falls wahr, wird
das Spiel mit einem ``PWIN``-Command von der Serverseite beendet.
Zudem wird vom Server mit der Flag `<valid>` mitgeteilt, ob die Konfiguration und der Spielzug gültig ist.

### Beispiel
Client: `PUTT 13:RED "13:BLUE 13:BLACK 13:YELLOW \"\" 3:YELLOW 4:YELLOW 5:YELLOW 6:YELLOW 7:YELLOW \"\" 11:BLUE 12:BLUE 13:BLUE \"\" \"\"  1:YELLOW 1:BLUE 1:RED \"\" \"\" \"\" \"\" \"\" \"\""`  
Server: `+PUTT t t`

---

## Spiel beenden (durch Sieg)
| Command           | Response | Sender |
|-------------------|----------|--------|
| `PWIN <nickname>` | `+PWIN`  | Server |
### Beschreibung
Command wird gesendet, wenn das Spiel vom Spieler mit Name `<nickname>` gewonnen wird.

---

## Spiel beenden (durch Unentschieden)
| Command | Response | Sender |
|---------|----------|--------|
| `EMPT`  | `+EMPT`  | Server |

### Beschreibung
Command wird vom Server gesendet, wenn der Hauptstapel leer ist, und somit das
Spiel mit einem Unentschieden beendet werden muss. 

---

# Die Kommunikation betreffend
## Chat-Nachricht senden (Client)
| Command                                         | Response | Sender |
|-------------------------------------------------|----------|--------|
| `CATC <messagetype> <msg> [<whisperrecipient>]` | ` +CATC` | Client |

### Beschreibung
Der CATC-Command wird vom Client an den Server geschickt, wenn der User eine Nachricht in den Chat schicken will.  
Sie enthält ein Argument `<messagetype>`, welches die Reichweite der Nachricht spezifiziert (`b`,`l`,`w` für jeweils broadcast, lobby, und whisper).  
Falls dieses den Wert `w` hat, muss der optionale Parameter `<whisperrecipient>` definiert werden.  
Hat `<messagetype>` den Wert `b`, wird ein `CATS`-Command an alle Clients auf dem Server weitergeleitet, 
falls `l` nur an die in der selben Lobby und falls `w` nur an den Spieler, welchen den Nickname `<whisperrecipient>` hat.  
Der Parameter `<msg>` ist der Inhalt der Nachricht.

### Beispiel
Client: `CATC w "Tom hat mir folgendes gesagt: \"Nick ist mühsam\"" robin`  
Server: `+CATC`

(Der Server muss dann die Nachricht mit einem `CATS` nur an den Spieler mit nickname `robin` weiterleiten, da die flag `<messagetype>` den Wert `w` hat)

---

## Chat-Nachricht an andere weiterleiten (Server)
| Command                             | Response | Sender |
|-------------------------------------|----------|--------|
| `CATS <messagetype> <msg> <sender>` | `+CATS`  | Server |
### Beschreibung
In diesem Command wird die Nachricht eines Clients an einen oder mehrere Clients weitergeleitet.  
Dieser Command ist immer die Folge eines `CATC`-Commands. `<messagetype>` hat dann den selben Wert wie der vom zugehörigen `CATC`-Command.  
`<sender>` ist der Nickname des Clients, der die Nachricht gesendet hat.
``<msg>`` ist die Nachricht, die versendet wird.


### Beispiel
Server: `CATS w "Tom hat mir folgendes gesagt: \"Nick ist mühsam\"" boran`
Client: `+CATS`

(Dies ist der `CATS`-Command, der an `robin` geschickt wird im obigen Beispiel vom `CATC`-Command)

---

## Ping
| Command | Response | Sender          |
|---------|----------|-----------------|
| `PING`  | `+PING`  | Server / Client |
### Beschreibung
Die Ping-Nachricht wird konstant gesendet (mit einem Delay von ca. 1 Sekunde (exakte Dauer nicht sehr wichtig)), um Verbindungsunterbrüche aufzuspüren. 
Falls 15 Sekunden lang keine Nachricht über das Socket ausgetauscht wird, wird die Verbindung beendet.

___