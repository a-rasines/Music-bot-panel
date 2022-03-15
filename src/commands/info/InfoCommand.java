package commands.info;

import botdata.BotData;
import botinternals.Client;
import containers.Commands;
import interfaces.NoParamCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

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
	public void execute(Guild g, MessageChannel mc, Member m) {
		Client.sendInfoMessage(mc, Client.jda.getSelfUser().getName(), 
				 "Servers incorporados: "+String.valueOf(Client.jda.getGuilds().size())+"\n"
				+"Comandos activos: "+String.valueOf(Commands.commandMap.size())+"\n"
				+"Versión activa: "+BotData.version+"\n");
		
	}
	
}
