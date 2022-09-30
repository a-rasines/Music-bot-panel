package commands.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import botinternals.Client;
import interfaces.NoParamCommand;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ShuffleCommand implements NoParamCommand{
    

	@Override
	public String getName() {
		return "Shuffle";
	}

	@Override
	public String getHelp() {
		return "Redistribuye de forma aleatoria la cola";
	}
	
	@Override
	public void execute(SlashCommandInteractionEvent event) {
		BlockingQueue<AudioTrack> prevQueue = new LinkedBlockingQueue<>(PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.queue);
		BlockingQueue<AudioTrack> postQueue = new LinkedBlockingQueue<>();
		Random rand = new Random();
		Integer num;
		while (prevQueue.size() != 0) {
			num = rand.nextInt(prevQueue.size());
			postQueue.offer((AudioTrack) prevQueue.toArray()[num]);
			prevQueue.remove((AudioTrack) prevQueue.toArray()[num]);
		}
		PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.queue.clear();
		PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.queue.addAll(postQueue);
		
        final int trackCount = Math.min(postQueue.size(), 20);
        final List<AudioTrack> trackList = new ArrayList<>(postQueue);
        String songs = "";
        for (int i = 0; i <  trackCount; i++) {
            final AudioTrack track = trackList.get(i);
            final AudioTrackInfo info = track.getInfo();
            songs+=String.valueOf(i+1)+". "+info.title+" - "+info.author+" ["+formatTime(track.getDuration())+"]\n";
        }
        if (trackList.size() > trackCount) {
            songs+="Y mas...";
        }
        Client.sendInfoMessage(event, "Nueva cola", songs);
	}
	
}
