package interfaces;

import botinternals.Client;
import commands.queue.PartyCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public interface NonPartyNoParamCommand extends NoParamCommand{
	@Override
	default void execute(Guild g, MessageChannel mc, Member m, boolean slash) {
		if(PartyCommand.stalkerMap.containsKey(g.getIdLong())) return;
		
	}
	public void execute0(Guild g, MessageChannel mc, Member m, boolean slash);
	@Override
	default Reply reply(Guild g, MessageChannel mc, Member m) {
		if(PartyCommand.stalkerMap.containsKey(g.getIdLong())) 
			return new Reply(Client.getErrorMessage("El modo party esta activado. Usa __stopparty__ para poder usar este comando."));
		else
			return reply0(g, mc, m);
	}
	public Reply reply0(Guild g, MessageChannel mc, Member m);
}
