package com.mibot.service;

import java.util.HashMap;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import dev.lavalink.youtube.YoutubeAudioSourceManager;
// Importamos todos los clientes
import dev.lavalink.youtube.clients.*;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class PlayerManager {

    private static PlayerManager instance;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager(){
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);

        // 2. Cargamos YouTube con el cliente salvador: AndroidVr
        YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(true, 
            new Music(), 
            new Web(),
            new AndroidVr() // <-- ¡ESTE SÍ SOPORTA OAUTH Y NO ESTÁ BLOQUEADO!
        );
        
        youtube.useOauth2(null, true);
        
        this.audioPlayerManager.registerSourceManager(youtube);

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
    }

    public static PlayerManager getInstance(){
        if (instance == null){
            instance = new PlayerManager();
        }
        return instance;
    }

    public GuildMusicManager getMusicManager(Guild guild){
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public void loadAndPlay(MessageChannel channel, Guild guild, String trackUrl){
        GuildMusicManager musicManager = getMusicManager(guild);
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler(){
            @Override
            public void trackLoaded(AudioTrack track){
                musicManager.scheduler.queue(track);
                channel.sendMessage("Añadiendo cancion a la cola: **" + track.getInfo().title + "**").queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist){
                AudioTrack firstTrack = playlist.getSelectedTrack();
                if (firstTrack == null){
                    firstTrack = playlist.getTracks().get(0);
                }
                musicManager.scheduler.queue(firstTrack);
                channel.sendMessage("Añadiendo cancion a la cola: **" + firstTrack.getInfo().title + "**").queue();
            }

            @Override
            public void noMatches(){
                channel.sendMessage("No encontré ninguna cancion en ese link. ¿Podrias volver a pasarmelo?").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception){
                channel.sendMessage("Ocurrio un error al cargar la cancion: " + exception.getMessage()).queue();
            }
        });
    }
}