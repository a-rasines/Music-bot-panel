package commands.queue;

import containers.Commands;
import interfaces.Command;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SkipPlayCommand implements Command{

	@Override
	public void execute(MessageReceivedEvent e) {
		if (!checks(e.getMessage()))return;
		int qlenght = PlayerManager.getInstance().getMusicManager(e.getGuild()).scheduler.queue.size();
		Commands.commandMap.get("playfirst").execute(e);
		while (qlenght == PlayerManager.getInstance().getMusicManager(e.getGuild()).scheduler.queue.size());
		Commands.commandMap.get("forceskip").execute(e);
	}
	@Override
	public void execute(SlashCommandInteractionEvent event) {
		if (!checks(event))return;
		int qlenght = PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.queue.size();
		Commands.commandMap.get("playfirst").execute(event);
		while (qlenght == PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.queue.size());
		Commands.commandMap.get("forceskip").execute(event);
		
	}

	@Override
	public String getName() {
		return "SkipPlay (sp / ps / playskip)";
	}

	@Override
	public String getHelp() {
		return "Salta la canción actual y activa la indicada.\nConjunto de playfirst y forceskip";
	}

	@Override
	public OptionData[] params() {
		return new OptionData[]{new OptionData(OptionType.STRING, "term", "Término o link a reproducir", true)};
	}
	
}
