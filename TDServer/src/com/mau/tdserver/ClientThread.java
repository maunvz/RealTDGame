package com.mau.tdserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.SwingWorker;

import org.json.JSONException;
import org.json.JSONObject;

import com.mau.tdgame.models.Event;
import com.mau.tdgame.models.Player;

public class ClientThread extends SwingWorker<Void, Integer>{
	private Socket socket;
	private PrintWriter pw;
	private BufferedReader br;
	Player player;
	private GameSession session;
	private boolean dead;
	public ClientThread(Socket socket, GameSession session){
		this.socket = socket;
		this.session = session;
		dead=false;
	}
	@Override
	protected Void doInBackground() throws Exception {
		try {
			pw = new PrintWriter(socket.getOutputStream(), true);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			//Create the player
			String username = br.readLine();
			if(username.equals("server_scan"))return null;
			if(session.getGameState().getPlayerByName(username)!=null){
				pw.println("Sorry, player by that name already exists.");
				return null;
			}
			String QRId = br.readLine();
			if(session.getGameState().getPlayerByQRId(QRId)!=null){
				pw.println("Sorry, player by that QR already exists.");
				return null;
			}
			int team = Integer.parseInt(br.readLine());
			player = new Player(username, QRId, team);
			
			//Add player to game state
			session.print(player.getName()+" has joined team "+player.getTeam()+" with QR "+player.getQRId()+" and IP "+socket.getInetAddress().getHostAddress());
			session.addPlayer(player);
			//Send the player to the client
			pw.println(player.toJSON().toString());
			pw.println(session.getGameState().toJSON().toString());
			//Tell everyone that player joined game
			session.broadcastGameState();
			
			//Listen for events
			String str;
			while((str=br.readLine())!=null){
				Event event = Event.fromJSON(new JSONObject(str));
				session.updateGameState(event);
			}
			session.print(username+" is gone.");
		} catch (IOException e) {
			e.printStackTrace();
			session.print(e.getMessage());
		}
		dead=true;
		return null;
	}
	public void sendGameState(){
		if(dead)return;
		try {
			pw.println(session.getGameState().toJSON().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public void closeConnection(){
		pw.println("bye");
	}
}