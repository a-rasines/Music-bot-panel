package commands.bot;

import interfaces.NoParamCommand;
import lavaplayer.PlayerManager;
import lavaplayer.TrackScheduler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class LeaveCommand implements NoParamCommand{
	@Override
	public void execute(Guild g, MessageChannel mc, Member m) {
		if(!checks(g,m,mc))return;
		g.getAudioManager().closeAudioConnection();
		TrackScheduler ts= PlayerManager.getInstance().getMusicManager(g).scheduler;
		ts.queue.clear();
		ts.nextTrack();
		
	}
	

	@Override
	public String getName() {
		return "Leave (l / disconnect / d)";
	}

	@Override
	public String getHelp() {
		return "Desconecta el bot del canal de voz";
	}
}
