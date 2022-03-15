package commands.info;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import botinternals.Client;
import interfaces.NoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class NowPlayingCommand implements NoParamCommand{
	@Override
	public void execute(Guild g, MessageChannel mc, Member m) {
		if (!checks(g,m,mc))return;

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        final AudioTrack track = audioPlayer.getPlayingTrack();

        if (track == null) {
            Client.sendInfoMessage(mc, "Now playing", "No hay nada reproduciondose ahora mismo");
            return;
        }
        final AudioTrackInfo info = track.getInfo();
        Client.sendInfoMessage(mc, "Now playing", info.title +" - "+info.author+ " ("+info.uri+")",formatTime(info.length));
	}
	

	@Override
	public String getName() {
		return "Now playing (np)";
	}

	@Override
	public String getHelp() {
		return "Indica lo que se está escuchando en el momento";
	}

}
