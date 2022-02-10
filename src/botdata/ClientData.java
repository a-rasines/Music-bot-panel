package botdata;

import java.awt.Desktop;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.security.auth.login.LoginException;
import javax.swing.JOptionPane;

import botinternals.EventHandler;
import main.StartPanel;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class ClientData implements Serializable {
	private static final long serialVersionUID = 4507709880375676529L;
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
		} catch (LoginException e) {
			JOptionPane.showMessageDialog(null, "Ha habido un error iniciando sesión: "+e.getMessage());
			new StartPanel();
			return null;
		}catch(IllegalStateException e) {
			JOptionPane.showMessageDialog(null, "Error interno, espere y vuelva a intentarlo: "+e.getMessage());
			new StartPanel();
			return null;
		}
	}
	public boolean equalTo(String other) {
		return token.equals(other);
	}
	public String toString() {
		return name;
	}
	public boolean equals(Object o) {
		return ((o instanceof ClientData) && ((ClientData)o).equalTo(token));
	}
}
