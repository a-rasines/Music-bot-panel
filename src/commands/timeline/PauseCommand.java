package commands.timeline;

import botinternals.Client;
import interfaces.NoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class PauseCommand implements NoParamCommand{
	@Override
	public void execute(Guild g, MessageChannel mc, Member m) {
		if (!checks(g,m,mc))return;
        
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
		musicManager.scheduler.player.setPaused(true);
		Client.sendInfoMessage(mc, "Modo pausado", "El modo pausado ha sido activado, para quitarlo usa resume");
		
	}

	@Override
	public String getName() {
		return "Pause";
	}

	@Override
	public String getHelp() {
		return "Pausa la canción que está sonando";
	}
	
}
