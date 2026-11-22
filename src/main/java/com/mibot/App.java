package com.mibot;

import io.github.bpj.BPJ;
import io.github.cdimascio.dotenv.Dotenv;
import moe.kyokobot.libdave.NativeDaveFactory;
import moe.kyokobot.libdave.jda.LDJDADaveSessionFactory;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.audio.AudioModuleConfig;
import net.dv8tion.jda.api.requests.GatewayIntent;

// Importaciones para el servidor fantasma de Render
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.OutputStream;

public class App {
    public static void main( String[] args )
    {   
        // 1. INICIAR EL SERVIDOR WEB FANTASMA PARA RENDER
        try {
            // Render inyecta la variable PORT, localmente usamos 8080
            String port = System.getenv("PORT");
            if (port == null) port = "8080";
            
            HttpServer server = HttpServer.create(new InetSocketAddress(Integer.parseInt(port)), 0);
            server.createContext("/", exchange -> {
                String response = "¡El Bot de música está vivo y funcionando!";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            });
            server.start();
            BPJ.println("Servidor web dummy iniciado en el puerto " + port);
        } catch (Exception e) {
            BPJ.println("Error iniciando servidor web: " + e.getMessage());
        }

        // 2. LEER EL TOKEN (Compatible con Render y Local)
        String token = System.getenv("DISCORD_TOKEN"); // Intenta leer de Render
        
        if (token == null || token.isEmpty()) {
            // Si no está en Render, lee el archivo .env local
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            token = dotenv.get("DISCORD_TOKEN");
        }

        // 3. INICIAR EL BOT
        try {
            JDABuilder builder = JDABuilder.createDefault(token);
            builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);

            builder.setAudioModuleConfig(
                new AudioModuleConfig()
                    .withDaveSessionFactory(new LDJDADaveSessionFactory(new NativeDaveFactory()))
            );

            builder.addEventListeners(new com.mibot.controller.ComandoListener());
            builder.build();
            BPJ.println("El bot funciona bien en discord");
        } catch (Exception e){
            BPJ.println("Error, el bot no funcionó: " + e);
        }
    }
}