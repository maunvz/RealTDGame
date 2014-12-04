package com.mau.tdserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.mau.tdgame.models.Event;
import com.mau.tdgame.models.GameState;
import com.mau.tdgame.models.Player;

public class GameSession {
	private String name;
	private int sessionPort;
	
	private ArrayList<ClientThread> clients;
	private ServerSocket socket;
	private GameState gameState;
	StringBuffer consoleText;
	private boolean active;
	ServerMain main;
	Thread killThread;
	public GameSession(String name, int sessionPort, ServerMain main){
		clients = new ArrayList<ClientThread>();
		this.sessionPort = sessionPort;
		this.name = name;
		this.main=main;
		active=true;
		consoleText = new StringBuffer();
		print("Listening on port: "+sessionPort);
		startListening();
		startKillThread();
	}
	public void startKillThread(){
		killThread = new Thread(){
			public void run(){
				try {
					Thread.sleep(1000*60*10);
					killGame();
				} catch (InterruptedException e) {
					//e.printStackTrace();
				}
			}
		};
		killThread.start();
	}
	public void restartKillThread(){
		if(killThread!=null)killThread.interrupt();
		startKillThread();
	}
	public void startListening(){
		gameState = new GameState();
		Thread listenThread = new Thread(){
			public void run(){
				try {
					socket = new ServerSocket(sessionPort);
					while(active){
						Socket client = socket.accept();
						ClientThread thread = new ClientThread(client, GameSession.this);
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
	public synchronized void print(String str){
		consoleText.append(str+"\n");
		if(main.selectedSession==sessionPort)main.console.setSession(this);
	}
	public synchronized GameState getGameState(){
		return gameState;
	}
	public void kickPlayer(Player player){
		gameState.removePlayer(player);
		if(main.selectedSession==sessionPort)main.pop.playerList.updateLists();
		for(ClientThread client:clients){
			if(client.player==null)continue;
			if(client.player.equals(player)){
				removeClient(client);
				break;
			}
		}
		broadcastGameState();
		print("Active Players: "+gameState.activePlayers);
		if(gameState.activePlayers<=0){
			killGame();
		}
		print("Kicked "+player.getName());
	}
	public void removeClient(ClientThread client){
		client.closeConnection();
		client.cancel(true);
		clients.remove(client);
	}
	public void killPlayer(Player player){
		gameState.playerDies(player);
		broadcastGameState();
	}
	public void respawnPlayer(Player player){
		gameState.playerRespawns(player);
		broadcastGameState();
	}
	public void powerPlayer(Player player, int power){
		player.powerups.add(power);
		broadcastGameState();
	}
	public void setPlayerSensitivity(Player player, float sensitivity){
		player.setSensitivity(sensitivity);
		broadcastGameState();
	}
	public void messagePlayer(Player player, String message){
		if(player==null)return;
		gameState.playerMessage = player.getName()+"`"+message;
		broadcastGameState();
	}
	public void messageAll(String message){
		gameState.globalMessage=message;
		broadcastGameState();
	}
	public void disconnectPlayer(Player player){
		gameState.disconnectPlayer(player);
		if(gameState.activePlayers<=0){
			killGame();
		}
	}
	public synchronized void broadcastGameState(){
		for(ClientThread client:clients){
			client.sendGameState();
		}
		gameState.clearMessage();
		gameState.globalMessage="";
		gameState.playerMessage="";
	}
	public synchronized void updateGameState(Event event){
		print(event.toString());
		if(event.getType()==Event.START_GAME){
			startGame();
			broadcastGameState();
			return;
		}
		gameState.processEvent(event);
		broadcastGameState();
		if(!gameState.getMessage().equals(""))print(gameState.getMessage());
		print("Game ended.");
		if(gameState.gameEnded)killGame();
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
		killGame();
	}
	public void killGame(){
		active=false;
		for(ClientThread client:clients){
			client.closeConnection();
		}
		main.killSession(sessionPort);
	}
	public synchronized void addPlayer(Player player){
		gameState.addPlayer(player);
		main.pop.playerList.updateLists();
	}
	public int getPort(){
		return sessionPort;
	}
	public String getName(){
		return name;
	}
	public JSONObject toJSON(){
		JSONObject obj = new JSONObject();
		try {
			obj.put("name", name);
			obj.put("port", sessionPort);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
}
