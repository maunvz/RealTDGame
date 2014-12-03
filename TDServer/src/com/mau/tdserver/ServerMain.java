package com.mau.tdserver;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mau.tdgame.models.Constants;

public class ServerMain {
	private int initialPort = Constants.INITIAL_PORT;
	private Console console;
	private GlobalOptionsPanel gop;
	private PlayerOptionsPanel pop;
	private ArrayList<GameSession> sessions;
	
	Random rand;
	
	public ServerMain(){
		sessions = new ArrayList<GameSession>();
		rand = new Random();
		createUI().setVisible(true);
		startListening();
	}
	public void print(String str){
		console.print(str);
	}
	public JFrame createUI(){
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		console = new Console();
		gop = new GlobalOptionsPanel(this);
		pop = new PlayerOptionsPanel(this);
		
		panel.add(gop, BorderLayout.WEST);
		panel.add(console, BorderLayout.CENTER);
		panel.add(pop, BorderLayout.EAST);
		
		frame.setContentPane(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;
	}
	public void startListening(){
		Thread listenThread = new Thread(){
			public void run(){
				try {
					@SuppressWarnings("resource")
					ServerSocket socket = new ServerSocket(initialPort);
					while(true){
						Socket client = socket.accept();
						new ConnectionThread(client).start();
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		};
		listenThread.start();
	}
	public int getUnusedPort(){
		int port = 1727+rand.nextInt(256); 
		try {
			ServerSocket socket = new ServerSocket(port);
			socket.close();
		} catch (IOException e) {
			return getUnusedPort();
		}
		return port;
	}
	public static void main(String[] args) {
		new ServerMain();
	}
	public class ConnectionThread extends Thread{
		Socket socket;
		public ConnectionThread(Socket socket){
			this.socket = socket;
		}
		public void run(){
			try {
				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				String action = br.readLine();
				if(action.equals(Constants.JOIN_ROOM)){
					JSONArray sessionArray = new JSONArray();
					for(GameSession session:sessions){
						JSONObject sessionJSON = session.toJSON();
						sessionArray.put(sessionJSON);
					}
					pw.println(sessionArray.toString());
				} else if(action.equals(Constants.CREATE_ROOM)){
					String name = br.readLine();
					int maxScore = Integer.parseInt(br.readLine());
					int port = getUnusedPort();
					GameSession session = new GameSession(name, port);
					session.getGameState().maxScore = maxScore;
					sessions.add(session);
					print("Created game session "+name+" on port "+port);
					pw.println(session.toJSON().toString());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
