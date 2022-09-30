package commands.timeline;

import botinternals.Client;
import interfaces.NonPartyNoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class RestartCommand implements NonPartyNoParamCommand{

	@Override
	public String getName() {
		return "Restart";
	}

	@Override
	public String getHelp() {
		return "Reinicia la canción que está sonando";
	}

	@Override
	public void execute0(SlashCommandInteractionEvent event) {
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
		musicManager.scheduler.player.getPlayingTrack().setPosition(0);
		Client.sendInfoMessage(event, "Reiniciado", musicManager.scheduler.player.getPlayingTrack().getInfo().title +" se esta volviendo a reproducir desde cero");
		
	}
	
}