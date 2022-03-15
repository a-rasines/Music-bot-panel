package commands.bot;

import java.util.ArrayList;

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
	public void execute(Guild g, MessageChannel mc, Member m) {
		ArrayList<CommandData> cdList = new ArrayList<>();
		for (String k : Commands.commandMap.keySet()) {
			CommandDataImpl temp = new CommandDataImpl(k, Commands.commandMap.get(k).getHelp());
			temp.addOptions(Commands.commandMap.get(k).params());
			cdList.add(temp);
		}
		g.updateCommands().addCommands(cdList).queue();
		
		
	}
	
}