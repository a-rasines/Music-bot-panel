package commands.timeline;

import botinternals.Client;
import interfaces.NonPartyNoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class StopCommand implements NonPartyNoParamCommand{
	
	@Override
	public String getName() {
		return "Stop (end)";
	}

	@Override
	public String getHelp() {
		return "Termina la canción y elimina la lista";
	}

	@Override
	public void execute0(SlashCommandInteractionEvent event) {
		if (!checks(event.getGuild(),event.getMember(),event.getMessageChannel()))return;

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.player.setPaused(false);
    	Client.sendInfoMessage(event, "Stop", "El bot se ha detenido y la lista ha sido eliminada");
		
	}			
}