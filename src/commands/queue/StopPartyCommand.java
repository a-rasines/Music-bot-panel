package commands.queue;

import botinternals.Client;
import interfaces.NoParamCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

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
	public void execute(Guild g, MessageChannel mc, Member m, boolean slash) {
		PartyCommand.stalkerMap.remove(g.getIdLong());
	}

	@Override
	public Reply reply(Guild g, MessageChannel mc, Member m) {
		return new Reply(Client.getInfoMessage("Exito", "El tracker ha sido eliminado satisfactoriamente"));
	}

	@Override
	public boolean replyFirst() {
		// TODO Auto-generated method stub
		return false;
	}

}
