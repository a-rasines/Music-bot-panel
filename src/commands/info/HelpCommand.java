package commands.info;

import botinternals.Client;
import containers.Commands;
import interfaces.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class HelpCommand implements Command{

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		if (event.getOption("command")==null || !Commands.commandMap.containsKey(event.getOption("command").getAsString().toLowerCase()))
			event.replyEmbeds(Client.getInfoMessage("Comandos disponibles", String.join("\n", Commands.commandMap.keySet()))).queue();
		else if(Commands.commandMap.containsKey(event.getOption("command").getAsString().toLowerCase())){
			Command c = Commands.commandMap.get(event.getOption("command").getAsString().toLowerCase());
			event.replyEmbeds(Client.getInfoMessage(c.getName(), c.getHelp())).queue();
		}
	}

	@Override
	public String getName() {
		return "Help (h)";
	}

	@Override
	public String getHelp() {
		return "¿En serio necesitas ayuda sobre como usar el comando de ayuda?";
	}

	@Override
	public OptionData[] params() {
		return new OptionData[]{new OptionData(OptionType.STRING, "command", "Comando con el que ayudar", false)};
	}		
}
