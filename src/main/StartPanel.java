package main;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import botdata.ClientData;

public class StartPanel extends JFrame {

	private static final long serialVersionUID = 474679858576248181L;
	boolean control = false;
	final static String SAVEDIR = new File("").getAbsolutePath().concat("\\bots.bdata");
	public static ArrayList<ClientData> bots = new ArrayList<>();
	int c = 0;
	JComboBox<ClientData> comboBots;
	public StartPanel() {
		bots.clear();
		c = 0;
		try {
			bots.addAll(Arrays.asList((ClientData[])Class.forName("botdata.BotData").getField("defaultBots").get(null)));
		}catch(Exception e) {e.printStackTrace();}//Para prevenir fallos por la falta de BotData en el caso el repo de github
		bots.addAll(loadBots());
		ClientData[] temp = new ClientData[bots.size()];
		bots.forEach(v->{
			temp[c] = v;
			c++;
		});
		comboBots = new JComboBox<ClientData>(temp);
		JButton boton = new JButton("Iniciar");
		JButton invite = new JButton("Invitar");
		JButton add = new JButton("+");
		JButton remove = new JButton("-");
		setLayout(new FlowLayout());
		setMinimumSize(new Dimension(200, 120));setResizable(false);
		setLocationRelativeTo(null);
		add(comboBots);add(boton);add(invite);add(add);add(remove);setVisible(true);
		boton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				control = true;
				dispose();
				new ControlPanel((ClientData) comboBots.getSelectedItem());
				
			}
			
		});
		invite.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				((ClientData) comboBots.getSelectedItem()).invite();			
			}
			
		});
		add.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String token = JOptionPane.showInputDialog("Token del nuevo bot");
				String id0 = "a";
				while(!isLong(id0)) {
					//<html><font color=\"red\">hello world!</font></html>
					id0 = JOptionPane.showInputDialog("<html>Introduzca la ID de la aplicacion del bot (https://discord.com/developers/applications/<b><font color=\"red\">ID</font></b>/bot)</html>");
					if(id0==null || id0.equals(""))return;
				}
				Long id = Long.parseLong(id0);
				String name = JOptionPane.showInputDialog("Nombre con el que guardar el bot");
				ClientData nuevo = new ClientData(name, id, token);
				bots.add(nuevo);
				comboBots.addItem(nuevo);
			}
			
		});
		remove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ClientData rem = (ClientData) comboBots.getSelectedItem();
				bots.remove(bots.indexOf(rem));
				comboBots.removeItem(rem);
			}
			
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(!control) {
					saveBots();
					System.exit(0);
				}
				else dispose();
				
			}
			
		});
	}
	public boolean isLong(String s) {
		try {
			Long.parseLong(s);
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	@SuppressWarnings("unchecked")
	public static ArrayList<ClientData> loadBots() {
		if(!(new File(SAVEDIR).isFile()))return new ArrayList<ClientData>();
		try {
		    ObjectInputStream objectinputstream = new ObjectInputStream(new FileInputStream(SAVEDIR));
		    ArrayList<ClientData> cd = (ArrayList<ClientData>) objectinputstream.readObject();
		    objectinputstream.close();
		    return cd;
		 } catch (Exception e) {
			 e.printStackTrace();
		     JOptionPane.showMessageDialog(null, "Un error inesperado ha ocurrido con la carga de los bots: "+e.getMessage());
		     return null;
		 }
	}
	public static void saveBots() {
		@SuppressWarnings("unchecked")
		ArrayList<ClientData> save = (ArrayList<ClientData>) bots.clone();
		try {
			save.removeAll(Arrays.asList((ClientData[])Class.forName("botdata.BotData").getField("defaultBots").get(null)));
		}catch(Exception e) {} //Para prevenir fallos por la falta de BotData en el caso el repo de github
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVEDIR));
		    oos.writeObject(save);
		    oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Un error inesperado ha ocurrido con el guardado de los bots: "+e.getMessage());
		}
	    
	}

public static void main(String[] args) {
	new StartPanel();
}


}
