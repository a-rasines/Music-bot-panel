package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import botdata.ClientData;
import botinternals.Client;
import commands.bot.RefreshCommand;
import containers.Commands;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class ControlPanel extends JFrame {

	private static final long serialVersionUID = 6622340982815215274L;
	
	public ControlPanel(ClientData data) {
		Client.generateJDA(data);
		if(Client.jda == null)return;
		JButton exit = new JButton("Terminar");
		JButton invitar = new JButton("Invitar");
		
		final JLabel foto = new JLabel(Client.jda.getSelfUser().getName());
		new Thread() {
			public void run() {
				try {
					foto.setIcon(new ImageIcon(ImageIO.read(new URL(Client.jda.getSelfUser().getEffectiveAvatarUrl()).openStream())));
					foto.setText("");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
		setLayout(new GridLayout(1,2));
		JPanel panelSup = new JPanel(new GridLayout(3,1));
		panelSup.add(invitar);panelSup.add(exit);
		add(foto);add(panelSup);setVisible(true);
		setMinimumSize(new Dimension(300,150));setResizable(false);
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
					try {
					if (g.retrieveCommands().complete().size() != Commands.commandMap.size())
						RefreshCommand.refresh(g);
					}catch(ErrorResponseException e) {
						JOptionPane.showMessageDialog(null, "En uno o mas servidores no se ha autorizado los comandos de barra lateral");
					}
			}
		};
		slashLoad.start();
		invitar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				data.invite();
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
				StartPanel.saveBots();
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
