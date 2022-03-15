package commands.info;

import botinternals.Client;
import interfaces.NoParamCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class PingCommand implements NoParamCommand{

	@Override
	public void execute(Guild g, MessageChannel mc, Member m) {
		Client.sendInfoMessage(mc,"Ping", "El ping del bot es: "+String.valueOf(Client.jda.getGatewayPing())+"ms");
		
	}

	@Override
	public String getName() {
		return "Ping";
	}

	@Override
	public String getHelp() {
		return "Manda un mensaje con el retardo entre el envio y la recepción del bot";
	}
	
}
