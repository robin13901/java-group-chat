# Konsolenbasierter & verschlüsselter Gruppenchat in Java

## Meta

**Projektname:** Konsolenbasierte & verschlüsselter Gruppenchat in Java

**Modul:**
Verteilte Systeme, Semester 6, DHBW Karlsruhe

**Projektmitglieder:**

- Robin Pfaff
- Niklas Trenz

**GitHub Link:** [GitHub java-group-chat](https://github.com/robin13901/java-group-chat)

## Projekt Beschreibung

<u>Anforderungen:</u>

- Konsolenbasierter Gruppenchat in Java
- Server verwaltet Nachrichten der Gruppe
- Beliebig viele Clients können sich in der Gruppe (=>Server) anmelden
- Nachrichten der Clients werden mit Namen auf der Konsole jedes Clients angezeigt

<u>Geschätzer Zeitaufwand:</u> 20 PS

<u>Besonderen Fokus haben wir gelegt auf:</u> die Verschlüsselung der Nachrichten durch den RSA Algorithmus

### Ausführung

1. Starte Server
2. Starte Clients

> ⚠️ Im Ordner `/lib` befindet sich die benötigte JSON-Bibliothek als .jar-Datei.

### Screenshots

> Im Ordner `/docs` befindet sich ein beispiel Video, welches das Programm demonstriert. Zusätzlich sind dort die Screenshots abgelegt.

1. Die Clients connecten auf den Server
![Schritt 01](docs/01-clients_connected_to_server.png)
2. Client A sendet Nachricht
![Schritt 02](docs/02-client_a_send_message.png)
3. Client A sendet lange Nachricht
![Schritt 03](docs/03-client_a_send_large_message.png)
4. Client B sendet lange Nachricht
![Schritt 04](docs/04-client_b_send_large_message.png)
5. Clients disconnecten
![Schritt 05](docs/05-clients_disconnect.png)

## Architektur

### Verschlüsselung

Zur Verschlüsselung der Nachrichten im Java-Groupchat wird das RSA Verfahren verwendet. Vorgehen:

1. Jeder Nutzer erzeugt für sich ein Key Pair (Public + Private)
2. Der public Key wird an den Server geschicht, welcher diesen an alle anderen Clients verteilt
3. Jeder Client hat einen KeyStore, welcher die Namen der Nutzer und deren public Keys enthält
4. Wenn ein Nutzer eine Nachricht verschickt, wird diese jeweils mit den public Keys der anderen Clients verschlüsselt
   - Eine Nachricht ist fogend aufgebaut:

        ```json
        {
                "msgType": "USER_MESSAGE",
                "senderName": "senderName",
                "timestamp": "01.01.2024 00:00:00.000",
                "content": {
                        "Alice": "Message Hash for Alice",
                        "Bob": "Message Hash for Bob",
                        "...": "...",
                }
        }
        ```

5. Der Server leitet dann die Nachricht an jeden Client weiter
6. Jeder Client entschlüsselt die Nachricht die für ihn verschlüsselt wurde mit seinem private Key

![Architektur der Verschlüsselung](docs/java-group-chat-Verschlüsselung.png)

### Server-Client-Architektur

Die Server-Client-Architektur des Java-Groupchats ermöglicht die Kommunikation zwischen mehreren Clients über einen zentralen Server. Hier sind die Schlüsselprinzipien:

*Server-Client-Verbindungsaufbau:*

- Der Server wird gestartet und wartet auf eingehende Verbindungen von Clients.
- Clients initiieren eine Verbindung mit dem Server, um sich zu registrieren und am Gruppenchat teilzunehmen.

*Client-Registrierung:*

- Bei der Registrierung meldet sich ein Client beim Server an und sendet seine Benutzerinformationen, einschließlich Benutzername und Public Key für die Verschlüsselung.
- Der Server registriert den Client und speichert seine Benutzerinformationen für zukünftige Kommunikation.

*Schlüsselaustausch:*

- Während der Registrierung erfolgt ein Schlüsselaustausch, bei dem der Client seinen Public Key an den Server sendet. Der Server verteilt dann diesen Public Key an alle anderen Clients im Chat.
- Dies ermöglicht es jedem Client, Nachrichten sicher zu verschlüsseln, um die Privatsphäre und Sicherheit der Kommunikation zu gewährleisten.

*Nachrichtenübertragung:*

- Ein Client kann eine Nachricht an den Server senden, die dann an alle anderen Clients weitergeleitet wird.
- Die Nachricht wird mit den Public Keys der anderen Clients verschlüsselt, um sicherzustellen, dass nur die beabsichtigten Empfänger sie entschlüsseln können.

*Client-Disconnect:*

- Clients haben die Möglichkeit, sich vom Server abzumelden und die Verbindung zu beenden, wenn sie den Gruppenchat verlassen möchten.
- Der Server aktualisiert seine Liste registrierter Clients entsprechend und informiert die anderen Clients über Änderungen in der Teilnehmerliste.

Die Server-Client-Architektur ermöglicht eine effiziente und sichere Kommunikation zwischen den Teilnehmern des Groupchats, wobei der Server als Vermittler fungiert und die Nachrichtenübertragung koordiniert.

### Mögliche Verbesserungen

Optimierungspotential haben folgende Aspekte:

- Bei der Übertragung von Nachrichten wird die Methode `broadcastMessage` verwendet. Dadurch wird eine Nachricht komplett an alle Clients geschickt. Da jede Nachricht jedoch alle verschlüsselten Nachrichten für jeden Nutzer enthält, könnte für einen Nutzer nur der relvante Hash gesendet werden. Dies würde bei vielen Nutzern die Netzlast deutlich reduzieren.
- Durch den Befehl `/w <user_name> <msg>` könnte ein Nutzer eine private Nachricht an den spezifizierten Nutzer senden.
