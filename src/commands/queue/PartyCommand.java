package commands.queue;

import java.util.HashMap;

import botinternals.Client;
import interfaces.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class PartyCommand implements Command{
	public static HashMap<Long, Long> stalkerMap = new HashMap<>();
	@Override
	public void execute(MessageReceivedEvent msg) {
		if(msg.getGuild().getAudioManager().getConnectedChannel() == null && msg.getMember().getVoiceState().inAudioChannel())
			msg.getGuild().getAudioManager().openAudioConnection(msg.getMember().getVoiceState().getChannel());
		else if(!checks(msg.getMessage()))return;
		if(!stalkerMap.containsKey(msg.getGuild().getIdLong())) {
			if(msg.getMessage().getMentionedMembers().size() != 1) {
				Client.sendErrorMessage(msg.getChannel(), "La cantidad de usuarios stalkeados debe ser estrictamente 1");
			}else {
				stalkerMap.put(msg.getGuild().getIdLong(), msg.getMessage().getMentionedMembers().get(0).getIdLong());
			}
		}else {
			Client.sendErrorMessage(msg.getChannel(), "El bot ya está stalkeando a un miembro");
		}
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		if(event.getGuild().getAudioManager().getConnectedChannel() == null && event.getMember().getVoiceState().inAudioChannel())
			event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
		if(!checks(event))return;
		if(!stalkerMap.containsKey(event.getGuild().getIdLong())) {
			stalkerMap.put(event.getGuild().getIdLong(), event.getOption("user").getAsMember().getIdLong());
			event.replyEmbeds(Client.getInfoMessage("Usuario trackeado", event.getOption("user").getAsMember().getAsMention() + " esta siendo trackeado.")).queue();
		}else {
			event.replyEmbeds(Client.getErrorMessage("El bot ya está stalkeando a un miembro")).queue();
		}
		
	}

	@Override
	public String getName() {
		return "Party";
	}

	@Override
	public String getHelp() {
		return "Este comando se encarga de reproducir a tiempo casi real la música que el usuario seleccionado esta escuchando";
	}

	@Override
	public OptionData[] params() {
		return new OptionData[]{new OptionData(OptionType.USER, "user", "Usuario a stalkear",true)};
	}

}
