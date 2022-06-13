package commands.timeline;

import botinternals.Client;
import interfaces.NonPartyNoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class StopCommand implements NonPartyNoParamCommand{
	@Override
	public void execute0(Guild g, MessageChannel mc, Member m, boolean slash) {
		if (!checks(g,m,(TextChannel) mc))return;

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);

        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.player.setPaused(false);
        if(!slash)
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
	@Override
	public Reply reply0(Guild g, MessageChannel mc, Member m) {
		return new Reply(Client.getInfoMessage("Stop", "El bot se ha detenido y la lista ha sido eliminada"));
	}
	@Override
	public boolean replyFirst() {
		return false;
	}			
}