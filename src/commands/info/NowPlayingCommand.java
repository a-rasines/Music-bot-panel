package commands.info;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import botinternals.Client;
import interfaces.NoParamCommand;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class NowPlayingCommand implements NoParamCommand{
	MessageEmbed reply;
	@Override
	public void execute(Guild g, MessageChannel mc, Member m, boolean slash) {
		if (!checks(g,m,mc))return;
        AudioTrack track = PlayerManager.getInstance().getMusicManager(g).audioPlayer.getPlayingTrack();

        if (track == null) {
        	reply= Client.getInfoMessage("Now playing", "No hay nada reproduciondose ahora mismo");
        }else {
        	AudioTrackInfo info = track.getInfo();
        	reply = Client.getInfoMessage("Now playing", info.title +" - "+info.author+ " ("+info.uri+")",formatTime(info.length));
        }
        if(!slash)
        	mc.sendMessageEmbeds(reply);
	}
	

	@Override
	public String getName() {
		return "Now playing (np)";
	}

	@Override
	public String getHelp() {
		return "Indica lo que se está escuchando en el momento";
	}


	@Override
	public Reply reply(Guild g, MessageChannel mc, Member m) {
		return new Reply(reply);
	}


	@Override
	public boolean replyFirst() {
		return false;
	}

}
