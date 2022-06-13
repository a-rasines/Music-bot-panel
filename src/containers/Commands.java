package containers;

import java.util.HashMap;
import commands.bot.JoinCommand;
import commands.bot.LeaveCommand;
import commands.bot.RefreshCommand;
import commands.info.HelpCommand;
import commands.info.InfoCommand;
import commands.info.LyricsCommand;
import commands.info.NowPlayingCommand;
import commands.info.PingCommand;
import commands.info.QueueCommand;
import commands.queue.ForceSkipCommand;
import commands.queue.MoveCommand;
import commands.queue.PartyCommand;
import commands.queue.PlayCommand;
import commands.queue.PlayFirstCommand;
import commands.queue.RemoveCommand;
import commands.queue.SearchCommand;
import commands.queue.ShuffleCommand;
import commands.queue.SkipPlayCommand;
import commands.timeline.PlayPauseCommands;
import commands.timeline.RepeatCommand;
import commands.timeline.RestartCommand;
import commands.timeline.SeekCommand;
import commands.timeline.StopCommand;
import interfaces.Command;

public class Commands {
	public static HashMap<String, Command> commandMap = new HashMap<>();
	public static void loadCommands() {
		
		commandMap.put("join", new JoinCommand());
		commandMap.put("leave", new LeaveCommand());
		commandMap.put("play", new PlayCommand());
		commandMap.put("nowplaying", new NowPlayingCommand());
		commandMap.put("queue", new QueueCommand());
		commandMap.put("remove", new RemoveCommand());
		commandMap.put("repeat", new RepeatCommand());
		commandMap.put("forceskip", new ForceSkipCommand());
		commandMap.put("stop", new StopCommand());
		commandMap.put("pause", new PlayPauseCommands(true));
		commandMap.put("resume", new PlayPauseCommands(false));
		commandMap.put("playfirst", new PlayFirstCommand());
		commandMap.put("skipplay", new SkipPlayCommand());
		commandMap.put("help", new HelpCommand());
		commandMap.put("shuffle", new ShuffleCommand());
		commandMap.put("search", new SearchCommand());
		commandMap.put("seek", new SeekCommand());
		commandMap.put("restart", new RestartCommand());
		commandMap.put("ping", new PingCommand());
		commandMap.put("lyrics", new LyricsCommand());
		commandMap.put("info", new InfoCommand());
		commandMap.put("refreshcommands", new RefreshCommand());
		commandMap.put("move", new MoveCommand());
		commandMap.put("party", new PartyCommand());
		commandMap.put("stopparty", new PartyCommand());
		
	}	
}
