package commands.bot;

import botinternals.Client;
import interfaces.NoParamCommand;
import lavaplayer.PlayerManager;
import lavaplayer.TrackScheduler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class LeaveCommand implements NoParamCommand{
	@Override
	public void execute(Guild g, MessageChannel mc, Member m, boolean slash) {
		if(!checks(g,m,mc))return;
		g.getAudioManager().closeAudioConnection();
		TrackScheduler ts= PlayerManager.getInstance().getMusicManager(g).scheduler;
		ts.queue.clear();
		ts.nextTrack();
		Client.sendInfoMessage(mc, "Desconectado", "El bot ha sido desconectado manualmente del actual canal");
		
	}
	

	@Override
	public String getName() {
		return "Leave (l / disconnect / d)";
	}

	@Override
	public String getHelp() {
		return "Desconecta el bot del canal de voz";
	}


	@Override
	public Reply reply(Guild g, MessageChannel mc, Member m) {
		return new Reply(Client.getInfoMessage("Desconectando", "Intentandod desconectarse de "+g.getAudioManager().getConnectedChannel()==null?"null":g.getAudioManager().getConnectedChannel().getName()));
	}


	@Override
	public boolean replyFirst() {
		// TODO Auto-generated method stub
		return false;
	}
}
