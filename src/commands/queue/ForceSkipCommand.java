package commands.queue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import botinternals.Client;
import interfaces.NonPartyNoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ForceSkipCommand implements NonPartyNoParamCommand{

	@Override
	public String getName() {
		return "ForceSkip (fs)";
	}

	@Override
	public String getHelp() {
		return "Salta la canción que está sonando";
	}

	@Override
	public void execute0(SlashCommandInteractionEvent event) {
		if (!checks(event.getGuild(),event.getMember(),event.getChannel()))return;

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        if (audioPlayer.getPlayingTrack() == null) {
            Client.sendErrorMessage(event, "No hay canciones reproduciendose");
            return;
        }
        if(!event.isAcknowledged())
        	Client.sendInfoMessage(event, "Saltando", "Se ha saltado "+ audioPlayer.getPlayingTrack().getInfo().title);
        musicManager.scheduler.nextTrack();
		
	}

}
