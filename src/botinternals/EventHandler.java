package botinternals;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

import commands.queue.PartyCommand;
import containers.Commands;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import lavaplayer.TrackScheduler;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventHandler extends ListenerAdapter {
	public final static HashMap<String, MenuManager> selectionMenu = new HashMap<>();
	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent mre) {
		if(contains(mre.getMessage().getMentions().getUsers(),Client.jda.getSelfUser()))
			Client.sendInfoMessage(mre.getChannel(), "Hola!", "Para mejorar la privacidad del bot, ya no acepta comandos de texto, por favor, escribe / para ver los comandos", "Si no te aparecen, vuelve a invitar al bot (No hace falta quitarlo)");
	}
	@Override
	public void onSelectMenuInteraction(SelectMenuInteractionEvent sme) {
		if (selectionMenu.containsKey(sme.getComponentId())) {
			sme.deferEdit().queue();
			MenuManager mm = selectionMenu.get(sme.getComponentId());
			Thread menuThread = new Thread() {
				@Override
				public void run() {
					mm.execute(mm.returnAndDestroy(sme.getSelectedOptions().get(0)));
				}
			};
			menuThread.start();
		}
	}
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent sce) {
		if (!Commands.commandMap.containsKey(sce.getCommandPath()))return;
		Thread th = new Thread(){
			@Override
			public void run() {
			Commands.commandMap.get(sce.getCommandPath()).execute(sce);
			}
		};
		th.start();
	}
	@Override
	public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
		if(event.getMember().getIdLong() == Client.jda.getSelfUser().getIdLong()) {
			TrackScheduler ts = PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler;
			ts.queue.clear();
			ts.nextTrack();
			PartyCommand.stalkerMap.remove(event.getGuild().getIdLong());
		}else if(event.getGuild().getAudioManager().getConnectedChannel() != null && event.getChannelLeft().getIdLong() == event.getGuild().getAudioManager().getConnectedChannel().getIdLong() && event.getChannelLeft().getMembers().size() == 1) {
			event.getGuild().getAudioManager().closeAudioConnection();
		}
	}
	@Override
	public void onUserActivityStart(UserActivityStartEvent event) {
		if(event.getNewActivity().getName().equals("Spotify") && PartyCommand.stalkerMap.containsValue(event.getMember().getIdLong())) {
			RichPresence pres = event.getNewActivity().asRichPresence();
			PartyCommand.stalkerMap.forEach((k, v)->{
				if(v == event.getMember().getIdLong()) {
					PlayerManager.getInstance().loadAndPlay(PlayerManager.getInstance().getMusicManager(Client.jda.getGuildById(k)),"ytsearch:" + pres.getDetails() + " - " + pres.getState()+ " lyrics", false);
					GuildMusicManager gmm = PlayerManager.getInstance().getMusicManager(Client.jda.getGuildById(k));
					gmm.audioPlayer.stopTrack();
					long milis0 = System.currentTimeMillis();
					while(gmm.scheduler.queue.size() == 0 && gmm.audioPlayer.getPlayingTrack() == null) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					gmm.scheduler.queue.clear();
					gmm.audioPlayer.getPlayingTrack().setPosition(pres.getTimestamps().getElapsedTime(ChronoUnit.MILLIS) + (System.currentTimeMillis() - milis0));
				}
			});
		}
	}
	public boolean contains(List<User> l, User u) {
		for (User i: l)
			if (i.getIdLong() == u.getIdLong())
				return true;
		return false;
	}
	
	 

}
