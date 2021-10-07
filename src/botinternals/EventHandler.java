package botinternals;

import java.util.HashMap;
import java.util.List;

import containers.Aliases;
import containers.Commands;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class EventHandler implements EventListener {
	public final static HashMap<String, MenuManager> selectionMenu = new HashMap<>();
	@Override
	public void onEvent(GenericEvent arg0) {
		if (arg0 instanceof GuildMessageReceivedEvent) {
			GuildMessageReceivedEvent mre = (GuildMessageReceivedEvent) arg0;
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
							Commands.commandMap.get(command).execute(mre.getMessage());
						}
					};
					startThread.start();
				}
			}
		}else if (arg0 instanceof SelectionMenuEvent) {
			if (selectionMenu.containsKey(((SelectionMenuEvent)arg0).getComponentId())) {
				final SelectionMenuEvent menuEvent = (SelectionMenuEvent)arg0;
				menuEvent.deferEdit().queue();
				MenuManager mm = selectionMenu.get(menuEvent.getComponentId());
				Thread menuThread = new Thread() {
					@Override
					public void run() {
						mm.execute(mm.returnAndDestroy(menuEvent.getSelectedOptions().get(0)));
					}
				};
				menuThread.start();
			}
		}else if (arg0 instanceof SlashCommandEvent) {
			SlashCommandEvent scEvent = (SlashCommandEvent)arg0;
			if (!Commands.commandMap.containsKey(scEvent.getCommandPath()))return;
			Thread th = new Thread(){
				@Override
				public void run() {
				Commands.commandMap.get(scEvent.getCommandPath()).execute(scEvent);
				}
			};
			th.start();
		}
		
	}
	public boolean contains(List<User> l, User u) {
		for (User i: l)
			if (i.getIdLong() == u.getIdLong())
				return true;
		return false;
	}
	
	 

}
