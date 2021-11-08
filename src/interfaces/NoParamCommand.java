package interfaces;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

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
	public default OptionData[] params() {
		return new OptionData[0];
	};

}
