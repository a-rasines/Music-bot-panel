package commands.timeline;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import botinternals.Client;
import interfaces.NonPartyNoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class RepeatCommand implements NonPartyNoParamCommand{

	@Override
	public String getName() {
		return "Repeat (tr / toglerepeat / loop)";
	}

	@Override
	public String getHelp() {
		return "Repite la canción que esta sonando";
	}

	@Override
	public void execute0(SlashCommandInteractionEvent event) {
		if (!checks(event.getGuild(),event.getMember(),event.getMessageChannel()))return;
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        final AudioTrack track = musicManager.audioPlayer.getPlayingTrack();
        final boolean newRepeating = !musicManager.scheduler.repeating;
        musicManager.scheduler.repeating = newRepeating;
        Client.sendInfoMessage(event, "Repeat", "El reproductor "+ (newRepeating?"ahora reproducirá la actual canción en bucle, "+track.getInfo().title:"dejará de reproducir "+track.getInfo().title+" en bucle"));
		
		
	}
}
