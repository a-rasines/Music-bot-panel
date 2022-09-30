package commands.info;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import botinternals.Client;
import interfaces.NoParamCommand;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class QueueCommand implements NoParamCommand{

	@Override
	public String getName() {
		return "Queue (q / list)";
	}

	@Override
	public String getHelp() {
		return "Muestra la lista de las primeras 20 canciones en la cola";
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		BlockingQueue<AudioTrack> queue = PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.queue;

        if (queue.isEmpty()) {
            Client.sendInfoMessage(event, "Canciones en cola", "Actualmente no hay canciones en cola");
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
	
	        Client.sendInfoMessage(event, "Canciones en cola", songs);
        }
		
	}

}
