package commands.queue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import botinternals.Client;
import interfaces.Command;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class RemoveCommand implements Command{
	
	@Override
	public void execute(SlashCommandInteractionEvent event) {
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
		int arg = (int) event.getOption("position").getAsLong();
		if (!checks(event))return;
		event.replyEmbeds(Client.getInfoMessage("Eliminando canción", "Eliminando "+((AudioTrack)musicManager.scheduler.queue.toArray()[arg-1]).getInfo().title)).queue();
		musicManager.scheduler.removeTrack(arg-1);
	}

	@Override
	public String getName() {
		return "Remove (r)";
	}

	@Override
	public String getHelp() {
		return "Elimina la canción de la posición especificada de la lista";
	}

	@Override
	public OptionData[] params() {
		return new OptionData[]{new OptionData(OptionType.INTEGER, "position", "Posición a eliminar", true)};
	}

}
