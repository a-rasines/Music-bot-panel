package commands.info;

import botdata.BotData;
import botinternals.Client;
import containers.Commands;
import interfaces.NoParamCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class InfoCommand implements NoParamCommand{

	@Override
	public String getName() {
		return "Info";
	}

	@Override
	public String getHelp() {
		return "Muestra la información global del bot";
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		Client.sendInfoMessage(event, Client.jda.getSelfUser().getName(), 
				 "Servers incorporados: "+String.valueOf(Client.jda.getGuilds().size())+"\n"
				+"Comandos activos: "+String.valueOf(Commands.commandMap.size())+"\n"
				+"Versión activa: "+BotData.version+"\n");
		
	}
	
}
