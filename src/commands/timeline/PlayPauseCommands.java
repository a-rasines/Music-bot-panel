package commands.timeline;

import botinternals.Client;
import interfaces.NonPartyNoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PlayPauseCommands implements NonPartyNoParamCommand{
	private boolean newState;
	public PlayPauseCommands(boolean stopped) {
		newState = stopped;
	}

	@Override
	public String getName() {
		return "Pause";
	}

	@Override
	public String getHelp() {
		return "Pausa la canción que está sonando";
	}
	@Override
	public void execute0(SlashCommandInteractionEvent event) {
		if (!checks(event.getGuild(),event.getMember(),event.getMessageChannel()))return;
        
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
		musicManager.scheduler.player.setPaused(newState);
		Client.sendInfoMessage(event, "Modo pausado", "El modo pausado ha sido activado, para quitarlo usa resume");	
	}
}
