package botinternals;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

import commands.queue.PartyCommand;
import containers.Aliases;
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
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventHandler extends ListenerAdapter {
	public final static HashMap<String, MenuManager> selectionMenu = new HashMap<>();
	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent mre) {
		if(!mre.isFromGuild())return;
		if(contains(mre.getMessage().getMentionedUsers(),Client.jda.getSelfUser())) {
			mre.getChannel().sendMessage("Estoy activo y mi prefijo es: "+Client.prefix).queue();
		}else if (mre.getMessage().getContentDisplay().startsWith(Client.prefix)){
			String command0 = mre.getMessage().getContentDisplay().split(" ")[0].toLowerCase().replaceFirst(Client.prefix, "");
			final String command;
			if (Aliases.aliasMap.containsKey(command0))command = Aliases.aliasMap.get(command0);
			else command = command0;
			if(Commands.commandMap.containsKey(command)) {
				Thread startThread = new Thread() {
					public void run() {
						try {
						Commands.commandMap.get(command).execute(mre);
						}catch(InsufficientPermissionException e) {
							Client.sendErrorMessage(mre.getChannel(), "Imposible ejecutar el comando sin el permiso "+e.getPermission().toString());
						}
					}
				};
				startThread.start();
			}
		}
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
			System.out.println(event.getMember().getEffectiveName() +" ha iniciado una cancion con nombre: " + pres.getDetails() + " y autor: " + pres.getState() + " time:");
		}
	}
	public boolean contains(List<User> l, User u) {
		for (User i: l)
			if (i.getIdLong() == u.getIdLong())
				return true;
		return false;
	}
	
	 

}
