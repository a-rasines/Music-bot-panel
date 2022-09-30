package commands.queue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import botinternals.Client;
import interfaces.Command;
import lavaplayer.PlayerManager;
import lavaplayer.TrackScheduler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class MoveCommand implements Command{

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		long oldPos = event.getOption("oldposition").getAsLong()-1;
		OptionMapping newPosOption = event.getOption("newposition");
		TrackScheduler ts = PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler;
		long newPos = newPosOption==null?ts.queue.size()-1:newPosOption.getAsLong()-1;
		AudioTrack at = ts.getFromQueue((int) oldPos);
		ts.removeFromQueue((int) oldPos).insertIntoQueue((int) newPos, at);
		Client.sendInfoMessage(event, at.getInfo().title+" movido a la posición "+String.valueOf(newPos+1), "Se ha movido la canción "+at.getInfo().title+" de la posición "+String.valueOf(oldPos+1)+" a "+String.valueOf(newPos+1));
	}

	@Override
	public String getName() {
		return "move";
	}

	@Override
	public String getHelp() {
		return "Mueve una canción de la lista de una posición a otra";
	}

	@Override
	public OptionData[] params() {
		return new OptionData[] {new OptionData(OptionType.INTEGER, "oldposition", "Antigua posición en la cola", true), new OptionData(OptionType.INTEGER, "newposition", "Nueva posición en la cola, última por defecto", false)};
	}
	
}
