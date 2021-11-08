package lavaplayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;

import botinternals.Client;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.hc.core5.http.ParseException;


public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }
    public ArrayList<AudioTrack> getPlaylistByTerm(String term, Guild guild) {
    	ArrayList<AudioTrack> ap = new ArrayList<>();
    	this.audioPlayerManager.loadItemOrdered(this.getMusicManager(guild), "ytsearch:"+term, new AudioLoadResultHandler() {
			
			@Override
			public void trackLoaded(AudioTrack track) {
				System.out.println("track");
				
			}
			
			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				ap.addAll(playlist.getTracks());
				
			}
			
			@Override
			public void noMatches() {
				System.out.println("noMatches");
				
			}
			
			@Override
			public void loadFailed(FriendlyException exception) {
				System.out.println("failed");
				
			}
		});
    	while (ap.size() == 0) {
    		try {
				Thread.sleep(60);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return ap;
    	
    }
    @SuppressWarnings("unchecked")
	public void loadAndPlay(SlashCommandEvent event, String trackUrl, boolean end) {
        final GuildMusicManager musicManager = this.getMusicManager(event.getGuild());
        if (trackUrl.startsWith("https://open.spotify.com/playlist")) {
        	event.replyEmbeds(Client.getInfoMessage("Advertencia", "La playlist ha sido recibida, debido al límite de pedidos de Spotify esta función va lenta, por favor, no cambie la queue hasta que salga el mensaje de finalización", "Ritmo actual de añadido: 32 canciones por segundo max."));
        	String id = trackUrl.split("\\?")[0].replace("https://open.spotify.com/playlist/", "");
        	String name = "";
        	Playlist play = new Playlist.Builder().build();
        	Integer offset = 0;
        	try {
        		play = Client.spotify.getPlaylist(id).build().execute();
        		name = play.getName();
			} catch (ParseException | SpotifyWebApiException | IOException e1) {
				e1.printStackTrace();
			}
        	Paging<PlaylistTrack> pl = new Paging.Builder<PlaylistTrack>().build();
			try {
				pl = Client.spotify.getPlaylistsItems(id).offset(offset).build().execute();
			} catch (ParseException | SpotifyWebApiException | IOException e2) {
				e2.printStackTrace();
			}
        	while (pl.getItems().length == 100) {
        		try {
					pl = Client.spotify.getPlaylistsItems(id).offset(offset).build().execute();
				} catch (ParseException | SpotifyWebApiException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	for (PlaylistTrack t: pl.getItems()) {
			    		String track;
						try {
							track = Client.spotify.getTrack(t.getTrack().getId()).build().getJson();
							ObjectMapper mapper = new ObjectMapper();
							HashMap<String, Object> jsonMap = mapper.readValue(track, HashMap.class);
							ArrayList<HashMap<String, Object>> artists = (ArrayList<HashMap<String, Object>>) jsonMap.get("artists");
							String artistName = (String) (artists.get(0)).get("name");
							String songName = (String) jsonMap.get("name");
							trackUrl = "ytsearch:"+songName +" - "+artistName;
							this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
		
								@Override
								public void trackLoaded(AudioTrack track) {
								musicManager.scheduler.queue(track, end);
								}
		
								@Override
								public void playlistLoaded(AudioPlaylist playlist) {
									final AudioTrack track = playlist.getTracks().get(0);
					                musicManager.scheduler.queue(track, end);
								}
		
								@Override
								public void noMatches() {
									System.out.println("no encontrado");
								}
		
								@Override
								public void loadFailed(FriendlyException exception) {
									System.out.println("error");
								}});
							try {
								Thread.sleep(62);
							} catch (InterruptedException e) {
								System.out.println(t.getTrack().getName());
								e.printStackTrace();
							}
							
						} catch (ParseException | SpotifyWebApiException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        	}
	        	offset+=100;
        	}
        	 MessageEmbed me = new EmbedBuilder()
     				.setTitle("Añadiendo a la cola")
     				.setDescription("Playlist "+name+" añadida a la cola")
     				.setColor(new Color(101020))
     				.setFooter(((Integer)(pl.getItems().length+offset-(Integer)100)).toString()+" canciones")
     				.build();
     		event.replyEmbeds(me).queue();
        }else {
        	if (trackUrl.startsWith("https://open.spotify.com/track")) {
        		String id = trackUrl.split("\\?")[0].replace("https://open.spotify.com/track/", "");
        		String track;
				try {
					track = Client.spotify.getTrack(id).build().getJson();
					ObjectMapper mapper = new ObjectMapper();
					HashMap<String, Object> jsonMap = mapper.readValue(track, HashMap.class);
					ArrayList<HashMap<String, Object>> artists = (ArrayList<HashMap<String, Object>>) jsonMap.get("artists");
					String artistName = (String) (artists.get(0)).get("name");
					String songName = (String) jsonMap.get("name");
					trackUrl = "ytsearch:"+songName +" - "+artistName;
					
					
				} catch (ParseException | SpotifyWebApiException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                int pos = musicManager.scheduler.queue(track, end);
                MessageEmbed me = new EmbedBuilder()
        				.setTitle("Añadiendo a la cola")
        				.setDescription(track.getInfo().title+" - "+track.getInfo().author+ " ("+track.getInfo().uri+")")
        				.setColor(new Color(101020))
        				.setFooter(formatTime(track.getInfo().length)+"/// Posición en la lista:" + String.valueOf(pos))
        				.build();
        		event.replyEmbeds(me).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final AudioTrack track = playlist.getTracks().get(0);
                int pos = musicManager.scheduler.queue(track, end);
                MessageEmbed me = new EmbedBuilder()
        				.setTitle("Añadiendo a la cola")
        				.setDescription(track.getInfo().title+" - "+track.getInfo().author+ " ("+track.getInfo().uri+")")
        				.setColor(new Color(101020))
        				.setFooter(formatTime(track.getInfo().length)+"/// Posición en la lista:" + String.valueOf(pos))
        				.build();
        		event.replyEmbeds(me).queue();{
                
                }
            }
            private String formatTime(long timeInMillis) {
		        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
		        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
		        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

		        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
		    }

            @Override
            public void noMatches() {
                event.replyEmbeds(Client.getErrorMessage("No se han encontrado resultados"));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
            	 event.replyEmbeds(Client.getErrorMessage("Ha habido un error de carga"));
            }
        });
        }
    }
    @SuppressWarnings("unchecked")
	public void loadAndPlay(TextChannel channel, String trackUrl, boolean end) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
        if (trackUrl.startsWith("https://open.spotify.com/playlist")) {
        	Client.sendInfoMessage(channel, "Advertencia", "La playlist ha sido recibida, debido al límite de pedidos de Spotify esta función va lenta, por favor, no cambie la queue hasta que salga el mensaje de finalización", "Ritmo actual de añadido: 32 canciones por segundo max.");
        	String id = trackUrl.split("\\?")[0].replace("https://open.spotify.com/playlist/", "");
        	String name = "";
        	Playlist play = new Playlist.Builder().build();
        	Integer offset = 0;
        	try {
        		play = Client.spotify.getPlaylist(id).build().execute();
        		name = play.getName();
			} catch (ParseException | SpotifyWebApiException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	Paging<PlaylistTrack> pl = new Paging.Builder<PlaylistTrack>().build();
			try {
				pl = Client.spotify.getPlaylistsItems(id).offset(offset).build().execute();
			} catch (ParseException | SpotifyWebApiException | IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
        	while (pl.getItems().length == 100) {
        		try {
					pl = Client.spotify.getPlaylistsItems(id).offset(offset).build().execute();
				} catch (ParseException | SpotifyWebApiException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	for (PlaylistTrack t: pl.getItems()) {
			    		String track;
						try {
							track = Client.spotify.getTrack(t.getTrack().getId()).build().getJson();
							ObjectMapper mapper = new ObjectMapper();
							HashMap<String, Object> jsonMap = mapper.readValue(track, HashMap.class);
							ArrayList<HashMap<String, Object>> artists = (ArrayList<HashMap<String, Object>>) jsonMap.get("artists");
							String artistName = (String) (artists.get(0)).get("name");
							String songName = (String) jsonMap.get("name");
							trackUrl = "ytsearch:"+songName +" - "+artistName;
							this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
		
								@Override
								public void trackLoaded(AudioTrack track) {
								musicManager.scheduler.queue(track, end);
								}
		
								@Override
								public void playlistLoaded(AudioPlaylist playlist) {
									final AudioTrack track = playlist.getTracks().get(0);
					                musicManager.scheduler.queue(track, end);
								}
		
								@Override
								public void noMatches() {
									System.out.println("no encontrado");
								}
		
								@Override
								public void loadFailed(FriendlyException exception) {
									System.out.println("error");
								}});
							try {
								Thread.sleep(62);
							} catch (InterruptedException e) {
								System.out.println(t.getTrack().getName());
								e.printStackTrace();
							}
							
						} catch (ParseException | SpotifyWebApiException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        	}
	        	offset+=100;
        	}
        	 MessageEmbed me = new EmbedBuilder()
     				.setTitle("Añadiendo a la cola")
     				.setDescription("Playlist "+name+" añadida a la cola")
     				.setColor(new Color(101020))
     				.setFooter(((Integer)(pl.getItems().length+offset-(Integer)100)).toString()+" canciones")
     				.build();
     		channel.sendMessageEmbeds(me).queue();
        }else {
        	if (trackUrl.startsWith("https://open.spotify.com/track")) {
        		String id = trackUrl.split("\\?")[0].replace("https://open.spotify.com/track/", "");
        		String track;
				try {
					track = Client.spotify.getTrack(id).build().getJson();
					ObjectMapper mapper = new ObjectMapper();
					HashMap<String, Object> jsonMap = mapper.readValue(track, HashMap.class);
					ArrayList<HashMap<String, Object>> artists = (ArrayList<HashMap<String, Object>>) jsonMap.get("artists");
					String artistName = (String) (artists.get(0)).get("name");
					String songName = (String) jsonMap.get("name");
					trackUrl = "ytsearch:"+songName +" - "+artistName;
					
					
				} catch (ParseException | SpotifyWebApiException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                int pos = musicManager.scheduler.queue(track, end);
                MessageEmbed me = new EmbedBuilder()
        				.setTitle("Añadiendo a la cola")
        				.setDescription(track.getInfo().title+" - "+track.getInfo().author+ " ("+track.getInfo().uri+")")
        				.setColor(new Color(101020))
        				.setFooter(formatTime(track.getInfo().length)+"/// Posición en la lista:" + String.valueOf(pos))
        				.build();
        		channel.sendMessageEmbeds(me).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final AudioTrack track = playlist.getTracks().get(0);
                int pos = musicManager.scheduler.queue(track, end);
                MessageEmbed me = new EmbedBuilder()
        				.setTitle("Añadiendo a la cola")
        				.setDescription(track.getInfo().title+" - "+track.getInfo().author+ " ("+track.getInfo().uri+")")
        				.setColor(new Color(101020))
        				.setFooter(formatTime(track.getInfo().length)+"/// Posición en la lista:" + String.valueOf(pos))
        				.build();
        		channel.sendMessageEmbeds(me).queue();{
                
                }
            }
            private String formatTime(long timeInMillis) {
		        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
		        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
		        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

		        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
		    }

            @Override
            public void noMatches() {
                Client.sendErrorMessage(channel, "No se han encontrado resultados");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
            	Client.sendErrorMessage(channel, "Ha habido un error de carga");
            }
        });
        }
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }

}
