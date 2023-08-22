package commands.bot;

import botinternals.Client;
import interfaces.NoParamCommand;
import lavaplayer.PlayerManager;
import lavaplayer.TrackScheduler;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class LeaveCommand implements NoParamCommand{
	@Override
	public String getName() {
		return "Leave (l / disconnect / d)";
	}

	@Override
	public String getHelp() {
		return "Desconecta el bot del canal de voz";
	}


	@Override
	public void execute(SlashCommandInteractionEvent event) {
		if(!checks(event.getGuild(),event.getMember(),event.getChannel()))return;
		event.getGuild().getAudioManager().closeAudioConnection();
		TrackScheduler ts= PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler;
		ts.queue.clear();
		ts.nextTrack();
		Client.sendInfoMessage(event, "Desconectado", "El bot ha sido desconectado manualmente del actual canal");
		Client.jda.getPresence().setActivity(Activity.listening("the beauty of silence"));
		
	}
}
