package commands.queue;

import botinternals.Client;
import interfaces.Command;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class PlayCommand implements Command{
	
	@Override
	public void execute(SlashCommandInteractionEvent event) {
		if(PartyCommand.stalkerMap.containsKey(event.getGuild().getIdLong())) {
			event.replyEmbeds(Client.getErrorMessage("El modo party esta activado. Usa __stopparty__ para poder usar este comando."));
			return;
		}
       if (event.getOption("term") == null || event.getOption("term").getAsString().equals("")) {
        	event.replyEmbeds(Client.getErrorMessage("Hace falta un link o termino de busqueda para hacer funcionar el bot")).queue();
            return;
       }
       if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel())event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
       else if (!checks(event)) {
    	   event.reply("Comando mal ejecutado");
    	   return;
       }
       String link = event.getOption("term").getAsString();

       if (!isUrl(link)) {
            link = "ytsearch:" + link;
        }
       event.reply("Comando ejecutado");
       PlayerManager.getInstance().loadAndPlay(event, link, true);
		
	}

	@Override
	public String getName() {
		return "Play (p)";
	}

	@Override
	public String getHelp() {
		return "Reproduce la canción del link o la busqueda indicada";
	}

	@Override
	public OptionData[] params() {
		return new OptionData[]{new OptionData(OptionType.STRING, "term", "Término o link a reproducir", true)};
	}

}
