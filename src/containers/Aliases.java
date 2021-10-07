package containers;

import java.util.HashMap;

public class Aliases {
	public static final HashMap<String, String> aliasMap = new HashMap<>();
	public static void loadAliases() {
		aliasMap.put("p", "play");
		
		aliasMap.put("fs", "forceskip");
		
		aliasMap.put("continue", "resume");
		
		aliasMap.put("np", "nowplaying");
		
		aliasMap.put("q", "queue");
		aliasMap.put("list", "queue");
		
		aliasMap.put("r", "remove");
		
		aliasMap.put("loop", "repeat");
		aliasMap.put("tr", "repeat");
		aliasMap.put("togglerepeat", "repeat");
		
		aliasMap.put("end", "stop");
		
		aliasMap.put("pf", "playfirst");
		aliasMap.put("override", "playfirst");
		aliasMap.put("fp", "playfirst");
		aliasMap.put("firstplay", "playfirst");
		
		aliasMap.put("sp", "skipplay");
		aliasMap.put("ps", "skipplay");
		aliasMap.put("playskip", "skipplay");
		
		aliasMap.put("h", "help");
		
		aliasMap.put("s", "search");
		
		aliasMap.put("disconnect", "leave");
		aliasMap.put("d", "disconnect");
		
		aliasMap.put("l", "lyrics");
		
	}

}
