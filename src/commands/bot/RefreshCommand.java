package commands.bot;

import java.util.ArrayList;

import botinternals.Client;
import containers.Commands;
import interfaces.NoParamCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class RefreshCommand implements NoParamCommand{

	@Override
	public String getName() {
		return "RefreshCommands";
	}

	@Override
	public String getHelp() {
		return "Reinicia los comandos de barra lateral";
	}

	@Override
	public void execute(Guild g, MessageChannel mc, Member m, boolean slash) {
		ArrayList<CommandData> cdList = new ArrayList<>();
		for (String k : Commands.commandMap.keySet()) {
			CommandDataImpl temp = new CommandDataImpl(k, Commands.commandMap.get(k).getHelp());
			temp.addOptions(Commands.commandMap.get(k).params());
			cdList.add(temp);
		}
		g.updateCommands().addCommands(cdList).queue();
		
		
	}

	@Override
	public Reply reply(Guild g, MessageChannel mc, Member m) {
		return new Reply(Client.getInfoMessage("Reinicio de comandos", "Los comandos de barra lateral van a ser reiniciados ahora. Los comandos nuevos/cambiados pueden no aparecer al momento por la prevención anti-DDoS de Discord"));
	}

	@Override
	public boolean replyFirst() {
		// TODO Auto-generated method stub
		return false;
	}
	
}