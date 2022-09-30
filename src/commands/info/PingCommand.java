package commands.info;

import botinternals.Client;
import interfaces.NoParamCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PingCommand implements NoParamCommand{

	@Override
	public String getName() {
		return "Ping";
	}

	@Override
	public String getHelp() {
		return "Manda un mensaje con el retardo entre el envio y la recepción del bot";
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		Client.sendInfoMessage(event,"Ping", "El ping del bot es: "+String.valueOf(Client.jda.getGatewayPing())+"ms");
		
	}	
}
