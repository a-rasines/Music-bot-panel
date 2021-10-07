package interfaces;

import botinternals.SlashOption;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public interface Command {
	public void execute(Message msg);
	public void execute(SlashCommandEvent event);
	public String getName();
	public String getHelp();
	public SlashOption[] params();

}
