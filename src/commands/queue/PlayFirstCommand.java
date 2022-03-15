package commands.queue;

import botinternals.Client;
import interfaces.Command;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class PlayFirstCommand implements Command{


	@Override
	public void execute(MessageReceivedEvent msg) {
		final TextChannel channel = msg.getTextChannel();

        if (msg.getMessage().getContentDisplay().split(" ").length <= 1) {
        	Client.sendErrorMessage(msg.getChannel(), "Hace falta un link o termino de busqueda para hacer funcionar el bot");
            return;
        }

        final GuildVoiceState selfVoiceState = msg.getGuild().getSelfMember().getVoiceState();
        final GuildVoiceState memberVoiceState = msg.getMember().getVoiceState();

        if (!selfVoiceState.inAudioChannel() && !memberVoiceState.inAudioChannel()) {
        	Client.sendErrorMessage(msg.getChannel(), "Tienes que estar en un canal de voz para que el bot funcione");
            return;
        }else if (!selfVoiceState.inAudioChannel() && memberVoiceState.inAudioChannel()) {
        	msg.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        }else if (!memberVoiceState.inAudioChannel() ||selfVoiceState.getChannel().getIdLong() != memberVoiceState.getChannel().getIdLong()) {
        	Client.sendErrorMessage(msg.getChannel(), "No estamos en el mismo canal, por favor cambiate para usarme");
        	return;
        }

        String link = String.join(" ", subArray(msg.getMessage().getContentDisplay().split(" "), 1, msg.getMessage().getContentDisplay().split(" ").length));

        if (!isUrl(link)) {
            link = "ytsearch:" + link;
        }

        PlayerManager.getInstance().loadAndPlay(channel, link, false);
		
	}
	@Override
	public void execute(SlashCommandInteractionEvent event) {
        if (event.getOption("term") == null || event.getOption("term").getAsString().equals("")) {
        	event.replyEmbeds(Client.getErrorMessage("Hace falta un link o termino de busqueda para hacer funcionar el bot")).queue();
            return;
        }

        final GuildVoiceState selfVoiceState = event.getGuild().getSelfMember().getVoiceState();
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

        if (!selfVoiceState.inAudioChannel() && !memberVoiceState.inAudioChannel()) {
        	event.replyEmbeds(Client.getErrorMessage("Tienes que estar en un canal de voz para que el bot funcione")).queue();
            return;
        }else if (!selfVoiceState.inAudioChannel() && memberVoiceState.inAudioChannel()) {
        	event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        }else if (!memberVoiceState.inAudioChannel() ||selfVoiceState.getChannel().getIdLong() != memberVoiceState.getChannel().getIdLong()) {
        	event.replyEmbeds(Client.getErrorMessage("No estamos en el mismo canal, por favor cambiate para usarme")).queue();
        	return;
        }

        String link = event.getOption("term").getAsString();

        if (!isUrl(link)) {
            link = "ytsearch:" + link;
        }

        PlayerManager.getInstance().loadAndPlay(event, link, false);
		
		
	}

	@Override
	public String getName() {
		return "PlayFirst (pf / override / fp / firstplay)";
	}

	@Override
	public String getHelp() {
		return "Pone la canción al principio de la lista para que se reproduzca a continuación";
	}

	@Override
	public OptionData[] params() {
		return new OptionData[]{new OptionData(OptionType.STRING,"term", "Término o link a reproducir", true)};
	}
	
}
