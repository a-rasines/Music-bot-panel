package commands.queue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import botinternals.Client;
import interfaces.Command;
import lavaplayer.PlayerManager;
import lavaplayer.TrackScheduler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class MoveCommand implements Command{

	@Override
	public void execute(MessageReceivedEvent msg) {
		String[] params = msg.getMessage().getContentDisplay().split(" ");
		if(params.length < 2) {
			Client.sendErrorMessage(msg.getChannel(), "Se necesita como mínimo la posición inicial");
		}
		if(!checks(msg.getGuild(), msg.getMember(), msg.getChannel()))return;
		if(!isInt(params[1]) || (params.length >2 && !isInt(params[2]))) {
			Client.sendErrorMessage(msg.getChannel(), "Las posiciones tienen que ser números");
		}
		TrackScheduler ts = PlayerManager.getInstance().getMusicManager(msg.getGuild()).scheduler;
		int oldPos = Integer.parseInt(params[1])-1;
		int newPos = params.length > 2? Integer.parseInt(params[2])-1:ts.queue.size()-1;
		AudioTrack at = ts.getFromQueue((int) oldPos);
		ts.removeFromQueue((int) oldPos).insertIntoQueue((int) newPos, at);
		Client.sendInfoMessage(msg.getChannel(), at.getInfo().title+" movido a la posición "+String.valueOf(newPos+1), "Se ha movido la canción "+at.getInfo().title+" de la posición "+String.valueOf(oldPos+1)+" a "+String.valueOf(newPos+1));
		
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		long oldPos = event.getOption("oldposition").getAsLong()-1;
		OptionMapping newPosOption = event.getOption("newposition");
		TrackScheduler ts = PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler;
		long newPos = newPosOption==null?ts.queue.size()-1:newPosOption.getAsLong()-1;
		AudioTrack at = ts.getFromQueue((int) oldPos);
		ts.removeFromQueue((int) oldPos).insertIntoQueue((int) newPos, at);
		event.replyEmbeds(Client.getInfoMessage(at.getInfo().title+" movido a la posición "+String.valueOf(newPos+1), "Se ha movido la canción "+at.getInfo().title+" de la posición "+String.valueOf(oldPos+1)+" a "+String.valueOf(newPos+1))).queue();
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
