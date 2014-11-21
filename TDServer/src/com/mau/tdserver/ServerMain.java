package com.mau.tdserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mau.tdgame.models.Event;
import com.mau.tdgame.models.GameState;

public class ServerMain {
	private ServerSocket socket;
	private int port;
	private JTextArea textArea;
	private GameState gameState;
	private ArrayList<ClientThread> clients;
	public ServerMain(){
		port = 1726;
		createUI().setVisible(true);
		clients = new ArrayList<ClientThread>();
		print("Server ip: "+getIp().getHostAddress());
	}
	public void startListening(){
		gameState = new GameState();
		Thread listenThread = new Thread(){
			public void run(){
				try {
					socket = new ServerSocket(port);
					while(true){
						Socket client = socket.accept();
						ClientThread thread = new ClientThread(client, ServerMain.this);
						thread.execute();
						clients.add(thread);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		};
		listenThread.start();
	}
	public void startGame(){
		gameState.startGame();
		broadcastGameState();
	}
	public JFrame createUI(){
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		textArea = new JTextArea(20,40);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		
		JPanel button_panel = new JPanel();
		JButton start_listening_button = new JButton("Start Listening");
		start_listening_button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				startListening();
				print("Waiting for players to connect.");
			}
		});
		
		JButton start_game_button = new JButton("Start Game");
		start_game_button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				startGame();
				print("Starting Game.");
			}
		});
		
		button_panel.add(start_listening_button);	
		button_panel.add(start_game_button);
		panel.add(button_panel);
		
		panel.add(scrollPane);
		frame.setContentPane(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;
	}
	public synchronized void print(String str){
		textArea.append(str+"\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	
	public synchronized void updateGameState(Event event){
		gameState.processEvent(event);
		broadcastGameState();
		if(!gameState.getMessage().equals(""))print(gameState.getMessage());
	}
	public synchronized void broadcastGameState(){
		for(ClientThread client:clients){
			client.sendGameState();
		}
	}
	public synchronized GameState getGameState(){
		return gameState;
	}
	public static void main(String[] args) {
		new ServerMain();
	}
	public static InetAddress getIp(){
		try {
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements()){
				NetworkInterface ni = e.nextElement();
				Enumeration<InetAddress> a = ni.getInetAddresses();
				while(a.hasMoreElements()){
					InetAddress ia = a.nextElement();
					if(ia.isSiteLocalAddress())
						return ia;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}
}
