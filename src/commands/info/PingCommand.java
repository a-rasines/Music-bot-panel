package commands.info;

import botinternals.Client;
import interfaces.NoParamCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class PingCommand implements NoParamCommand{

	@Override
	public void execute(Guild g, MessageChannel mc, Member m, boolean slash) {
		if(!slash)
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

	@Override
	public Reply reply(Guild g, MessageChannel mc, Member m) {
		return new Reply(Client.getInfoMessage("Ping", "El ping del bot es: "+String.valueOf(Client.jda.getGatewayPing())+"ms"));
	}

	@Override
	public boolean replyFirst() {
		return true;
	}	
}
