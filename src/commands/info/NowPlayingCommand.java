package commands.info;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import botinternals.Client;
import interfaces.NoParamCommand;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class NowPlayingCommand implements NoParamCommand{

	@Override
	public String getName() {
		return "Now playing (np)";
	}

	@Override
	public String getHelp() {
		return "Indica lo que se está escuchando en el momento";
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		if (!checks(event.getGuild(),event.getMember(),event.getChannel()))return;
        AudioTrack track = PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer.getPlayingTrack();
        if (track == null) {
        	Client.sendInfoMessage(event, "Now playing", "No hay nada reproduciondose ahora mismo");
        }else {
        	AudioTrackInfo info = track.getInfo();
        	Client.sendInfoMessage(event, "Now playing", info.title +" - "+info.author+ " ("+info.uri+")",formatTime(info.length));
        }
	}

}
