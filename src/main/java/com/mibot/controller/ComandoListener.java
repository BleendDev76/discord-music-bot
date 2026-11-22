package com.mibot.controller;

import com.mibot.service.PlayerManager;
import io.github.bpj.BPJ;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ComandoListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.getAuthor().isBot()){
            return;
        }

        String mensaje = event.getMessage().getContentRaw().trim();

        BPJ.println("El bot detectó el mensaje: " + mensaje);


        // Comando Ping Pong
        if (mensaje.equalsIgnoreCase("!ping")){
            BPJ.println("El mensaje llegó, intentando mandar el mensaje de respuesta");
            event.getMessage().reply("Pong, funciono").queue(
                exito -> BPJ.println("Mensaje envidado a discord"),
                error -> BPJ.println("Error al enviar mensaje a discord: " + error.getMessage())
            );
        }

        // Comando Entrar al Voice
        if (mensaje.equalsIgnoreCase("!entrar")){
            Member miembro = event.getMember();
            GuildVoiceState estadoVoice = miembro.getVoiceState();
            
            if(estadoVoice != null && estadoVoice.inAudioChannel()){
                AudioChannel canalVoz = estadoVoice.getChannel();
                event.getGuild().getAudioManager().openAudioConnection(canalVoz);
                event.getMessage().reply("Conectado a **" + canalVoz.getName() + "** Listo para ejecutar musica").queue();
            } else {
                event.getMessage().reply("Debes estar en un canal de voz primero").queue();  
            }
        }

        // Comando Play
        if (mensaje.toLowerCase().startsWith("!play") || mensaje.toLowerCase().startsWith("!p")){
            String[] partes = mensaje.split(" ", 2);
            if (partes.length < 2){
                event.getMessage().reply("Oe pete, te olvidate de poner el link. Usa: `!play <link>´").queue();
                return;
            }

            String link = partes[1];

            Member miembro = event.getMember();
            GuildVoiceState estadoVoice = miembro.getVoiceState();

            if(estadoVoice != null && estadoVoice.inAudioChannel()){
                AudioChannel canalVoz = estadoVoice.getChannel();

                // ✅ CORRECCIÓN: Solo conectarse si el bot NO está ya en un canal de voz
                if (!event.getGuild().getAudioManager().isConnected()){
                    event.getGuild().getAudioManager().openAudioConnection(canalVoz);
                }

                PlayerManager.getInstance().loadAndPlay(event.getChannel(), event.getGuild(), link);
            } else {
                event.getMessage().reply("Debes de meterte a un canal de voz para pedir musica").queue();
            }
        }
    }
}
