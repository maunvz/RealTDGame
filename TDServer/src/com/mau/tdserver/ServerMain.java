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
	Console console;
	PlayerOptionsPanel pop;
	private ArrayList<GameSession> sessions;
	private SessionOptionsPanel sop;
	Random rand;
	int selectedSession;
	public ServerMain(){
		sessions = new ArrayList<GameSession>();
		rand = new Random();
		createUI().setVisible(true);
		startListening();
		selectedSession = -1;
	}
	public JFrame createUI(){
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		console = new Console();
		
		sop = new SessionOptionsPanel(this);
		pop = new PlayerOptionsPanel(this);
		
		panel.add(sop, BorderLayout.WEST);
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
	public String[] getSessionNames(){
		String[] sessionNames = new String[sessions.size()];
		for(int i=0; i<sessions.size(); i++){
			sessionNames[i] = sessions.get(i).getName() + " " + sessions.get(i).getPort();
		}
		return sessionNames;
	}
	public void setSelectedSession(int index){
		if(index<0||index>=sessions.size())return;
		pop.setSession(sessions.get(index));
		pop.playerList.updateLists();
		console.setSession(sessions.get(index));
		selectedSession = sessions.get(index).getPort();
	}
	public void killSelectedSession(){
		getSessionByPort(selectedSession).killGame();
	}
	public void killSession(int port){
		GameSession session = getSessionByPort(port);
		if(session==null)return;
		sessions.remove(getSessionByPort(port));
		sop.sessionList.updateLists();
	}
	public GameSession getSessionByPort(int port){
		for(GameSession session:sessions){
			if(session.getPort()==port)return session;
		}
		return null;
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
					GameSession session = new GameSession(name, port, ServerMain.this);
					session.getGameState().maxScore = maxScore;
					sessions.add(session);
					sop.sessionList.updateLists();
					pw.println(session.toJSON().toString());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
