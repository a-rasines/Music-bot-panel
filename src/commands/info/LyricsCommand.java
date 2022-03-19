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
	AudioTrack track;
	String lyrics;
	@Override
	public void execute(Guild g, MessageChannel mc, Member m, boolean slash) {
		GuildMusicManager gmm = PlayerManager.getInstance().getMusicManager(g);
		track = gmm.audioPlayer.getPlayingTrack(); 
		lyrics = Client.genius.search(track.getInfo().title).get(0).getText();
		if(!slash)
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

	@Override
	public Reply reply(Guild g, MessageChannel mc, Member m) {
		return new Reply(Client.getInfoMessage("Letra de "+track.getInfo().title, lyrics));
	}
	@Override
	public boolean replyFirst() {
		return false;
	}

}
