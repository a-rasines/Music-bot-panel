package lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import botinternals.Client;
import net.dv8tion.jda.api.entities.Activity;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    public final AudioPlayer player;
    public final BlockingQueue<AudioTrack> queue;
    public boolean repeating = false;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }
    public TrackScheduler insertIntoQueue(int pos, AudioTrack track) {
    	ArrayList<AudioTrack> qTemp = new ArrayList<>(this.queue);
    	qTemp.add(pos, track);
    	this.queue.clear();
    	this.queue.addAll(qTemp);
    	return this;
    }
    public TrackScheduler removeFromQueue(int pos) {
    	ArrayList<AudioTrack> temp = new ArrayList<>(queue);
    	temp.remove(pos);
    	this.queue.clear();
    	this.queue.addAll(temp);
    	return this;
    }
    public AudioTrack getFromQueue(int pos) {
    	return new ArrayList<AudioTrack>(queue).get(pos);
    }
    public int queue(AudioTrack track, boolean end) {
        if (!this.player.startTrack(track, true)) {
        	if (end) {
        		this.queue.offer(track);
        		return this.queue.size();
        	}else {
        		BlockingQueue<AudioTrack> newQueue = new LinkedBlockingQueue<>();
        		newQueue.offer(track);
        		for (AudioTrack at : this.queue)newQueue.offer(at);
        		this.queue.clear();
        		this.queue.addAll(newQueue);
        		return 1;
        	}
        }else {
        	Client.jda.getPresence().setActivity(Activity.listening(track.getInfo().title));
        	return 0;
        }
    }
    public void removeTrack(int pos) {
    	this.queue.remove(this.queue.toArray()[pos-1]);
    }
    public void nextTrack() {
    	
    	AudioTrack next = this.queue.poll();
    	//Random rr = new Random();
        //this.player.startTrack(rr.nextInt(200000)==1?rrTrack:next, false);
    	this.player.startTrack(next, false);
        Client.jda.getPresence().setActivity(Activity.listening(next==null?"silencio":next.getInfo().title));
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            if (this.repeating) {
                this.player.startTrack(track.makeClone(), false);
                return;
            }

            nextTrack();
        }
    }
}
