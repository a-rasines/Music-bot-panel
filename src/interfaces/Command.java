package interfaces;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public interface Command {
	public void execute(Message msg);
	public void execute(SlashCommandEvent event);
	public String getName();
	public String getHelp();
	public OptionData[] params();

}
