package commands.timeline;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import botinternals.Client;
import interfaces.NonPartyNoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class RepeatCommand implements NonPartyNoParamCommand{
	@Override
	public void execute0(Guild g, MessageChannel mc, Member m, boolean slash) {
		if (!checks(g,m,(TextChannel)mc))return;
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
        final AudioTrack track = musicManager.audioPlayer.getPlayingTrack();
        final boolean newRepeating = !musicManager.scheduler.repeating;
        musicManager.scheduler.repeating = newRepeating;
        Client.sendInfoMessage(mc, "Repeat", "El reproductor "+ (newRepeating?"ahora reproducirá la actual canción en bucle, "+track.getInfo().title:"dejará de reproducir "+track.getInfo().title+" en bucle"));
		
	
	}

	@Override
	public String getName() {
		return "Repeat (tr / toglerepeat / loop)";
	}

	@Override
	public String getHelp() {
		return "Repite la canción que esta sonando";
	}

	@Override
	public Reply reply0(Guild g, MessageChannel mc, Member m) {
		return null;
	}

	@Override
	public boolean replyFirst() {
		return false;
	}
}
