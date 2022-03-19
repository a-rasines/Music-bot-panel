package interfaces;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public interface NoParamCommand extends Command {
	public default void execute(MessageReceivedEvent msg) {
		execute(msg.getGuild(), msg.getChannel(), msg.getMember(), false);
	}
	public default void execute(SlashCommandInteractionEvent event) {
		Reply r = reply(event.getGuild(),event.getChannel(),event.getMember());
		if(!replyFirst())execute(event.getGuild(),event.getChannel(),event.getMember(), true);
		try {
		if(r==null)
			event.reply("Comando "+event.getName()+" recibido").queue();
		else if(r.type == Reply.Type.Embed)
			event.replyEmbeds((MessageEmbed)r.value);
		else
			event.reply((String)r.value);
		}catch (Exception e) {}
		if(replyFirst())execute(event.getGuild(),event.getChannel(),event.getMember(), true);
	};
	public void execute(Guild g, MessageChannel mc, Member m, boolean slash);
	public Reply reply(Guild g, MessageChannel mc, Member m);
	public boolean replyFirst();
	public default OptionData[] params() {
		return new OptionData[0];
	};
	public static class Reply{
		public enum Type{Embed, Text};
		public final Type type;
		public final Object value;
		public Reply(MessageEmbed e) {
			type = Type.Embed;
			value = e;
		}
		public Reply(String text) {
			type = Type.Text;
			value = text;
		}
	}

}
