package commands.bot;

import botinternals.Client;
import interfaces.NoParamCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class JoinCommand implements NoParamCommand{
	@Override
	public String getName() {
		return "Join";
	}

	@Override
	public String getHelp() {
		return "Hace que el bot se una a la llamada";
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
			Client.sendErrorMessage(event, "Ya estoy en otro canal de voz");
			return;
		}else if(!event.getMember().getVoiceState().inAudioChannel()) {
			Client.sendErrorMessage(event, "Necesito que te conectes a un canal de voz");
			return;
		}
		event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
		Client.sendInfoMessage(event, "Conectado", "Conexión establecida con <#"+event.getMember().getVoiceState().getChannel().getId()+">");
		
	}
}
