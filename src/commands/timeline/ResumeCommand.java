package commands.timeline;

import botinternals.Client;
import interfaces.NoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ResumeCommand implements NoParamCommand{

	@Override
	public void execute(Guild g, MessageChannel mc, Member m) {
 
        if (!checks(g,m,mc))return;
        
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
		musicManager.scheduler.player.setPaused(false);
		Client.sendInfoMessage(mc, "Modo pausado", "El modo pausado ha sido desactivado");
	}

	@Override
	public String getName() {
		return "Resume (continue)";
	}

	@Override
	public String getHelp() {
		return "Desactiva el modo pausado";
	}
}
