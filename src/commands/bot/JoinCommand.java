package commands.bot;

import botinternals.Client;
import interfaces.NoParamCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class JoinCommand implements NoParamCommand{
	@Override
	public void execute(Guild g, MessageChannel mc, Member m) {
		if (g.getSelfMember().getVoiceState().inAudioChannel()) {
			Client.sendErrorMessage(mc, "Ya estoy en otro canal de voz");
			return;
		}else if(!m.getVoiceState().inAudioChannel()) {
			Client.sendErrorMessage(mc, "Necesito que te conectes a un canal de voz");
			return;
		}
		g.getAudioManager().openAudioConnection(m.getVoiceState().getChannel());
		Client.sendInfoMessage(mc, "Conectando...", "Estableciendo conexión con <#"+m.getVoiceState().getChannel().getId()+">");
	}

	@Override
	public String getName() {
		return "Join";
	}

	@Override
	public String getHelp() {
		return "Hace que el bot se una a la llamada";
	}
}
