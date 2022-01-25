package botinternals;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

import containers.Aliases;
import containers.Commands;
import lavaplayer.PlayerManager;
import lavaplayer.TrackScheduler;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventHandler extends ListenerAdapter {
	public final static HashMap<String, MenuManager> selectionMenu = new HashMap<>();
	@Override
	public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent mre) {
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
						Commands.commandMap.get(command).execute(mre.getMessage());
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
	public void onSelectionMenu(SelectionMenuEvent sme) {
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
	public void onSlashCommand(SlashCommandEvent sce) {
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
		}else if(event.getGuild().getAudioManager().getConnectedChannel() != null && event.getChannelLeft().getIdLong() == event.getGuild().getAudioManager().getConnectedChannel().getIdLong() && event.getChannelLeft().getMembers().size() == 1) {
			event.getGuild().getAudioManager().closeAudioConnection();
		}
	}
	public boolean contains(List<User> l, User u) {
		for (User i: l)
			if (i.getIdLong() == u.getIdLong())
				return true;
		return false;
	}
	
	 

}
