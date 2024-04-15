# Konsolenbasierte & verschlüsselter Gruppenchat in Java

## Meta

**Projektname:** Konsolenbasierte & verschlüsselter Gruppenchat in Java

**Modul:**
Verteilte Systeme, Semester 6, DHBW Karlsruhe

**Projektmitglieder:**

- Robin Pfaff ()
- Niklas Trenz ()

**GitHub Link:** [GitHub java-group-chat](https://github.com/robin13901/java-group-chat)

## Projekt Beschreibung

> Besonderes Augenmerk auf (für 0.5 besser): Dokumentation | Verständlicher Quellcode | gute UX | Effizienz | ... -> bei uns die Verschlüsselung

<u>Anforderungen:</u>

- Konsolenbasierter Gruppenchat in Java
- Server verwaltet Nachrichten der Gruppe
- Beliebig viele Clients können sich in der Gruppe (=>Server) anmelden
- Nachrichten der Clients werden mit Namen auf der Konsole jedes Clients angezeigt

<u>Geschätzer Zeitaufwand:</u> 20 PS

### Ausführung

> Wie kann er die Software ausführen?

```shell
command to execute the software
```

### Screenshots

> Die Screenschots sollen das Programm beispielsweise darstellen

## Architektur

### Verschlüsselung

Zur Verschlüsselung der Nachrichten im Java-Groupchat wird das RSA Verfahren verwendet. Vorgehen:

1. Jeder Nutzer erzeugt für sich ein Key Pair (Public + Private)
2. Der public Key wird an den Server geschicht, welcher diesen an alle anderen Clients verteilt
3. Jeder Client hat einen KeyStore, welcher die Namen der Nutzer und deren public Keys enthält
4. Wenn ein Nutzer eine Nachricht verschickt, wird diese jeweils mit den public Keys der anderen Clients verschlüsselt
   - Eine Nachricht ist fogend aufgebaut:
        ```<name des senders> <timestamp> <name client #1> <verschlüsselte nachricht mit public key des clients #1> <name client #2> <verschlüsselte nachricht mit public key des clients #2> ...```
5. Der Server leitet dann die Nachricht an jeden Client weiter
6. Jeder Client entschlüsselt die Nachricht die für ihn verschlüsselt wurde mit seinem private Key

![Architektur der Verschlüsselung](assets/java-group-chat-Verschlüsselung.png)

### Server-Client-Architektur
