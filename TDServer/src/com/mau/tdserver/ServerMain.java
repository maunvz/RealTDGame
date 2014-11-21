package com.mau.tdserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.mau.tdgame.models.Event;
import com.mau.tdgame.models.GameState;

public class ServerMain {
	private ArrayList<ClientThread> clients;
	private ServerSocket socket;
	private int port;
	private GameState gameState;
	
	private Console console;
	private GlobalOptionsPanel gop;
	private PlayerList playerList;
	private PlayerOptionsPanel pop;
	
	public ServerMain(){
		port = 1726;
		createUI().setVisible(true);
		clients = new ArrayList<ClientThread>();
		print("Server ip: "+getIp().getHostAddress());
		startListening();
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
					System.exit(0);
				}
			}
		};
		listenThread.start();
		print("Waiting for players to connect.");
	}
	public void startGame(){
		print("Starting Game.");
		gameState.startGame();
		broadcastGameState();
	}
	public void endGame(){
		print("Ending Game.");
		gameState.endGame();
		broadcastGameState();
	}
	public JFrame createUI(){
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		
		console = new Console();
		gop = new GlobalOptionsPanel(this);
		playerList = new PlayerList(this);
		pop = new PlayerOptionsPanel(this);
		
		panel.add(gop);
		panel.add(console);
		panel.add(playerList);
		panel.add(pop);
		
		frame.setContentPane(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;
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
	public void print(String str){
		console.print(str);
	}
}
