# Discord Music Bot

Bot de música para Discord escrito en Java. Se conecta al canal de voz del usuario y
reproduce audio desde YouTube con LavaPlayer. Incluye un servidor HTTP mínimo que
mantiene el proceso vivo en plataformas como Render.

## Comandos

| Comando | Qué hace |
|---|---|
| `!ping` | Responde `Pong` y confirma que el bot está en línea |
| `!entrar` | Conecta el bot al canal de voz donde está el usuario |
| `!play <url>` / `!p <url>` | Encola la canción y comienza la reproducción |

## Stack

- **Java 17** + **Maven**
- **JDA 6.4.1** — cliente de Discord, con soporte del protocolo DAVE
- **LavaPlayer 2.2.3** + **youtube-source 1.18.0** — reproducción de audio
- **libdave-jvm** — cifrado de voz E2EE que Discord exige desde 2026
- **java-dotenv** — lectura del token en desarrollo local
- **Docker** — imagen de despliegue

## Requisitos

- JDK 17
- Maven 3.8+
- Una aplicación de Discord con el intent **MESSAGE CONTENT** habilitado
- El token del bot

## Configuración

Crea un `.env` en la raíz (está ignorado por git):

```env
DISCORD_TOKEN=tu_token_de_discord
```

En producción el token se lee de la variable de entorno `DISCORD_TOKEN`. El puerto
del servidor HTTP sale de `PORT` (por defecto `8080`).

## Ejecutar

```bash
mvn clean package
java -jar target/discord-music-bot-1.0-SNAPSHOT.jar
```

Con Docker:

```bash
docker build -t discord-music-bot .
docker run --env-file .env -p 8080:8080 discord-music-bot
```

## Notas

- `pom.xml` incluye las nativas de libdave para Windows y Linux. Si el bot corre en
  macOS, hay que descomentar `natives-darwin`.
- El servidor HTTP responde `200` en `/`. No sirve datos: existe para que la
  plataforma de hosting valide el puerto y no apague el proceso.
- Si el token estuvo expuesto en algún repositorio, hay que rotarlo en el portal de
  Discord antes de usar esta versión.
