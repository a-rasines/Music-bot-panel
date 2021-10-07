package interfaces;

import botinternals.SlashOption;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public interface NoParamCommand extends Command {
	public default void execute(Message msg) {
		execute(msg.getGuild(), msg.getChannel(), msg.getMember());
	}
	public default void execute(SlashCommandEvent event) {
		try {
		event.reply("Comando "+event.getName()+" recibido").queue();
		}catch (Exception e) {}
		execute(event.getGuild(),event.getChannel(),event.getMember());
	};
	public void execute(Guild g, MessageChannel mc, Member m);
	public default SlashOption[] params() {
		return new SlashOption[0];
	};

}
