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

public class QueueCommand implements NoParamCommand{
	
	@Override
	public void execute(Guild g, MessageChannel mc, Member m) {
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        if (queue.isEmpty()) {
            Client.sendInfoMessage(mc, "Canciones en cola", "Actualmente no hay canciones en cola");
            return;
        }

        final int trackCount = Math.min(queue.size(), 20);
        final List<AudioTrack> trackList = new ArrayList<>(queue);
        EmbedBuilder eb = new EmbedBuilder().setTitle("Canciones en cola").setColor(new Color(101020));
        String songs = "";
        for (int i = 0; i <  trackCount; i++) {
            final AudioTrack track = trackList.get(i);
            final AudioTrackInfo info = track.getInfo();
            songs+=String.valueOf(i+1)+". "+info.title+" - "+info.author+" ["+formatTime(track.getDuration())+"]\n";
        }

        if (trackList.size() > trackCount) {
            songs+="Y mas...";
        }

        eb.setDescription(songs);
        mc.sendMessageEmbeds(eb.build()).queue();
		
	}

	@Override
	public String getName() {
		return "Queue (q / list)";
	}

	@Override
	public String getHelp() {
		return "Muestra la lista de las primeras 20 canciones en la cola";
	}

}
