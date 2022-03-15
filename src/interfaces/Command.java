package interfaces;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import botinternals.Client;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public interface Command {
	public void execute(MessageReceivedEvent msg);
	public void execute(SlashCommandInteractionEvent event);
	public String getName();
	public String getHelp();
	public OptionData[] params();
	public default String[] subArray(String[] a, int pos0, int posf) {
		String[] fin = new String[posf - pos0];
		for (int i = pos0; i<posf;i++) {
			fin[i-pos0] = a[i];
		}
		return fin;
	}
	public default boolean isUrl(String link) {
		try {
			new URL(link);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
	public default boolean isInt(String link) {
		try {
			Integer.parseInt(link);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	public default boolean isInt(String[] link) {
		try {
			for (String s:link)
				Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	public default String formatTime(long timeInMillis) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
	public default boolean checks(SlashCommandInteractionEvent event) {
		return checks(event.getGuild(),event.getMember(),event.getChannel());
	}
	public default boolean checks(Message msg) {
		return checks(msg.getGuild(),msg.getMember(),msg.getChannel());
	}
	public default boolean checks(Guild g, Member m, MessageChannel mc) {
		final GuildVoiceState selfVoiceState = g.getSelfMember().getVoiceState();
        final GuildVoiceState memberVoiceState = m.getVoiceState();
        final MessageChannel channel = mc;
		 if (!selfVoiceState.inAudioChannel()) {
	            Client.sendErrorMessage(channel, "Necesito estar en un canal de voz para funcionar");
	            return false;
	        }
		 if (!memberVoiceState.inAudioChannel()) {
	            Client.sendErrorMessage(channel, "No te encuentras en el mismo canal que yo, conectate a <#"+selfVoiceState.getChannel().getId()+"> para usar el comando");
	            return false;
	        }

	        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
	        	Client.sendErrorMessage(channel, "No te encuentras en el mismo canal que yo, conectate a <#"+selfVoiceState.getChannel().getId()+"> para usar el comando");
	            return false;
	        }
		return true;
	}

}
