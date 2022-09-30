package commands.queue;

import botinternals.Client;
import interfaces.NoParamCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class StopPartyCommand implements NoParamCommand{

	@Override
	public String getName() {
		return "StopParty";
	}

	@Override
	public String getHelp() {
		return "Este comando hace que se deje de controlar el Spotify del usuario y desbloquea las funciones del bot";
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		PartyCommand.stalkerMap.remove(event.getGuild().getIdLong());
		Client.sendInfoMessage(event, "Party detenida", "La party ya no está activa");
	}

}
