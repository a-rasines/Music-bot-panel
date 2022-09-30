package commands.queue;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import botinternals.Client;
import botinternals.MenuManager;
import interfaces.NonPartyCommand;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SearchCommand implements NonPartyCommand{
	@Override
	public void execute0(SlashCommandInteractionEvent event) {
		if (event.getOption("term")==null || event.getOption("term").getAsString().equals("")) {
        	event.replyEmbeds(Client.getErrorMessage("Hace falta un link o termino de busqueda para hacer funcionar el bot")).queue();;
            return;
        }		        final GuildVoiceState selfVoiceState = event.getGuild().getSelfMember().getVoiceState();
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
        if (!selfVoiceState.inAudioChannel() && memberVoiceState.inAudioChannel()) 
        	event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        if(!checks(event)) {
        	event.reply("Algo ha fallado").queue();
        	return;}
        
		PlayerManager manager = PlayerManager.getInstance();
		List<AudioTrack> playlist = manager.getPlaylistByTerm(event.getOption("term").getAsString(), event.getGuild());
		MenuManager menu = new MenuManager(event.getId(), new MenuManager.MenuAction() {
			@Override
			public void execute(String value) {
				manager.loadAndPlay(event, playlist.get(Integer.parseInt(value)).getInfo().uri, true);
				
			}
		});
		for (AudioTrack at : playlist) {
			menu.addOption(at.getInfo().title+" - "+at.getInfo().author, String.valueOf(playlist.indexOf(at)));
		}
		String desc = "";
		for (Integer i = 0; i<playlist.size(); i++) {
			AudioTrackInfo info = playlist.get(i).getInfo();
			desc += i.toString()+". "+info.title+" - "+info.author+"\n";
		}
		event.reply("Seleccione en el menu cual se reproducirá").queue();
		event.getChannel().sendMessageEmbeds(Client.getInfoMessage("Resultados", desc)).setActionRow(menu.build()).queue();
		
		
	}

	@Override
	public String getName() {
		return "Search";
	}

	@Override
	public String getHelp() {
		return "Busca una canción en youtube y te da a elegir entre los 10 primeros resultados";
	}

	@Override
	public OptionData[] params() {
		return new OptionData[]{new OptionData(OptionType.STRING, "term", "Término a buscar", true)};
	}			
}
