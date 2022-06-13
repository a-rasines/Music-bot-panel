package interfaces;

import botinternals.Client;
import commands.queue.PartyCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface NonPartyCommand extends Command{
	@Override
	default void execute(MessageReceivedEvent msg) {
		if(PartyCommand.stalkerMap.containsKey(msg.getGuild().getIdLong())) {
			Client.sendErrorMessage(msg.getChannel(), "El modo party esta activado. Usa __stopparty__ para poder usar este comando.");
			return;
		}
		execute0(msg);
		
	}
	public void execute0(MessageReceivedEvent msg);
	@Override
	default void execute(SlashCommandInteractionEvent event) {
		if(PartyCommand.stalkerMap.containsKey(event.getGuild().getIdLong())) {
			event.replyEmbeds(Client.getErrorMessage("El modo party esta activado. Usa __stopparty__ para poder usar este comando."));
			return;
		}
	}
	public void execute0(SlashCommandInteractionEvent event);

}
