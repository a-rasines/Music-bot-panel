package commands.timeline;

import botinternals.Client;
import interfaces.NoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class StopCommand implements NoParamCommand{
	@Override
	public void execute(Guild g, MessageChannel mc, Member m, boolean slash) {
		if (!checks(g,m,(TextChannel) mc))return;

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);

        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.player.setPaused(false);

        Client.sendInfoMessage(mc, "Stop", "El bot se ha detenido y la lista ha sido eliminada");
		
	}
	@Override
	public String getName() {
		return "Stop (end)";
	}

	@Override
	public String getHelp() {
		return "Termina la canción y elimina la lista";
	}			
}