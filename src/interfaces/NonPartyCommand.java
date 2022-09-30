package interfaces;

import botinternals.Client;
import commands.queue.PartyCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface NonPartyCommand extends Command{
	@Override
	default void execute(SlashCommandInteractionEvent event) {
		if(PartyCommand.stalkerMap.containsKey(event.getGuild().getIdLong())) {
			event.replyEmbeds(Client.getErrorMessage("El modo party esta activado. Usa __stopparty__ para poder usar este comando."));
			return;
		}
	}
	public void execute0(SlashCommandInteractionEvent event);

}
