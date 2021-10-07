package main;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import botdata.BotData;

public class StartPanel extends JFrame {

	private static final long serialVersionUID = 474679858576248181L;
	boolean control = false;
	public StartPanel() {
		JComboBox<String> comboBots = new JComboBox<String>(BotData.names);
		JButton boton = new JButton("Iniciar");
		JButton invite = new JButton("Invitar");
		setLayout(new FlowLayout());
		setMinimumSize(new Dimension(200, 120));setResizable(false);
		setLocationRelativeTo(null);
		add(comboBots);add(boton);add(invite);setVisible(true);
		boton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				control = true;
				dispose();
				new ControlPanel(BotData.tokens[comboBots.getSelectedIndex()]);
				
			}
			
		});
		invite.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("https://discord.com/api/oauth2/authorize?client_id="+BotData.ids[comboBots.getSelectedIndex()]+"&permissions=3230720&scope=bot%20applications.commands"));
				} catch (IOException | URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
		});
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				if(!control)System.exit(0);
				else dispose();
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

public static void main(String[] args) {
	new StartPanel();
}


}
