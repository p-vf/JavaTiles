# Protokollbeschrieb
## Aufbau eines Commands auf Zeichenebene
Alle Tokens können beliebige(*) Zeichenfolgen sein.  
Ist *t* nun ein solches Token. Dann gilt:  

`<t>` = `"` + F(*t*) + `%"`  



Wobei F(*t*) alle Anführungszeichen in *t* mit `\"` ersetzt und die resultierende Zeichenfolge zurückgibt.  

Wir können nun mehrere beliebige(*) Zeichenfolgen *t_0*, *t_1*, *t_2*, ... wie folgt beliebig aneinanderreihen:

`<t_0>` + ` ` + `<t_1>` + ` ` + `<t_2>` + ` ` + `<t_3>` + ...

oder kurz:  
*z* = `<t_0> <t_1> <t_2> <t_3> ...`

was wieder eine Zeichenfolge ist.

Was es hierbei zu bemerken gibt: Es existiert eine Rückwärtsumwandlung, die aus dieser Zeichenfolge die einzelnen Tokens extrahieren kann, 
nennen wir sie R(*z*, *i*), wobei *i* der Index des Tokens angeben soll (beginnend bei 0). 

Die Definition dieser Rückwärtsfunktion ist eine Übung für den Lesenden ;).

Damit unser Protokoll einigermassen lesbar ist (und wegen Rückwärtskompatibilität), kann die Umwandlung `<t>` ausgelassen werden, solange *t* keine Anführungszeichen (`"`) und keine Leerschläge (` `) enthält und nicht leer ist. 

### Beispiele
Die oben beschriebene Transformation erlaubt es uns auch, Tokens darzustellen, die in sich verschachtelt mehrere Tokens darstellen, da F() für beliebige(*) Zahlenfolgen funktioniert.

Beispiel (1):  
*L* = `<<t_0> <t_1> <t_2>> <<t_3> <t_4> <t_5>>`
In diesem Beispiel gilt unter anderem:  
R(*L*, 0) = `<t_0> <t_1> <t_2>`,  
R(R(*L*, 0), 0) = *t_0*,  
R(R(*L*, 2), 1) = *t_5*

Wenn also nun *t_0* = "2", *t_1* = "robin der fuchs", *t_2* = "player1", *t_3* = "5", *t_4* = "istref", *t_5* = ""

Dann ist *L* = `<2 "robin der fuchs%" player1> <5 istref "%">`
  = `"2 \"robin der fuchs%\" player1%" "5 istref \"%\"%"`

*L* ist die Response des Servers zum `LLPL`-Command, wenn Lobby 2 die Spieler mit Nickname "robin der fuchs" und "player1" enthält und Lobby 5 den Spieler mit Nickname "istref" und einen Leeren Slot enthält.

("robin der fuchs" ist kein Valider Nickname, aber für Demonstrationszwecke existiert er hier im Beispiel)


## Sonstige Eigenschaften unseres Protokolls
- Unser Protokoll ist Verbindungsbasiert, das heisst jeder Client hat eine feste Socket-Verbindung mit dem Server.  
Aus dieser Gegebenheit können viele Informationen von den Commands ausgelassen werden (z. B. wer der Absender ist), da sie sich jeweils aus dem Kontext ergeben. 
- Jede Request in unserem Netzwerkprotokoll sieht wie folgt aus:  
```<commandname> <arg_1> <arg_2> ... [<optionalarg_1> ...] [<debugmessage>]```  
wobei ``<commandname>`` aus vier Grossbuchstaben besteht, `<arg_N>` das N-te Argument und `<optionalarg_N>` das N-te optionale Argument ist.
- Jede Response hat den gleichen Aufbau wie eine Request, jedoch wird ein ``+`` am Anfang angehängt.

(*) Die Zeichenfolge darf keinen newline Buchstaben und auch kein Carriage Return beinhalten, aber ansonsten kann sie jede Zeichenfolge sein.


| Parameter                                                      | Beschreibung                                                                                                                                                                                                                                                                                                                                   |
|----------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `<joinsuccessful>`, `<valid>`, `<won>`, `<nicknameexists>`     | entweder "`t`" oder "`f`", stellt booleschen Wert dar (`true`/`false`)                                                                                                                                                                                                                                                                         |  
| `<nickname>`, `<whisperrecipient>`, `<sender>`, `<actualname>` | Spielername ohne Leerschlag, mit maximaler Länge 30. (stellt Spielername dar)                                                                                                                                                                                                                                                                  |
| `<msg>`                                                        | Zeichenfolge, die eine Nachricht darstellt.                                                                                                                                                                                                                                                                                                    |
| `<n>`                                                          | Ganze Zahl in Dezimaldarstellung. (stellt Lobbynummer dar)                                                                                                                                                                                                                                                                                     |
| `<drawstack>`                                                  | Entweder "`m`" oder "`e`" (stellt entweder Hauptstapel oder Austauschstapel dar)                                                                                                                                                                                                                                                               |
| `<tile>`                                                       | Repräsentation eines Spielsteins;<br/>*zahl* + `:` + *farbe* <br/> wobei *zahl* eine Zahl zwischen `0` und `13` und *farbe* entweder "`RED`", "`BLUE`", "`YELLOW`" oder "`BLACK`" ist. Speziell: bei der Zahl 0 handelt es sich um den Joker, egal in welcher Farbkombination.                                                                 |
| `<deck>` `<startdeck>`                                         | Repräsentation des Spielerdecks (muss immer Länge 24 haben);<br/>`<<tile_1> <tile_2> ... >` wobei `<tile_N>` die gleiche Darstellung wie `<tile>` hat. Ist kein Stein an einer gewissen Stelle, so wird dort ein leerer Wert dargestellt.                                                                                                      |
| `<exchangestacks>`                                             | Repräsentation des obersten Steins der vier Austauschstapel;<br>`<<tile_0> <tile_1> <tile_2> <tile_3>>` wobei *tile_N* den obersten Stein des Austauschstapels des Spielers mit Index N darstellt.                                                                                                                                             |
| `<games>`                                                      | Eine Repräsentation von Lobbys; <br/>`<<n_1>[:[<s_1>]] <n_2>[:[<s_2>]] ...>` wobei *n_M* die Lobbynummer und *s_M* die Anz. Spieler oder ein Spielername von Lobby M ist. <br>(Für genauere Beschreibung siehe `LGAM`-Command)                                                                                                                 |
| `<playerlist>`, `<nicknames>`                                  | Stellt Liste von Spielern dar; (können auch leere Werte sein im Falle von `<nicknames>`) <br/>`<<name_1> <name_2> ...>`                                                                                                                                                                                                                        |
| `<lobbieswithplayerlist>`                                      | Stellt Liste von Lobbies mit den jeweiligen Spielern dar; <br/>`<<lobby_1_names> <lobby_2_names> ...>` wobei *lobby_M_names* die M-te Lobby wie folgt darstellt: `<lobby_M_number> <lobby_M_name_1> <lobby_M_name_2> <lobby_M_name_4> ...`                                                                                                     |
| `<messagetype>`                                                | Stellt Art einer Chat-Nachricht dar; <br/> "`b`", "`l`", "`w`" für jeweils broadcast, lobby, und whisper                                                                                                                                                                                                                                       |
| `<highscores>`                                                 | Stellt eine Liste von Highscore-Einträgen dar; jedes Element besteht aus drei Teilen: Spielername, Datum des Highscores und der Highscore-Wert. Diese Elemente werden in der Form `<nickname> <date> <score>` zusammengefasst, wobei `<nickname>` der Name des Spielers, `<date>` das Datum und die Uhrzeit und `<score>` der Punktestand ist. |


---


# Login und Lobby
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

## Namen ändern
| Command           | Response             | Sender |
|-------------------|----------------------|--------|
| `NAME <nickname>` | `+NAME <actualname>` | Client |
### Beschreibung
Wird gesendet, wenn der User seinen Spielernamen zu `<nickname>` verändern will.  
In der Antwort ist das Argument `<actualname>` enthalten, welches den  
effektiven Spielernamen enthält, welchen der Spieler erhalten hat.
Dieser Command funktioniert gleich wie der ``LOGI``-Command.

Client: ```NAME Istref```  
Server: ```+NAME Istref_1```

🔴***TODO*** vielleicht ist dieser Command redundant (weil kein bedeutender Unterschied zu LOGI)

---

## Namensliste in Lobby bekannt geben
| Command            | Response | Sender |
|--------------------|----------|--------|
| `NAMS <nicknames>` | `+NAMS`  | Server |
### Beschreibung
Dieser Command beinhaltet die Nicknames der Spieler in der Lobby oder einen leeren Wert, wenn an dieser Stelle kein Spieler ist. 
Er wird gesendet, wenn ein Spieler aus der Lobby geht, wenn ein Spieler der Lobby beitretet 
und wenn ein Spieler in der Lobby seinen Namen ändert.  
`<nicknames>` hat immer 4 Werte, die entweder leer sind oder den Namen des Spielers an dieser Stelle in der Lobby beinhalten. 
### Beispiel
Server: ```NAMS "boran \"%\" istref robin%"```  
Client: ```+NAMS```

(Hier hat die Lobby nur 3 Spieler, der Spieler mit index 1 hat das Spiel verlassen)

---

## Namensliste anfordern
| Command | Response            | Sender |
|---------|---------------------|--------|
| `RNAM`  | `+RNAM <nicknames>` | Client |
### Beschreibung
Wird geschickt, wenn der Client die aktuelle namen in der Lobby haben will. 
`<nicknames>` hat immer 4 Werte, die entweder leer sind oder den Namen des Spielers an dieser Stelle in der Lobby beinhalten.
### Beispiel
Client: ```RNAM```  
Server: ```+RNAM "boran \"%\" istref robin%"```

(Hier hat die Lobby nur 3 Spieler, der Spieler mit index 1 hat das Spiel verlassen)

---

## Lobby verlassen
| Command           | Response        | Sender |
|-------------------|-----------------|--------|
| `LLOB <nickname>` | `+LLOB <valid>` | Client |
### Beschreibung
Dieser Command wird vom Client gesendet, wenn er seine momentane Lobby verlassen will.
Der Server überprüft zuerst, ob der Client überhaupt in einer Lobby ist und gibt dann als Antwort über
die Flag valid zurück, ob der Client überhaupt in einer Lobby war.

### Beispiel
Client: ```LLOB Pascal```  
Server: ```+LLOB t```

(Hier war Pascal tatsächlich in einer Lobby, bevor er diese dann erfolgreich verlassen hat)

---

## Informieren, dass ein Spieler die Lobby verlassen hat
| Command           | Response | Sender |
|-------------------|----------|--------|
| `LEFT <nickname>` | `+LEFT`  | Server |
### Beschreibung
Dieser Command wird vom Server gesendet, wenn ein Spieler die Lobby verlassen hat. 
In *nickname* wird der Nickname des Spielers mitgegeben, der das Spiel verlassen hat.
### Beispiel
Server: ```LEFT pascal```  
Client: ```+LEFT```

(Hier hat der Spieler mit Nickname ```pascal``` das Spiel verlassen)

---

## Informieren, dass ein Spieler der Lobby beigetreten ist
| Command           | Response | Sender |
|-------------------|----------|--------|
| `JOND <nickname>` | `+JOND`  | Server |
### Beschreibung
Dieser Command wird vom Server gesendet, wenn ein Spieler der Lobby beigetreten ist.
In *nickname* wird der Nickname des Spielers mitgegeben, der der Lobby beigetreten ist.
### Beispiel
Server: ```JOND pascal```  
Client: ```+JOND```

(Hier ist der Spieler mit Nickname ```pascal``` der Lobby beigetreten)


## Spieler auf Server auflisten
| Command | Response             | Sender |
|---------|----------------------|--------|
| `LPLA`  | `+LPLA <playerlist>` | Client |
### Beschreibung
Listet die Spieler auf, die mit dem Server verbunden sind. 

### Beispiel
Client: `LPLA`  
Server: `+LPLA "istref robin boran pascal "`

---

## Spieler in Lobby auflisten
| Command | Response                        | Sender |
|---------|---------------------------------|--------|
| `LLPL`  | `+LLPL <lobbieswithplayerlist>` | Client |
### Beschreibung
Listet in `<lobbieswithplayerlist>` die Spieler auf, die in den lobbies sind.

### Beispiel
Client: `LLPL`  
Server: `+LLPL "\"2 istref boran%\" \"6 \\"%\\" robin%\"%"`

*lobbieswithplayerlist* = "`"2 istref boran%" "6 \"%\" robin%"`"  
*lobby_1_number* = "`2`"  
*lobby_1_player_1* = "`istref`"  
*lobby_1_player_2* = "`boran`"  
*lobby_2_number* = "`6`"  
*lobby_2_player_1* = ""  
*lobby_2_player_2* = "`robin`"

(In Lobby 2 sind Spieler istref und boran, in Lobby 6 hat es einen leeren Slot und den Spieler robin)

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
Hat `<gamestatus>` den Wert `o`, so hat `<games>` folgende Form: `<<lobbynumber_1>:<playercount_1> <lobbynumber_2>:<playercount_2> ...>`.  
Hat `<gamestatus>` den Wert `r`, so hat `<games>` folgende Form: `<<lobbynumber_1> <lobbynumber_2> ...>`.  
Hat `<gamestatus>` den Wert `f`, so hat `<games>` folgende Form: `<<lobbynumber_1>:[<winner_1>] <lobbynumber_2>:[<winner_2>] ...>`. 
(zu beachten: nicht bei jedem Spiel gibt es einen Gewinner, in diesem Fall darf kein `<winner_N>` angegeben werden)

### Beispiel
Client: ```LGAM o```  
Server: ``+LGAM o "23:4 12:2 3:3%"``  
Client: ```LGAM r```  
Server: ``+LGAM r "51 23%"``  
Client: ```LGAM f```  
Server: ``+LGAM f "22:robin 1:nick 4:%"``

---

## Derzeitige Highscores anzeigen
| Command | Response             | Sender |
|---------|----------------------|--------|
| `HIGH`  | `+HIGH <highscores>` | Client |
### Beschreibung
Zeigt alle Highscores an, in aufsteigender Reihenfolge. Bei identischem Highscore ist der Spieler, 
der den Highscore früher aufgestellt hat weiter oben in der Platzierung.

### Beispiel
Client: `HIGH`  
Server: `" +HIGH "\"Robin 2024-04-20 10%\" \"Boran 2024-04-20 10%\" \"Pascal 2024-04-20 20%\"%""`

(Hier sehen wir, dass obwohl Robin und Boran  den gleichen Highscore haben, Robin vor Boran angezeigt wird, 
weil Robin zeitlich gesehen vor Boran den Highscore erstellt hat. 
Pascal belegt den 3.Platz, weil er den höchsten Highscore hat.)

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

### Beispiel
Client: `JLOB 42`  
Server: `+LOGO t`

---

## Logout
| Command | Response | Sender |
|---------|----------|--------|
| `LOGO`  | `+LOGO`  | Client |
### Beschreibung
Logout vom Server.

### Beispiel
Client: `LOGO`  
Server: `+LOGO`

---

# Die Spiellogik / den Spielablauf betreffende Commands

## Bereitschaft erklären
| Command | Response | Sender |
|---------|----------|--------|
| `REDY`  | `+REDY`  | Client |
### Beschreibung
Wird vom Client geschickt, wenn er parat zum Spielen ist.

### Beispiel
Client: `REDY`  
Server: `+REDY`

---

## Spiel beginnen
| Command                        | Response | Sender |
|--------------------------------|----------|--------|
| `STRT <startdeck> <playeridx>` | `+STRT`  | Server |
### Beschreibung
Wird vom Server geschickt, wenn alle Spieler in einer Lobby den `REDY`-Command an den Server geschickt haben,  
und 4 Spieler in der Lobby sind. In `<startdeck>` ist das Startdeck des Spielers enthalten (insgesamt 24 Werte, entweder leer oder einen Stein darstellend).  
Sind in diesem Deck 15 Steine, ist der empfangende Client der Startspieler.  
In `<playeridx>` wird eine Zahl übergeben, die den Index des empfangenden Clients darstellt, welcher für die Identifikation des Spielers wichtig ist. 


### Beispiel
Server: `STRT "8:RED 12:RED \"%\" 6:BLUE \"%\" 2:YELLOW \"%\" 5:YELLOW 6:YELLOW \"%\" 7:YELLOW 10:YELLOW \"%\" 12:YELLOW \"%\" 3:BLACK 4:BLACK \"%\" 6:BLACK 7:BLACK 8:BLACK \"%\" \"%\" \"%\"%" 3`  
Client: `+STRT`

---

## Spielzustand senden
| Command                                    | Response | Sender |
|--------------------------------------------|----------|--------|
| `STAT <exchangestacks> <currentplayeridx>` | `+STAT`  | Server |
### Beschreibung
Der Server schickt den obersten Stein der Austauschstapel in `<exchangestacks>`   
und der Index des Spielers, der an der Reihe ist in `<currentplayeridx>`. 

### Beispiel
Server: `STAT "\"%\" 4:BLACK 5:RED 3:YELLOW%" 1`  
Client: `+STAT`  
(Spieler mit playerindex 1 ist am Zug und der Erste exchangeStack ist leer, die anderen haben jeweils eine schwarze 4, eine rote 5 und eine gelbe 3)

---

## Spielzustand auf Anfrage senden
| Command | Response                                    | Sender |
|---------|---------------------------------------------|--------|
| `RSTA   | `+RSTA <exchangestacks> <currentplayeridx>` | Client |
### Beschreibung
Der Server schickt auf Anfrage vom client den jeweils obersten Stein der Austauschstapel in `<exchangestacks>`   
und der Index des Spielers, der an der Reihe ist in `<currentplayeridx>`.

### Beispiel
Client: `RSTA`  
Server: `+RSTA "9:BLACK \"%\" \"%\" \"%\"%" 0` 
(Spieler mit playerindex 0 ist am Zug und der Erste exchangeStack beinhaltet die schwarze 9, die anderen exchangeStacks sind leer.)

---


## Stein ziehen
| Command            | Response       | Sender |
|--------------------|----------------|--------|
| `DRAW <drawstack>` | `+DRAW <tile>` | Client |
### Beschreibung
Der Client gibt einen Stapel in `<drawstack>` an, von welchem er einen Stein haben will,  
der Server gibt den Stein in `<tile>` zurück. Ist der spieler nicht an der Reihe, oder probiert er ein zweites Mal, zu ziehen, so wird eine leere String als `<tile>` geschickt. 

### Beispiel
Client: `DRAW m`  
Server: `+DRAW 0:BLACK`  
(der Client zieht einen Stein vom Hauptstapel und erhält einen Joker)


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
Zuletzt wird auch noch vom Server das Exchangestack aktualisiert, auf welches man den Stein drauflegt.

### Beispiel
Client: `PUTT 4:RED "\"%\" 3:RED 11:RED 12:RED 13:RED 2:BLUE 3:BLACK \"%\" \"%\" 11:BLUE 12:BLUE \"%\" 2:YELLOW 7:YELLOW \"%\" 1:YELLOW 1:RED \"%\" \"%\" \"%\" 9:BLACK 9:RED \"%\" \"%\"%"`  
Server: `+PUTT t t`

---

## Spiel beenden (durch Sieg)
| Command           | Response | Sender |
|-------------------|----------|--------|
| `PWIN <nickname>` | `+PWIN`  | Server |
### Beschreibung
Command wird gesendet, wenn das Spiel vom Spieler mit Name `<nickname>` gewonnen wird.

### Beispiel
Server: `PWIN istref`  
Client: `+PWIN`


---

## Spiel beenden (durch Unentschieden)
| Command | Response | Sender |
|---------|----------|--------|
| `EMPT`  | `+EMPT`  | Server |

### Beschreibung
Command wird vom Server gesendet, wenn der Hauptstapel leer ist, und somit das
Spiel mit einem Unentschieden beendet werden muss. 

### Beispiel
Server: `EMPT`  
Client: `+EMPT`

---

# Die Kommunikation betreffend
## Chat-Nachricht senden (Client)
| Command                                         | Response                                                          | Sender |
|-------------------------------------------------|-------------------------------------------------------------------|--------|
| `CATC <messagetype> <msg> [<whisperrecipient>]` | `+CATC <messagetype> <msg> [<whisperrecipient> <nicknameexists>]` | Client |

### Beschreibung
Der CATC-Command wird vom Client an den Server geschickt, wenn der User eine Nachricht in den Chat schicken will.  
Sie enthält ein Argument `<messagetype>`, welches die Reichweite der Nachricht spezifiziert (`b`,`l`,`w` für jeweils broadcast, lobby, und whisper).  
Falls dieses den Wert `w` hat, muss der optionale Parameter `<whisperrecipient>` definiert werden.  
Hat `<messagetype>` den Wert `b` (für Broadcast), wird ein `CATS`-Command an alle Clients auf dem Server weitergeleitet, 
falls `l` (für Lobby) nur an die in derselben Lobby und falls `w` (für Whisper) nur an den Spieler, welchen den Nickname `<whisperrecipient>` hat.  
Der Parameter `<msg>` ist der Inhalt der Nachricht.
Die response vom Server beinhaltet die gleichen Argumente, die er auch erhalten hat, ausser `<messagetype>` ist `w`, 
dann bekommt sie zusätzlich noch eine flag `<nicknameexists>` (entweder `t` oder `f`), welche angibt, ob der spezifizierte Nickname existiert.

### Beispiel
Client: `CATC w "Tom hat mir folgendes gesagt: \"Nick ist mühsam\"%" robin`  
Server: `+CATC w "Tom hat mir folgendes gesagt: \"Nick ist mühsam\"%" robin t`

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
Server: `CATS w "Tom hat mir folgendes gesagt: \"Nick ist mühsam\"%" boran`
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

### Beispiel
Server: `PING`  
Client: `+PING`

Client: `PING`  
Server: `+PING`

---

## Cheating
| Command | Response       | Sender |
|---------|----------------|--------|
| `WINC`  | `+WINC <deck>` | Client |
### Beschreibung
Wenn ein Client den `WINC`-Command schickt, dann bekommt er ein Deck, welches alle Steine hat, um eine Gewinnkonfiguration zu erreichen.
Dabei wird sein Deck auf dem Server angepasst, sodass alle zukünftigen Spielzüge legal sind. 


Server: `WINC`  
Client: `+WINC "0:BLACK \"%\" 1:RED 2:RED 3:RED 4:RED 1:BLUE 2:BLUE 3:BLUE 4:BLUE 1:YELLOW 2:YELLOW 3:YELLOW 4:YELLOW 5:YELLOW \"%\" \"%\" \"%\" \"%\" \"%\" \"%\" \"%\" \"%\" \"%\" %"`

---