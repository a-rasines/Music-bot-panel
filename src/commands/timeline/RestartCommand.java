package commands.timeline;

import botinternals.Client;
import interfaces.NoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class RestartCommand implements NoParamCommand{

	@Override
	public void execute(Guild g, MessageChannel mc, Member m) {
		if (!checks(g,m,mc))return;
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
		musicManager.scheduler.player.getPlayingTrack().setPosition(0);
		Client.sendInfoMessage(mc, "Reiniciando", musicManager.scheduler.player.getPlayingTrack().getInfo().title +" se esta volviendo a reproducir desde cero");
	}

	@Override
	public String getName() {
		return "Restart";
	}

	@Override
	public String getHelp() {
		return "Reinicia la canción que está sonando";
	}
	
}