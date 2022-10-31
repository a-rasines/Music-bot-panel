package commands.info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import botinternals.Client;
import botinternals.MenuManager;
import genius.SongSearch.Hit;
import interfaces.Command;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LyricsCommand implements Command{
	AudioTrack track;
	String lyrics;

	@Override
	public String getName() {
		return "Lyrics (l)";
	}

	@Override
	public String getHelp() {
		return "Manda la letra de la canción sonando";
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		GuildMusicManager gmm = PlayerManager.getInstance().getMusicManager(event.getGuild());
		track = gmm.audioPlayer.getPlayingTrack(); 
		try {
			String searchterm = (event.getOption("song")==null?"":event.getOption("song").getAsString())+ " " + (event.getOption("author")==null?"":event.getOption("author").getAsString()); 
			searchterm = searchterm.strip();
			if(searchterm.equals(""))
				searchterm = track.getInfo().title + " " + track.getInfo().author;
			LinkedList<Hit> hits = Client.genius.search(searchterm).getHits();
			switch(hits.size()) {
				case 0:
					Client.sendErrorMessage(event, "Letra no encontrada. Usa los parametros del comando para especificarle al bot la busqueda a hacer o revisa tu ortografía.");
					return;
				case 1:
					lyrics = hits.get(0).fetchLyrics();
					Client.sendInfoMessage(event, "Letra de "+track.getInfo().title, lyrics);
					return;
				default:
					String message = "";
					MenuManager mg = new MenuManager(searchterm, new MenuManager.MenuAction() {
						@SuppressWarnings("unchecked")
						LinkedList<Hit> l = (LinkedList<Hit>) hits.clone();
						@Override
						public void execute(String value) {
							Hit h = l.get(Integer.parseInt(value));
							event.getHook().editOriginalEmbeds(Client.getInfoMessage("Letra de "+h.getTitle(), h.fetchLyrics())).setComponents(new ArrayList<>()).queue();						
						}
					});
					for (int i = 0; i < Math.min(25, hits.size()); i++) {
						Hit h = hits.get(i);
						message += h.getTitle() +" · " +h.getArtist().getName() + "\n";
						mg.addOption(h.getTitle() +" · " +h.getArtist().getName() + "\n", ""+i);
					}
					event.replyEmbeds(Client.getInfoMessage("Lyric results", message)).addActionRow(mg.build()).queue();
					
			}
			
		} catch (IOException e) {
			Client.sendErrorMessage(event, "Something went wrong: "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public OptionData[] params() {
		return new OptionData[] {new OptionData(OptionType.STRING, "song", "nombre de la cancion"), new OptionData(OptionType.STRING, "author", "nombre del autor")};
	}
	
}
