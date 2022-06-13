package commands.queue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import botinternals.Client;
import interfaces.NonPartyNoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class ForceSkipCommand implements NonPartyNoParamCommand{
	@Override
	public void execute0(Guild g, MessageChannel mc, Member m, boolean slash) {
		if (!checks(g,m,(TextChannel) mc))return;

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        if (audioPlayer.getPlayingTrack() == null) {
            Client.sendErrorMessage(mc, "No hay canciones reproduciendose");
            return;
        }
        Client.sendInfoMessage(mc, "Saltando", "Se ha saltado "+ audioPlayer.getPlayingTrack().getInfo().title);
        musicManager.scheduler.nextTrack();
	}

	@Override
	public String getName() {
		return "ForceSkip (fs)";
	}

	@Override
	public String getHelp() {
		return "Salta la canción que está sonando";
	}

	@Override
	public Reply reply0(Guild g, MessageChannel mc, Member m) {
		return null;
	}

	@Override
	public boolean replyFirst() {
		return true;
	}

}
