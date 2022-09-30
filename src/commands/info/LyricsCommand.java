package commands.info;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import botinternals.Client;
import interfaces.NoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class LyricsCommand implements NoParamCommand{
	AudioTrack track;
	String lyrics;

	@Override
	public String getName() {
		return "Lyrics (l)";
	}

	@Override
	public String getHelp() {
		return "Manda la letra de la canción sonando";
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		GuildMusicManager gmm = PlayerManager.getInstance().getMusicManager(event.getGuild());
		track = gmm.audioPlayer.getPlayingTrack(); 
		lyrics = Client.genius.search(track.getInfo().title).get(0).getText();
		Client.sendInfoMessage(event, "Letra de "+track.getInfo().title, lyrics);
	}

}
