package botdata;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import botinternals.EventHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class ClientData {
	public static ArrayList<ClientData> extraBots = new ArrayList<>();
	public String prefix;
	private String token;
	private long id;
	public String name;
	public ClientData(String name, long id, String token, String prefix) {
		this.name = name;
		this.id = id;
		this.token = token;
		this.prefix = prefix;
	}
	public void invite() {
		try {
			Desktop.getDesktop().browse(new URI("https://discord.com/api/oauth2/authorize?client_id="+String.valueOf(id)+"&permissions=3230720&scope=bot%20applications.commands"));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	public JDA generate() {
		try {
			 return JDABuilder.create(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES)
			            .addEventListeners(new EventHandler()).enableCache(Arrays.asList(CacheFlag.VOICE_STATE)).disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS)
			            .build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
