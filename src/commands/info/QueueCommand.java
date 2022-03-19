package commands.info;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import botinternals.Client;
import interfaces.NoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class QueueCommand implements NoParamCommand{
	MessageEmbed reply;
	@Override
	public void execute(Guild g, MessageChannel mc, Member m, boolean slash) {
        BlockingQueue<AudioTrack> queue = PlayerManager.getInstance().getMusicManager(g).scheduler.queue;

        if (queue.isEmpty()) {
            reply = Client.getInfoMessage("Canciones en cola", "Actualmente no hay canciones en cola");
            return;
        }else {
	        int trackCount = Math.min(queue.size(), 20);
	        List<AudioTrack> trackList = new ArrayList<>(queue);
	        String songs = "";
	        for (int i = 0; i <  trackCount; i++) {
	            final AudioTrack track = trackList.get(i);
	            final AudioTrackInfo info = track.getInfo();
	            songs+=String.valueOf(i+1)+". "+info.title+" - "+info.author+" ["+formatTime(track.getDuration())+"]\n";
	        }
	
	        if (trackList.size() > trackCount) {
	            songs+="Y mas...";
	        }
	
	        reply = Client.getInfoMessage("Canciones en cola", songs);
        }
        if(!slash)mc.sendMessageEmbeds(reply);
		
	}

	@Override
	public String getName() {
		return "Queue (q / list)";
	}

	@Override
	public String getHelp() {
		return "Muestra la lista de las primeras 20 canciones en la cola";
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
