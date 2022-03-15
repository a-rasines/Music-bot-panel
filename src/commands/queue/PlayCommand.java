package commands.queue;

import botinternals.Client;
import interfaces.Command;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class PlayCommand implements Command{
	@Override
	public void execute(MessageReceivedEvent event) {
		final Message msg = event.getMessage();
		final TextChannel channel = msg.getTextChannel();

        if (msg.getContentDisplay().split(" ").length == 1) {
        	if (msg.getAttachments().size() == 1 && (msg.getAttachments().get(0).getContentType().equals("audio/mpeg"))||msg.getAttachments().get(0).getContentType().equals("video/mp4"))
        		PlayerManager.getInstance().loadAndPlay(channel, msg.getAttachments().get(0).getUrl(), true);
        	Client.sendErrorMessage(msg.getChannel(), "Hace falta un link o termino de busqueda para hacer funcionar el bot");
            return;
        }
        if (!msg.getGuild().getSelfMember().getVoiceState().inAudioChannel())msg.getGuild().getAudioManager().openAudioConnection(msg.getMember().getVoiceState().getChannel());
	    else if (!checks(msg))return;
        String link = String.join(" ", subArray(msg.getContentDisplay().split(" "), 1, msg.getContentDisplay().split(" ").length));

        if (!isUrl(link)) {
            link = "ytsearch:" + link;
        }

        PlayerManager.getInstance().loadAndPlay(channel, link, true);
		
	}
	@Override
	public void execute(SlashCommandInteractionEvent event) {
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
