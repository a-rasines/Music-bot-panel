package commands.timeline;

import botinternals.Client;
import interfaces.NonPartyCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SeekCommand implements NonPartyCommand{

	@Override
	public void execute0(SlashCommandInteractionEvent event) {
		if (!checks(event))return;
		if (event.getOption("position") != null && isInt(event.getOption("position").getAsString().split(":"))) {
			String[] time0 = event.getOption("position").getAsString().split(":");
	        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
	        String[] time = {"0","0","0"};
	        for (int i = 0;i<Integer.min(3, time0.length);i++) {
	        	time[i] = time0[(time0.length-1)-i];
	        }
			musicManager.scheduler.player.getPlayingTrack().setPosition(Integer.parseInt(time[2])*3600000+Integer.parseInt(time[1])*60000+Integer.parseInt(time[0])*1000);
			event.replyEmbeds(Client.getInfoMessage("Salto", "El reproductor ha saltado a "+formatTime(musicManager.scheduler.player.getPlayingTrack().getPosition()))).queue();
		} else
			event.replyEmbeds(Client.getErrorMessage("Introduce el punto al que saltar")).queue();
	}

	@Override
	public String getName() {
		return "Seek";
	}

	@Override
	public String getHelp() {
		return "Avanza o retrocede al punto que se indica";
	}

	@Override
	public OptionData[] params() {
		return new OptionData[]{new OptionData(OptionType.STRING, "position", "Posición de la cancion actual a reproducir", true)};
	}			
}