package containers;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import botdata.BotData;
import botinternals.Client;
import botinternals.MenuManager;
import interfaces.Command;
import interfaces.NoParamCommand;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import lavaplayer.TrackScheduler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class Commands {
	public static HashMap<String, Command> commandMap = new HashMap<>();
	public static void loadCommands() {
		
		commandMap.put("join", new NoParamCommand() {
			@Override
			public void execute(Guild g, MessageChannel mc, Member m) {
				if (g.getSelfMember().getVoiceState().inVoiceChannel()) {
					Client.sendErrorMessage(mc, "Ya estoy en otro canal de voz");
					return;
				}else if(!m.getVoiceState().inVoiceChannel()) {
					Client.sendErrorMessage(mc, "Necesito que te conectes a un canal de voz");
					return;
				}
				g.getAudioManager().openAudioConnection(m.getVoiceState().getChannel());
				Client.sendInfoMessage(mc, "Conectando...", "Estableciendo conexión con <#"+m.getVoiceState().getChannel().getId()+">");
			}

			@Override
			public String getName() {
				return "Join";
			}

			@Override
			public String getHelp() {
				return "Hace que el bot se una a la llamada";
			}
			
		});
		commandMap.put("leave", new NoParamCommand() {
			@Override
			public void execute(Guild g, MessageChannel mc, Member m) {
				if(!checks(g,m,mc))return;
				g.getAudioManager().closeAudioConnection();
				TrackScheduler ts= PlayerManager.getInstance().getMusicManager(g).scheduler;
				ts.queue.clear();
				ts.nextTrack();
				
			}
			

			@Override
			public String getName() {
				return "Leave (l / disconnect / d)";
			}

			@Override
			public String getHelp() {
				return "Desconecta el bot del canal de voz";
			}
			
		});
		commandMap.put("play", new Command() {

			@Override
			public void execute(Message msg) {
				final TextChannel channel = msg.getTextChannel();

		        if (msg.getContentDisplay().split(" ").length == 1) {
		        	if (msg.getAttachments().size() == 1 && (msg.getAttachments().get(0).getContentType().equals("audio/mpeg"))||msg.getAttachments().get(0).getContentType().equals("video/mp4"))
		        		PlayerManager.getInstance().loadAndPlay(channel, msg.getAttachments().get(0).getUrl(), true);
		        	Client.sendErrorMessage(msg.getChannel(), "Hace falta un link o termino de busqueda para hacer funcionar el bot");
		            return;
		        }
		        if (!msg.getGuild().getSelfMember().getVoiceState().inVoiceChannel())msg.getGuild().getAudioManager().openAudioConnection(msg.getMember().getVoiceState().getChannel());
			    else if (!checks(msg))return;
		        String link = String.join(" ", subArray(msg.getContentDisplay().split(" "), 1, msg.getContentDisplay().split(" ").length));

		        if (!isUrl(link)) {
		            link = "ytsearch:" + link;
		        }

		        PlayerManager.getInstance().loadAndPlay(channel, link, true);
				
			}
			@Override
			public void execute(SlashCommandEvent event) {
		       if (event.getOption("term") == null || event.getOption("term").getAsString().equals("")) {
		        	event.replyEmbeds(Client.getErrorMessage("Hace falta un link o termino de busqueda para hacer funcionar el bot")).queue();
		            return;
		       }
		       if (!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel())event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
		       else if (!checks(event)) {
		    	   event.reply("Comando mal ejecutado");
		    	   return;
		       }
		       String link = event.getOption("term").getAsString();

		       if (!isUrl(link)) {
		            link = "ytsearch:" + link;
		        }
		       event.reply("Comando ejecutado");
		       PlayerManager.getInstance().loadAndPlay(event, link, true);
				
			}

			@Override
			public String getName() {
				return "Play (p)";
			}

			@Override
			public String getHelp() {
				return "Reproduce la canción del link o la busqueda indicada";
			}

			@Override
			public OptionData[] params() {
				return new OptionData[]{new OptionData(OptionType.STRING, "term", "Término o link a reproducir", true)};
			}

			
		});
		commandMap.put("nowplaying", new NoParamCommand() {
			
			@Override
			public void execute(Guild g, MessageChannel mc, Member m) {
				if (!checks(g,m,mc))return;

		        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
		        final AudioPlayer audioPlayer = musicManager.audioPlayer;
		        final AudioTrack track = audioPlayer.getPlayingTrack();

		        if (track == null) {
		            Client.sendInfoMessage(mc, "Now playing", "No hay nada reproduciondose ahora mismo");
		            return;
		        }
		        final AudioTrackInfo info = track.getInfo();
		        Client.sendInfoMessage(mc, "Now playing", info.title +" - "+info.author+ " ("+info.uri+")",formatTime(info.length));
			}
			

			@Override
			public String getName() {
				return "Now playing (np)";
			}

			@Override
			public String getHelp() {
				return "Indica lo que se está escuchando en el momento";
			}
		});
		commandMap.put("queue", new NoParamCommand() {

			@Override
			public void execute(Guild g, MessageChannel mc, Member m) {
				final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
		        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

		        if (queue.isEmpty()) {
		            Client.sendInfoMessage(mc, "Canciones en cola", "Actualmente no hay canciones en cola");
		            return;
		        }

		        final int trackCount = Math.min(queue.size(), 20);
		        final List<AudioTrack> trackList = new ArrayList<>(queue);
		        EmbedBuilder eb = new EmbedBuilder().setTitle("Canciones en cola").setColor(new Color(101020));
		        String songs = "";
		        for (int i = 0; i <  trackCount; i++) {
		            final AudioTrack track = trackList.get(i);
		            final AudioTrackInfo info = track.getInfo();
		            songs+=String.valueOf(i+1)+". "+info.title+" - "+info.author+" ["+formatTime(track.getDuration())+"]\n";
		        }

		        if (trackList.size() > trackCount) {
		            songs+="Y mas...";
		        }

		        eb.setDescription(songs);
		        mc.sendMessageEmbeds(eb.build()).queue();
				
			}

			@Override
			public String getName() {
				return "Queue (q / list)";
			}

			@Override
			public String getHelp() {
				return "Muestra la lista de las primeras 20 canciones en la cola";
			}

			
			
		});
		commandMap.put("remove", new Command() {

			@Override
			public void execute(Message msg) {
				final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(msg.getGuild());
				String arg = msg.getContentDisplay().split(" ")[1];
				if (!checks(msg))return;
				if (msg.getContentDisplay().split(" ").length < 2 || isInt(arg)) {
					Client.sendInfoMessage(msg.getChannel(), "Eliminando canción", "Eliminando "+((AudioTrack)musicManager.scheduler.queue.toArray()[Integer.parseInt(arg)-1]).getInfo().title);
					musicManager.scheduler.removeTrack(Integer.parseInt(arg));
				}else
					Client.sendErrorMessage(msg.getChannel(), "La posición tiene que ser un número");
			}
			@Override
			public void execute(SlashCommandEvent event) {
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
			
		});
		commandMap.put("repeat", new NoParamCommand() {
			@Override
			public void execute(Guild g, MessageChannel mc, Member m) {
				if (!checks(g,m,(TextChannel)mc))return;
		        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
		        final AudioTrack track = musicManager.audioPlayer.getPlayingTrack();
		        final boolean newRepeating = !musicManager.scheduler.repeating;
		        musicManager.scheduler.repeating = newRepeating;
		        Client.sendInfoMessage(mc, "Repeat", "El reproductor "+ (newRepeating?"ahora reproducirá la actual canción en bucle, "+track.getInfo().title:"dejará de reproducir "+track.getInfo().title+" en bucle"));
				
			
			}

			@Override
			public String getName() {
				return "Repeat (tr / toglerepeat / loop)";
			}

			@Override
			public String getHelp() {
				return "Repite la canción que esta sonando";
			}
			
		});
		commandMap.put("forceskip", new NoParamCommand() {
			@Override
			public void execute(Guild g, MessageChannel mc, Member m) {
				if (!checks(g,m,(TextChannel) mc))return;

		        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
		        final AudioPlayer audioPlayer = musicManager.audioPlayer;

		        if (audioPlayer.getPlayingTrack() == null) {
		            Client.sendErrorMessage(mc, "No hay canciones reproduciendose");
		            return;
		        }
		        Client.sendInfoMessage(mc, "Saltando", "Se ha saltado "+ audioPlayer.getPlayingTrack().getInfo().title);
		        musicManager.scheduler.nextTrack();
			}

			@Override
			public String getName() {
				return "ForceSkip (fs)";
			}

			@Override
			public String getHelp() {
				return "Salta la canción que está sonando";
			}

		});
		commandMap.put("stop", new NoParamCommand() {
			@Override
			public void execute(Guild g, MessageChannel mc, Member m) {
				if (!checks(g,m,(TextChannel) mc))return;

		        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);

		        musicManager.scheduler.player.stopTrack();
		        musicManager.scheduler.queue.clear();
		        musicManager.scheduler.player.setPaused(false);

		        Client.sendInfoMessage(mc, "Stop", "El bot se ha detenido y la lista ha sido eliminada");
				
			}
			@Override
			public String getName() {
				return "Stop (end)";
			}

			@Override
			public String getHelp() {
				return "Termina la canción y elimina la lista";
			}			
		});
		commandMap.put("pause", new NoParamCommand() {
			@Override
			public void execute(Guild g, MessageChannel mc, Member m) {
				if (!checks(g,m,mc))return;
		        
		        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
				musicManager.scheduler.player.setPaused(true);
				Client.sendInfoMessage(mc, "Modo pausado", "El modo pausado ha sido activado, para quitarlo usa resume");
				
			}

			@Override
			public String getName() {
				return "Pause";
			}

			@Override
			public String getHelp() {
				return "Pausa la canción que está sonando";
			}
			
		});
		commandMap.put("resume", new NoParamCommand() {

			@Override
			public void execute(Guild g, MessageChannel mc, Member m) {
		 
		        if (!checks(g,m,mc))return;
		        
		        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
				musicManager.scheduler.player.setPaused(false);
				Client.sendInfoMessage(mc, "Modo pausado", "El modo pausado ha sido desactivado");
			}

			@Override
			public String getName() {
				return "Resume (continue)";
			}

			@Override
			public String getHelp() {
				return "Desactiva el modo pausado";
			}
		});
		commandMap.put("playfirst", new Command() {

			@Override
			public void execute(Message msg) {
				final TextChannel channel = msg.getTextChannel();

		        if (msg.getContentDisplay().split(" ").length <= 1) {
		        	Client.sendErrorMessage(msg.getChannel(), "Hace falta un link o termino de busqueda para hacer funcionar el bot");
		            return;
		        }

		        final GuildVoiceState selfVoiceState = msg.getGuild().getSelfMember().getVoiceState();
		        final GuildVoiceState memberVoiceState = msg.getMember().getVoiceState();

		        if (!selfVoiceState.inVoiceChannel() && !memberVoiceState.inVoiceChannel()) {
		        	Client.sendErrorMessage(msg.getChannel(), "Tienes que estar en un canal de voz para que el bot funcione");
		            return;
		        }else if (!selfVoiceState.inVoiceChannel() && memberVoiceState.inVoiceChannel()) {
		        	msg.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
		        }else if (!memberVoiceState.inVoiceChannel() ||selfVoiceState.getChannel().getIdLong() != memberVoiceState.getChannel().getIdLong()) {
		        	Client.sendErrorMessage(msg.getChannel(), "No estamos en el mismo canal, por favor cambiate para usarme");
		        	return;
		        }

		        String link = String.join(" ", subArray(msg.getContentDisplay().split(" "), 1, msg.getContentDisplay().split(" ").length));

		        if (!isUrl(link)) {
		            link = "ytsearch:" + link;
		        }

		        PlayerManager.getInstance().loadAndPlay(channel, link, false);
				
			}
			@Override
			public void execute(SlashCommandEvent event) {
		        if (event.getOption("term") == null || event.getOption("term").getAsString().equals("")) {
		        	event.replyEmbeds(Client.getErrorMessage("Hace falta un link o termino de busqueda para hacer funcionar el bot")).queue();
		            return;
		        }

		        final GuildVoiceState selfVoiceState = event.getGuild().getSelfMember().getVoiceState();
		        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

		        if (!selfVoiceState.inVoiceChannel() && !memberVoiceState.inVoiceChannel()) {
		        	event.replyEmbeds(Client.getErrorMessage("Tienes que estar en un canal de voz para que el bot funcione")).queue();
		            return;
		        }else if (!selfVoiceState.inVoiceChannel() && memberVoiceState.inVoiceChannel()) {
		        	event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
		        }else if (!memberVoiceState.inVoiceChannel() ||selfVoiceState.getChannel().getIdLong() != memberVoiceState.getChannel().getIdLong()) {
		        	event.replyEmbeds(Client.getErrorMessage("No estamos en el mismo canal, por favor cambiate para usarme")).queue();
		        	return;
		        }

		        String link = event.getOption("term").getAsString();

		        if (!isUrl(link)) {
		            link = "ytsearch:" + link;
		        }

		        PlayerManager.getInstance().loadAndPlay(event, link, false);
				
				
			}

			@Override
			public String getName() {
				return "PlayFirst (pf / override / fp / firstplay)";
			}

			@Override
			public String getHelp() {
				return "Pone la canción al principio de la lista para que se reproduzca a continuación";
			}

			@Override
			public OptionData[] params() {
				return new OptionData[]{new OptionData(OptionType.STRING,"term", "Término o link a reproducir", true)};
			}
			
		});
		commandMap.put("skipplay", new Command() {

			@Override
			public void execute(Message msg) {
				if (!checks(msg))return;
				commandMap.get("playfirst").execute(msg);
				commandMap.get("forceskip").execute(msg);
			}
			@Override
			public void execute(SlashCommandEvent event) {
				if (!checks(event))return;
				commandMap.get("playfirst").execute(event);
				commandMap.get("forceskip").execute(event);
				
			}

			@Override
			public String getName() {
				return "SkipPlay (sp / ps / playskip)";
			}

			@Override
			public String getHelp() {
				return "Salta la canción actual y activa la indicada.\nConjunto de playfirst y forceskip";
			}

			@Override
			public OptionData[] params() {
				return new OptionData[]{new OptionData(OptionType.STRING, "term", "Término o link a reproducir", true)};
			}
			
		});
		commandMap.put("help", new Command() {

			@Override
			public void execute(Message msg) {
				if (msg.getContentDisplay().split(" ").length <2)
					Client.sendInfoMessage(msg.getChannel(), "Comandos disponibles", String.join("\n", commandMap.keySet()));
				else if(commandMap.containsKey(msg.getContentDisplay().split(" ")[1].toLowerCase())){
					Command c = commandMap.get(msg.getContentDisplay().split(" ")[1].toLowerCase());
					Client.sendInfoMessage(msg.getChannel(), c.getName(), c.getHelp());
				}
				
			}
			@Override
			public void execute(SlashCommandEvent event) {
				if (event.getOption("command")==null || !commandMap.containsKey(event.getOption("command").getAsString().toLowerCase()))
					event.replyEmbeds(Client.getInfoMessage("Comandos disponibles", String.join("\n", commandMap.keySet()))).queue();
				else if(commandMap.containsKey(event.getOption("command").getAsString().toLowerCase())){
					Command c = commandMap.get(event.getOption("command").getAsString().toLowerCase());
					event.replyEmbeds(Client.getInfoMessage(c.getName(), c.getHelp())).queue();
				}
			}

			@Override
			public String getName() {
				return "Help (h)";
			}

			@Override
			public String getHelp() {
				return "¿En serio necesitas ayuda sobre como usar el comando de ayuda?";
			}

			@Override
			public OptionData[] params() {
				return new OptionData[]{new OptionData(OptionType.STRING, "command", "Comando con el que ayudar", false)};
			}		
		});
		commandMap.put("shuffle", new NoParamCommand() {
			@Override
			public void execute(Guild g, MessageChannel mc, Member m) {
				BlockingQueue<AudioTrack> prevQueue = new LinkedBlockingQueue<>(PlayerManager.getInstance().getMusicManager(g).scheduler.queue);
				BlockingQueue<AudioTrack> postQueue = new LinkedBlockingQueue<>();
				Random rand = new Random();
				Integer num;
				while (prevQueue.size() != 0) {
					num = rand.nextInt(prevQueue.size());
					postQueue.offer((AudioTrack) prevQueue.toArray()[num]);
					prevQueue.remove((AudioTrack) prevQueue.toArray()[num]);
				}
				PlayerManager.getInstance().getMusicManager(g).scheduler.queue.clear();
				PlayerManager.getInstance().getMusicManager(g).scheduler.queue.addAll(postQueue);
				
		        final int trackCount = Math.min(postQueue.size(), 20);
		        final List<AudioTrack> trackList = new ArrayList<>(postQueue);
		        EmbedBuilder eb = new EmbedBuilder().setTitle("Nueva cola").setColor(new Color(101020));
		        String songs = "";
		        for (int i = 0; i <  trackCount; i++) {
		            final AudioTrack track = trackList.get(i);
		            final AudioTrackInfo info = track.getInfo();
		            songs+=String.valueOf(i+1)+". "+info.title+" - "+info.author+" ["+formatTime(track.getDuration())+"]\n";
		        }
		        if (trackList.size() > trackCount) {
		            songs+="Y mas...";
		        }

		        eb.setDescription(songs);
		        mc.sendMessageEmbeds(eb.build()).queue();
		    }
		    

			@Override
			public String getName() {
				return "Shuffle";
			}

			@Override
			public String getHelp() {
				return "Redistribuye de forma aleatoria la cola";
			}
			
		});
		commandMap.put("search", new Command() {

			@Override
			public void execute(Message msg) {
				if (msg.getContentDisplay().split(" ").length <= 1) {
		        	Client.sendErrorMessage(msg.getChannel(), "Hace falta un link o termino de busqueda para hacer funcionar el bot");
		            return;
		        }		        final GuildVoiceState selfVoiceState = msg.getGuild().getSelfMember().getVoiceState();
		        final GuildVoiceState memberVoiceState = msg.getMember().getVoiceState();

		        if (!selfVoiceState.inVoiceChannel() && memberVoiceState.inVoiceChannel()) 
		        	msg.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
		        if(!checks(msg))return;
				PlayerManager manager = PlayerManager.getInstance();
				List<AudioTrack> playlist = manager.getPlaylistByTerm(String.join(" ", subArray(msg.getContentDisplay().split(" "), 1, msg.getContentDisplay().split(" ").length)), msg.getGuild()).subList(0, 10);
				MenuManager menu = new MenuManager(msg.getId(), new MenuManager.MenuAction() {
					@Override
					public void execute(String value) {
						manager.loadAndPlay(msg.getTextChannel(), playlist.get(Integer.parseInt(value)).getInfo().uri, true);
						
					}
				});
				for (AudioTrack at : playlist) {
					menu.addOption(at.getInfo().title+" - "+at.getInfo().author, String.valueOf(playlist.indexOf(at)));
				}
				EmbedBuilder eb = new EmbedBuilder().setTitle("Resultados").setColor(new Color(101020));
				String desc = "";
				for (Integer i = 0; i<playlist.size(); i++) {
					AudioTrackInfo info = playlist.get(i).getInfo();
					desc += i.toString()+". "+info.title+" - "+info.author+"\n";
				}
				eb.setDescription(desc).setFooter("Seleccione en el menu cual se reproducirá");
				msg.getTextChannel().sendMessageEmbeds(eb.build()).setActionRow(menu.build()).queue();
				
			}
			@Override
			public void execute(SlashCommandEvent event) {
				if (event.getOption("term")==null || event.getOption("term").getAsString().equals("")) {
		        	event.replyEmbeds(Client.getErrorMessage("Hace falta un link o termino de busqueda para hacer funcionar el bot")).queue();;
		            return;
		        }		        final GuildVoiceState selfVoiceState = event.getGuild().getSelfMember().getVoiceState();
		        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
		        if (!selfVoiceState.inVoiceChannel() && memberVoiceState.inVoiceChannel()) 
		        	event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
		        if(!checks(event)) {
		        	event.reply("Algo ha fallado").queue();
		        	return;}
		        
				PlayerManager manager = PlayerManager.getInstance();
				List<AudioTrack> playlist = manager.getPlaylistByTerm(event.getOption("term").getAsString(), event.getGuild());
				MenuManager menu = new MenuManager(event.getId(), new MenuManager.MenuAction() {
					@Override
					public void execute(String value) {
						manager.loadAndPlay(event, playlist.get(Integer.parseInt(value)).getInfo().uri, true);
						
					}
				});
				for (AudioTrack at : playlist) {
					menu.addOption(at.getInfo().title+" - "+at.getInfo().author, String.valueOf(playlist.indexOf(at)));
				}
				String desc = "";
				for (Integer i = 0; i<playlist.size(); i++) {
					AudioTrackInfo info = playlist.get(i).getInfo();
					desc += i.toString()+". "+info.title+" - "+info.author+"\n";
				}
				event.reply("Seleccione en el menu cual se reproducirá").queue();
				event.getTextChannel().sendMessageEmbeds(Client.getInfoMessage("Resultados", desc)).setActionRow(menu.build()).queue();
				
				
			}

			@Override
			public String getName() {
				return "Search";
			}

			@Override
			public String getHelp() {
				return "Busca una canción en youtube y te da a elegir entre los 10 primeros resultados";
			}

			@Override
			public OptionData[] params() {
				return new OptionData[]{new OptionData(OptionType.STRING, "term", "Término a buscar", true)};
			}			
		});
		commandMap.put("seek", new Command() {

			@Override
			public void execute(Message msg) {
				if (!checks(msg))return;
				if (msg.getContentDisplay().split(" ").length >= 2) {
					String[] time0 = msg.getContentDisplay().split(" ")[1].split(":");
			        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(msg.getGuild());
			        String[] time = {"0","0","0"};
			        for (int i = 0;i<Integer.min(3, time0.length);i++) {
			        	time[i] = time0[(time0.length-1)-i];
			        }
					musicManager.scheduler.player.getPlayingTrack().setPosition(Integer.parseInt(time[2])*3600000+Integer.parseInt(time[1])*60000+Integer.parseInt(time[0])*1000);
					Client.sendInfoMessage(msg.getChannel(), "Salto", "El reproductor ha saltado a "+formatTime(musicManager.scheduler.player.getPlayingTrack().getPosition()));
				} else
					Client.sendErrorMessage(msg.getChannel(), "Introduce el punto al que saltar");
			}
			@Override
			public void execute(SlashCommandEvent event) {
				if (!checks(event))return;
				if (event.getOption("position") != null && isInt(event.getOption("position").getAsString().split(":"))) {
					String[] time0 = event.getOption("position").getAsString().split(":");
			        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
			        String[] time = {"0","0","0"};
			        for (int i = 0;i<Integer.min(3, time0.length);i++) {
			        	time[i] = time0[(time0.length-1)-i];
			        }
					musicManager.scheduler.player.getPlayingTrack().setPosition(Integer.parseInt(time[2])*3600000+Integer.parseInt(time[1])*60000+Integer.parseInt(time[0])*1000);
					event.replyEmbeds(Client.getInfoMessage("Salto", "El reproductor ha saltado a "+formatTime(musicManager.scheduler.player.getPlayingTrack().getPosition()))).queue();
				} else
					event.replyEmbeds(Client.getErrorMessage("Introduce el punto al que saltar")).queue();
			}

			@Override
			public String getName() {
				return "Seek";
			}

			@Override
			public String getHelp() {
				return "Avanza o retrocede al punto que se indica";
			}

			@Override
			public OptionData[] params() {
				return new OptionData[]{new OptionData(OptionType.STRING, "position", "Posición de la cancion actual a reproducir", true)};
			}			
		});
		commandMap.put("restart", new NoParamCommand() {

			@Override
			public void execute(Guild g, MessageChannel mc, Member m) {
				if (!checks(g,m,mc))return;
				final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
				musicManager.scheduler.player.getPlayingTrack().setPosition(0);
				Client.sendInfoMessage(mc, "Reiniciando", musicManager.scheduler.player.getPlayingTrack().getInfo().title +" se esta volviendo a reproducir desde cero");
			}

			@Override
			public String getName() {
				return "Restart";
			}

			@Override
			public String getHelp() {
				return "Reinicia la canción que está sonando";
			}
			
		});
		commandMap.put("ping", new NoParamCommand() {

			@Override
			public void execute(Guild g, MessageChannel mc, Member m) {
				Client.sendInfoMessage(mc,"Ping", "El ping del bot es: "+String.valueOf(Client.jda.getGatewayPing())+"ms");
				
			}

			@Override
			public String getName() {
				return "Ping";
			}

			@Override
			public String getHelp() {
				return "Manda un mensaje con el retardo entre el envio y la recepción del bot";
			}
			
		});
		commandMap.put("lyrics", new NoParamCommand() {

			@Override
			public void execute(Guild g, MessageChannel mc, Member m) {
				GuildMusicManager gmm = PlayerManager.getInstance().getMusicManager(g);
				AudioTrack track = gmm.audioPlayer.getPlayingTrack(); 
				String lyrics = Client.genius.search(track.getInfo().title).get(0).getText();
				Client.sendInfoMessage(mc, "Letra de "+track.getInfo().title, lyrics);
			}

			@Override
			public String getName() {
				return "Lyrics (l)";
			}

			@Override
			public String getHelp() {
				return "Manda la letra de la canción sonando";
			}
		
		});
		commandMap.put("info", new NoParamCommand() {

			@Override
			public String getName() {
				return "Info";
			}

			@Override
			public String getHelp() {
				return "Muestra la información global del bot";
			}

			@Override
			public void execute(Guild g, MessageChannel mc, Member m) {
				Client.sendInfoMessage(mc, Client.jda.getSelfUser().getName(), 
						 "Servers incorporados: "+String.valueOf(Client.jda.getGuilds().size())+"\n"
						+"Comandos activos: "+String.valueOf(commandMap.size())+"\n"
						+"Versión activa: "+BotData.version+"\n");
				
			}
			
		});
		commandMap.put("refreshcommands", new NoParamCommand() {

			@Override
			public String getName() {
				return "RefreshCommands";
			}

			@Override
			public String getHelp() {
				return "Reinicia los comandos de barra lateral";
			}

			@Override
			public void execute(Guild g, MessageChannel mc, Member m) {
				ArrayList<CommandData> cdList = new ArrayList<>();
				for (String k : commandMap.keySet()) {
					CommandData temp = new CommandData(k, commandMap.get(k).getHelp());
					temp.addOptions(commandMap.get(k).params());
					cdList.add(temp);
				}
				g.updateCommands().addCommands(cdList).queue();
				
				
			}
			
		});
		commandMap.put("move", new Command() {

			@Override
			public void execute(Message msg) {
				String[] params = msg.getContentDisplay().split(" ");
				if(params.length < 2) {
					Client.sendErrorMessage(msg.getChannel(), "Se necesita como mínimo la posición inicial");
				}
				if(!checks(msg.getGuild(), msg.getMember(), msg.getChannel()))return;
				if(!isInt(params[1]) || (params.length >2 && !isInt(params[2]))) {
					Client.sendErrorMessage(msg.getChannel(), "Las posiciones tienen que ser números");
				}
				TrackScheduler ts = PlayerManager.getInstance().getMusicManager(msg.getGuild()).scheduler;
				int oldPos = Integer.parseInt(params[1])-1;
				int newPos = params.length > 2? Integer.parseInt(params[2])-1:ts.queue.size()-1;
				AudioTrack at = ts.getFromQueue((int) oldPos);
				ts.removeFromQueue((int) oldPos).insertIntoQueue((int) newPos, at);
				Client.sendInfoMessage(msg.getChannel(), at.getInfo().title+" movido a la posición "+String.valueOf(newPos+1), "Se ha movido la canción "+at.getInfo().title+" de la posición "+String.valueOf(oldPos+1)+" a "+String.valueOf(newPos+1));
				
			}

			@Override
			public void execute(SlashCommandEvent event) {
				long oldPos = event.getOption("oldposition").getAsLong()-1;
				OptionMapping newPosOption = event.getOption("newposition");
				TrackScheduler ts = PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler;
				long newPos = newPosOption==null?ts.queue.size()-1:newPosOption.getAsLong()-1;
				AudioTrack at = ts.getFromQueue((int) oldPos);
				ts.removeFromQueue((int) oldPos).insertIntoQueue((int) newPos, at);
				event.replyEmbeds(Client.getInfoMessage(at.getInfo().title+" movido a la posición "+String.valueOf(newPos+1), "Se ha movido la canción "+at.getInfo().title+" de la posición "+String.valueOf(oldPos+1)+" a "+String.valueOf(newPos+1))).queue();
			}

			@Override
			public String getName() {
				return "move";
			}

			@Override
			public String getHelp() {
				return "Mueve una canción de la lista de una posición a otra";
			}

			@Override
			public OptionData[] params() {
				return new OptionData[] {new OptionData(OptionType.INTEGER, "oldposition", "Antigua posición en la cola", true), new OptionData(OptionType.INTEGER, "newposition", "Nueva posición en la cola, última por defecto", false)};
			}
			
		});
		
	}
	public static boolean checks(SlashCommandEvent event) {
		return checks(event.getGuild(),event.getMember(),event.getChannel());
	}
	public static boolean checks(Message msg) {
		return checks(msg.getGuild(),msg.getMember(),msg.getChannel());
	}
	public static boolean checks(Guild g, Member m, MessageChannel mc) {
		final GuildVoiceState selfVoiceState = g.getSelfMember().getVoiceState();
        final GuildVoiceState memberVoiceState = m.getVoiceState();
        final MessageChannel channel = mc;
		 if (!selfVoiceState.inVoiceChannel()) {
	            Client.sendErrorMessage(channel, "Necesito estar en un canal de voz para funcionar");
	            return false;
	        }
		 if (!memberVoiceState.inVoiceChannel()) {
	            Client.sendErrorMessage(channel, "No te encuentras en el mismo canal que yo, conectate a <#"+selfVoiceState.getChannel().getId()+"> para usar el comando");
	            return false;
	        }

	        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
	        	Client.sendErrorMessage(channel, "No te encuentras en el mismo canal que yo, conectate a <#"+selfVoiceState.getChannel().getId()+"> para usar el comando");
	            return false;
	        }
		return true;
	}
	public static String[] subArray(String[] a, int pos0, int posf) {
		String[] fin = new String[posf - pos0];
		for (int i = pos0; i<posf;i++) {
			fin[i-pos0] = a[i];
		}
		return fin;
	}
	public static boolean isUrl(String link) {
		try {
			new URL(link);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
	public static boolean isInt(String link) {
		try {
			Integer.parseInt(link);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	public static boolean isInt(String[] link) {
		try {
			for (String s:link)
				Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	public static String formatTime(long timeInMillis) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
}
