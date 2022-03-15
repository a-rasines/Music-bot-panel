package commands.queue;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import interfaces.NoParamCommand;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ShuffleCommand implements NoParamCommand{
	@Override
	public void execute(Guild g, MessageChannel mc, Member m) {
		BlockingQueue<AudioTrack> prevQueue = new LinkedBlockingQueue<>(PlayerManager.getInstance().getMusicManager(g).scheduler.queue);
		BlockingQueue<AudioTrack> postQueue = new LinkedBlockingQueue<>();
		Random rand = new Random();
		Integer num;
		while (prevQueue.size() != 0) {
			num = rand.nextInt(prevQueue.size());
			postQueue.offer((AudioTrack) prevQueue.toArray()[num]);
			prevQueue.remove((AudioTrack) prevQueue.toArray()[num]);
		}
		PlayerManager.getInstance().getMusicManager(g).scheduler.queue.clear();
		PlayerManager.getInstance().getMusicManager(g).scheduler.queue.addAll(postQueue);
		
        final int trackCount = Math.min(postQueue.size(), 20);
        final List<AudioTrack> trackList = new ArrayList<>(postQueue);
        EmbedBuilder eb = new EmbedBuilder().setTitle("Nueva cola").setColor(new Color(101020));
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
		return "Shuffle";
	}

	@Override
	public String getHelp() {
		return "Redistribuye de forma aleatoria la cola";
	}
	
}
