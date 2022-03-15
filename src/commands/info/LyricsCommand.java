package commands.info;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import botinternals.Client;
import interfaces.NoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class LyricsCommand implements NoParamCommand{

	@Override
	public void execute(Guild g, MessageChannel mc, Member m) {
		GuildMusicManager gmm = PlayerManager.getInstance().getMusicManager(g);
		AudioTrack track = gmm.audioPlayer.getPlayingTrack(); 
		String lyrics = Client.genius.search(track.getInfo().title).get(0).getText();
		Client.sendInfoMessage(mc, "Letra de "+track.getInfo().title, lyrics);
	}

	@Override
	public String getName() {
		return "Lyrics (l)";
	}

	@Override
	public String getHelp() {
		return "Manda la letra de la canción sonando";
	}

}
