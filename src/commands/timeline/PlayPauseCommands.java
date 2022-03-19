package commands.timeline;

import botinternals.Client;
import interfaces.NoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class PlayPauseCommands implements NoParamCommand{
	private boolean newState;
	public PlayPauseCommands(boolean stopped) {
		newState = stopped;
	}
	@Override
	public void execute(Guild g, MessageChannel mc, Member m, boolean slash) {
		if(slash)return;
		if (!checks(g,m,mc))return;
        
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
		musicManager.scheduler.player.setPaused(newState);
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

	@Override
	public Reply reply(Guild g, MessageChannel mc, Member m) {
		MessageEmbed e = checks(g,m);
		if(e!=null)return new Reply(e);
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
		musicManager.scheduler.player.setPaused(newState);
		return new Reply(Client.getInfoMessage("Modo pausado", "El modo pausado ha sido activado, para quitarlo usa resume"));
	}

	@Override
	public boolean replyFirst() {
		return true;
	}
	
}
