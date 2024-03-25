# Einige Eigenheiten unseres Protokolls
- Unser Protokoll ist Verbindungsbasiert, das heisst jeder Client hat eine feste Socket-Verbindung mit dem Server.  
Aus dieser Gegebenheit können viele Informationen von den Commands ausgelassen werden (z. B. wer der Absender ist), da sie sich jeweils aus dem Kontext ergeben. 
- Separator unseres Protokolls: Leerschlag ("` `")  
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
| `<lobbies>`                                                        | Eine Repräsentation von Lobbys<br>(`<n_1>:<s_1>,<n_2>:<s_2>,...` wobei `n_M` die Lobbynummer und `s_M` die Anz. Spieler von Lobby `M` ist.)                                                                           |


🔴***TODO*** festlegen der Darstellung von <tile>, <deck> und <lobbies>  
🔴***TODO*** festlegen, was alles in <gamestate> gespeichert werden muss

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

## Lobbies anzeigen
| Command | Response          | Sender     |
|---------|-------------------|------------|
| `LLOB`  | `+LLOB <lobbies>` | Client     |
### Beschreibung
Wird geschickt, wenn der Client alle Lobbys auflisten will. 

### Beispiel
Client: ```LLOB```  
Server: ``+LLOB 23:4,12:2,3:1``

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

## Tile ablegen
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
| Command                                     | Response | Sender |
|---------------------------------------------|----------|--------|
| `CATC <whisper> <msg> [<whisperrecipient>]` | ` +CATC` | Client |

### Beschreibung
Der CATC-Command wird vom Client an den Server geschickt, wenn der User eine Nachricht in den Chat schicken will.
Der Parameter `<nickname>` gibt an, von wem die Nachricht kommt. 
Der Server muss dann einen CATS-Command an alle/einen Client/s schicken.  
Sie enthält eine Flag `<whisper>`. Falls diese den Wert `t` hat, muss der
optionale Parameter `<whisperrecipient>` definiert werden, der der Nickname des Spielers ist, an den sie gerichtet ist. 
Der Parameter `<msg>` ist der Inhalt der Nachricht. 

🔴***TODO***: `<whisper>` muss durch einen Wert ersetzt werden, der drei Werte darstellen kann (global, lobby oder whisper)

### Beispiel
Client: `CATC t "Tom hat mir folgendes gesagt: \"Nick ist mühsam\"" robin`  
Server: `+CATC`

(Der Server muss dann die Nachricht mit einem `CATS` nur an den Spieler `robin` weiterleiten, da die flag `<whisper>` den Wert `t` hat)

---

## Chat-Nachricht an andere weiterleiten (Server)
| Command                           | Response | Sender |
|-----------------------------------|----------|--------|
| `CATS <whisper> "<msg>" <sender>` | `+CATS`  | Server |
### Beschreibung
In diesem Command wird die Nachricht eines Clients an einen oder mehrere Clients in der Lobby weitergeleitet. 
Die Nachricht wird nur an alle Clients in der Lobby weitergeleitet, wenn `<whisper>` vom zugehörigen `CATC`-Command `f` ist.
`<sender>` ist der Nickname des Clients, der die Nachricht gesendet hat.
``<msg>`` ist die Nachricht, die versendet wird.

🔴***TODO***: `<whisper>` muss durch einen Wert ersetzt werden, der drei Werte darstellen kann (global, lobby oder whisper)

### Beispiel
Server: `CATS t "Tom hat mir folgendes gesagt: \"Nick ist mühsam\"" boran`
Client: `+CATS`

(Dies ist der `CATS`-Command, der an `robin` geschickt wird im Beispiel vom `CATC`-Command)

---

## Ping
| Command | Response | Sender          |
|---------|----------|-----------------|
| `PING`  | `+PING`  | Server / Client |
### Beschreibung
Die Ping-Nachricht konstant gesendet (mit einem Delay von ca. 1 Sekunde (exakte Dauer nicht sehr wichtig)), um Verbindungsunterbrüche aufzuspüren. 
Falls 15 Sekunden lang keine Nachricht über das Socket ausgetauscht wird, wird die Verbindung beendet.

___