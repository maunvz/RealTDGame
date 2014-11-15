package com.mau.tdserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.mau.tdgame.models.Event;
import com.mau.tdgame.models.GameState;
import com.mau.tdgame.models.Player;

public class ServerMain {
	ServerSocket socket;
	int port;
	JTextArea textArea;
	GameState gameState;
	ArrayList<ClientThread> clients;
	public ServerMain(){
		port = 1726;
		createUI().setVisible(true);
		clients = new ArrayList<ClientThread>();
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
		
		panel.add(textArea);
		frame.setContentPane(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;
	}
	public synchronized void print(String str){
		textArea.append(str+"\n");
	}
	
	public synchronized void updateGameState(Event event){
		//UPDATE GAME STATE
		
		broadcastGameState();
	}
	public synchronized void broadcastGameState(){
		for(ClientThread client:clients){
			client.sendGameState();
		}
	}
	public synchronized void addPlayer(Player player){
		gameState.addPlayer(player);
	}
	public static void main(String[] args) {
		new ServerMain();
	}
}
