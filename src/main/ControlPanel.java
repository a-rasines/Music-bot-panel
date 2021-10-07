package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import botdata.BotData;
import botinternals.Client;
import botinternals.SlashOption;
import containers.Commands;
import interfaces.NoParamCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;

public class ControlPanel extends JFrame {

	private static final long serialVersionUID = 6622340982815215274L;
	
	public ControlPanel(String token) {
		Long id = BotData.ids[(Arrays.asList(BotData.tokens)).indexOf(token)];
		Client.generateJDA(token, BotData.prefix[(Arrays.asList(BotData.tokens)).indexOf(token)]);
		JButton exit = new JButton("Terminar");
		JButton invitar = new JButton("Invitar");
		JLabel foto = new JLabel(Client.jda.getSelfUser().getName());
		try {
			foto = new JLabel(new ImageIcon(ImageIO.read(new URL(Client.jda.getSelfUser().getAvatarUrl()))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setLayout(new GridLayout(1,2));
		JPanel panelSup = new JPanel(new GridLayout(2,1));
		panelSup.add(invitar);panelSup.add(exit);
		add(foto);add(panelSup);setVisible(true);
		setMinimumSize(new Dimension(300,200));setResizable(false);
		setLocationRelativeTo(null);
		Thread slashLoad = new Thread() {
			@Override
			public void run() {
				while (Client.jda.getGuilds().size() == 0) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				for (Guild g : Client.jda.getGuilds())
					if (g.retrieveCommands().complete().size() != Commands.commandMap.size())
						((NoParamCommand)Commands.commandMap.get("refreshcommands")).execute(g,null,null);
			}
		};
		slashLoad.start();
		invitar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("https://discord.com/api/oauth2/authorize?client_id="+id.toString()+"&permissions=3230720&scope=bot%20applications.commands"));
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
				
			}
			
		});
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Client.jda.shutdown();
				System.exit(0);
				
			}
			
		});
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				Client.jda.shutdown();
				System.exit(0);
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				
			}
			
		});
		
		
	}
}
