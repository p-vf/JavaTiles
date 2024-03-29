# Einige Eigenheiten unseres Protokolls
- Unser Protokoll ist Verbindungsbasiert, das heisst jeder Client hat eine feste Socket-Verbindung mit dem Server.  
Aus dieser Gegebenheit können viele Informationen von den Commands ausgelassen werden (z. B. wer der Absender ist), da sie sich jeweils aus dem Kontext ergeben. 
- Jede Request in unserem Netzwerkprotokoll sieht wie folgt aus:  
```<commandname> <arg_1> <arg_2> ... [<optionalarg_1> ...] [<debugmessage>]```  
wobei ``<commandname>`` aus vier Grossbuchstaben besteht, `<arg_N>` das N-te argument ist. 
- Jede Response hat den gleichen Aufbau wie eine Request, jedoch wird ein ``+`` am Anfang angehängt:  
``+<commandname> ...``
- Jedes Argument kann im Prinzip Leerschläge beinhalten (siehe Beschreibung des Parameters `<msg>`).
Da dies aber nur im Falle der Chat-Nachricht von Notwendigkeit ist, wird dies nur dort explizit erwähnt.


| Parameter                                                          | Beschreibung                                                                                                                                                                                                          |
|--------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `<joinsuccessful>`, `<nametaken>`, `<valid>`, `<won>`, `<whisper>` | entweder `t` oder `f`, stellt booleschen Wert dar (`true`/`false`)                                                                                                                                                    |  
| `<nickname>`, `<whisperrecipient>`, `<sender>`                     | Spielername ohne Leerschlag, mit maximaler Länge 15. (stellt Spielername dar)                                                                                                                                         |
| `<msg>`                                                            | <a name="msg"></a>Mit Anführungszeichen begrenzte Zeichenfolge, welche Anführungszeichen mit \" darstellt. (Stellt Nachricht dar)                                                                                     |
| `<n>`                                                              | Ganze Zahl in Dezimaldarstellung. (stellt Lobbynummer dar)                                                                                                                                                            |
| `<drawstack>`                                                      | Entweder `"m"` oder `"p"` (stellt entweder Hauptstapel oder Nachbarsstapel dar)                                                                                                                                       |
| `<tile>`                                                           | Eine Repräsentation eines Spielsteins<br>(Vorschlag: `<zahl><farbe>`, wobei `<zahl>` eine Zahl zwischen `01` und `13` (immer zwei Ziffern lang) und `<farbe>` der erste Buchstabe der Farbe, die der Stein hat, ist.) |
| `<deck>`                                                           | Eine Repräsentation des Spielerdecks<br>(Vorschlag: `<tile_1><tile_2>...<tile_20>` wobei `<tile_N>` die gleiche darstellung wie `<tile>` hat)                                                                         |
| `<gamestate>`                                                      | Eine Repräsentation des Spielzustandes                                                                                                                                                                                |
| `<games>`                                                          | Eine Repräsentation von Lobbys<br>(`<n_1>:<s_1>,<n_2>:<s_2>,...` wobei `n_M` die Lobbynummer und `s_M` die Anz. Spieler von Lobby `M` ist.)                                                                           |


🔴***TODO*** festlegen der Darstellung von `<tile>`, `<deck>`, `<playerlist>` und `<games>`  
🔴***TODO*** festlegen, was alles in `<gamestate>` gespeichert werden muss

---


# Login und Lobby Stuff 
## Login
| Command             | Response                        | Sender     |
|---------------------|---------------------------------|------------|
| `LOGI <nickname>`   | `+LOGI <actualname>`            | Client     |
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
| Command | Response             | Sender |
|---------|----------------------|--------|
| `LLPL`  | `+LLPL <playerlist>` | Client |
### Beschreibung
Listet die Spieler auf, die in der Lobby sind.
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


(Hier hat der Server drei Lobbys, jeweils mit Lobbynummer `23`, `12` und `3` und einer Anzahl von Spielern von jeweils `4`, `2` und `1`.)

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

# Die Spiellogik betreffende Commands
## Gamestate anfragen
| Command | Response            | Sender |
|---------|---------------------|--------|
| `STAT`  | `+STAT <gamestate>` | Client |
### Beschreibung
Der Client fragt den Zustand des Spiels an und erhält ihn in `<gamestate>` in der Antwort vom Server. 

### Beispiel
🔴***TODO***

---

## Tile ziehen
| Command            | Response       | Sender |
|--------------------|----------------|--------|
| `DRAW <drawstack>` | `+DRAW <tile>` | Client |
### Beschreibung
Der Client will eine Karte vom Stack aufheben, der Server gibt die Tile zurück.

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
🔴***TODO***


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

(Der Server muss dann die Nachricht mit einem `CATS` nur an den Spieler `robin` weiterleiten, da die flag `<messagetype>` den Wert `w` hat)

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