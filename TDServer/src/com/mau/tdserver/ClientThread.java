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
	private Player player;
	private ServerMain main;
	public ClientThread(Socket socket, ServerMain main){
		this.socket = socket;
		this.main = main;
	}
	@Override
	protected Void doInBackground() throws Exception {
		try {
			pw = new PrintWriter(socket.getOutputStream(), true);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			//Create the player
			String username = br.readLine();
			int team = Integer.parseInt(br.readLine());
			player = new Player(username, team);
			
			//Add player to game state
			main.print(player.getName()+" has joined team "+player.getTeam());
			main.addPlayer(player);
			main.print("added player");
			//Send the player to the client
			pw.println(player.toJSON().toString());
			main.print("sent player");
			pw.println(main.gameState.toJSON().toString());
			main.print("sent gamestate");
			//Tell everyone that player joined game
			main.broadcastGameState();
			main.print("broadcasted game state, waiting for events.");
			
			//Listen for events
			String str;
			while((str=br.readLine())!=null){
				Event event = Event.fromJSON(new JSONObject(str));
				main.updateGameState(event);
				main.print(event.toString());
				if(str.equals("bye")){
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void sendGameState(){
		try {
			pw.println(main.gameState.toJSON().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}