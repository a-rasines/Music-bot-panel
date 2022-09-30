package interfaces;

import commands.queue.PartyCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface NonPartyNoParamCommand extends NoParamCommand{
	@Override
	default void execute(SlashCommandInteractionEvent event) {
		if(PartyCommand.stalkerMap.containsKey(event.getGuild().getIdLong())) return;
		else execute0(event);
		
	}
	public void execute0(SlashCommandInteractionEvent event);
}
