package commands.timeline;

import botinternals.Client;
import interfaces.NonPartyNoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class RestartCommand implements NonPartyNoParamCommand{
	@Override
	public void execute0(Guild g, MessageChannel mc, Member m, boolean slash) {
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
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

	@Override
	public Reply reply0(Guild g, MessageChannel mc, Member m) {
		MessageEmbed e = checks(g,m);
		if(e != null)return new Reply(e);
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
		musicManager.scheduler.player.getPlayingTrack().setPosition(0);
		return new Reply(Client.getInfoMessage("Reiniciando", musicManager.scheduler.player.getPlayingTrack().getInfo().title +" se esta volviendo a reproducir desde cero"));
	}

	@Override
	public boolean replyFirst() {
		// TODO Auto-generated method stub
		return false;
	}
	
}