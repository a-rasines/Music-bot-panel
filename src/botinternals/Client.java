package botinternals;

import java.awt.Color;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;

import botdata.BotData;
import botdata.ClientData;
import containers.Aliases;
import containers.Commands;
import core.GLA;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Client {
	public static JDA jda;
	public static GLA genius;
	public static SpotifyApi spotify;
	public static void generateJDA(ClientData data){
		 spotify = SpotifyApi.builder().setClientSecret(BotData.spotifySecret).setClientId(BotData.spotifyId).build();
        Thread authTread = new Thread() {
        	public void run() {
        		while (true){
        			try {
        				ClientCredentials ccr = spotify.clientCredentials().build().execute();
        				spotify.setAccessToken(ccr.getAccessToken());
        				Thread.sleep(35999970);
        			} catch (Exception e1) {
        				e1.printStackTrace();
        			}
        		}
        	}
        };
        authTread.start();
        jda = data.generate();
        if(jda == null)return;
		genius = new GLA(BotData.geniusId, BotData.geniusSecret);
		Commands.loadCommands();
		Aliases.loadAliases();
		jda.getPresence().setActivity(Activity.listening("la música que youtube intenta monetizar"));
	}
	public static Message sendInfoMessage(MessageChannel channel, String title, String reason) {
		return channel.sendMessageEmbeds(getInfoMessage(title, reason)).complete();
    	
    }
	public static void sendInfoMessage(MessageChannel channel, String title, String reason, String footer) {
		channel.sendMessageEmbeds(getInfoMessage(title, reason, footer)).queue();	
    }
	public static void sendErrorMessage(MessageChannel channel, String reason) {
		channel.sendMessageEmbeds(getErrorMessage(reason)).queue();
    	
    }
	
	
	public static Message sendInfoMessage(MessageReceivedEvent event, String title, String reason) {
		return sendInfoMessage(event.getChannel(), title, reason);
    	
    }
	public static void sendInfoMessage(MessageReceivedEvent event, String title, String reason, String footer) {
		sendInfoMessage(event.getChannel(), title, reason);
    }
	public static void sendErrorMessage(MessageReceivedEvent event, String reason) {
		sendErrorMessage(event.getChannel(), reason);
    }
	
	
	public static void sendInfoMessage(SlashCommandInteractionEvent event, String title, String reason) {
		event.replyEmbeds(getInfoMessage(title, reason)).complete();
    	
    }
	public static void sendInfoMessage(SlashCommandInteractionEvent event, String title, String reason, String footer) {
		event.replyEmbeds(getInfoMessage(title, reason, footer)).queue();	
    }
	
	public static void sendErrorMessage(SlashCommandInteractionEvent event, String reason, boolean ephemeral) {
		event.replyEmbeds(getErrorMessage(reason)).setEphemeral(ephemeral).queue();    	
    }
	
	
	public static void sendInfoMessage(SlashCommandInteractionEvent event, String title, String reason, boolean ephemeral) {
		event.replyEmbeds(getInfoMessage(title, reason)).setEphemeral(ephemeral).queue();
    	
    }
	public static void sendInfoMessage(SlashCommandInteractionEvent event, String title, String reason, String footer, boolean ephemeral) {
		event.replyEmbeds(getInfoMessage(title, reason, footer)).setEphemeral(ephemeral).queue();	
    }
	
	public static void sendErrorMessage(SlashCommandInteractionEvent event, String reason) {
		event.replyEmbeds(getErrorMessage(reason)).queue();
    }
	
	public static void sendInfoMessage(InteractionHook event, String title, String reason, boolean ephemeral) {
		event.sendMessageEmbeds(getInfoMessage(title, reason)).setEphemeral(ephemeral).queue();
    	
    }
	public static void sendInfoMessage(InteractionHook event, String title, String reason, String footer, boolean ephemeral) {
		event.sendMessageEmbeds(getInfoMessage(title, reason, footer)).setEphemeral(ephemeral).queue();	
    }
	
	public static void sendErrorMessage(InteractionHook event, String reason) {
		event.sendMessageEmbeds(getErrorMessage(reason)).queue();    	
    }
	public static MessageEmbed getErrorMessage( String reason) {
		return new EmbedBuilder()
				.setTitle("ERROR")
				.setDescription(reason)
				.setColor(new Color(255,0,0))
				.build();
	}
	public static MessageEmbed getInfoMessage(String title, String reason, String footer) {
		return new EmbedBuilder()
				.setTitle(title)
				.setDescription(reason)
				.setColor(new Color(101020))
				.setFooter(footer)
				.build();
	}
	public static MessageEmbed getInfoMessage(String title, String reason) {
		return new EmbedBuilder()
				.setTitle(title)
				.setDescription(reason)
				.setColor(new Color(101020))
				.build();
	}

}
