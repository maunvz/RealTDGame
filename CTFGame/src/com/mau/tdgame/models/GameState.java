package com.mau.tdgame.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameState {
	public static final int SCORE_VALUE=5;
	public static final int KILL_VALUE=2;
	public static final int DEFAULT_SENSITIVITY = 650;
	private boolean gameStarted;
	private Team team1;
	private Team team2;
	private ArrayList<Player> players;
	private String message;
	
	public GameState(){
		team1 = new Team(Team.TEAM_1);
		team2 = new Team(Team.TEAM_2);
		players = new ArrayList<Player>();
		gameStarted = false;
		message="";
	}
	public boolean gameStarted(){
		return gameStarted;
	}
	public void startGame(){
		gameStarted=true;
	}
	public void processEvent(Event event){
		message = event.toString();
		Player player1 = getPlayerByName(event.player1);
		
		switch(event.getType()){
		case Event.DIED:
			player1.alive=false;
			if(player1.hasFlag){
				player1.hasFlag=false;
				if(player1.getTeam()==Team.TEAM_1)team2.flagAtBase=true;
				if(player1.getTeam()==Team.TEAM_2)team1.flagAtBase=true;
			}
			break;
		case Event.QR_EVENT:
			
			break;
		}
	}
	public void addPlayer(Player player){
		players.add(player);
		if(player.getTeam()==Team.TEAM_1)team1.addPlayer(player.getName());
		if(player.getTeam()==Team.TEAM_2)team2.addPlayer(player.getName());
	}
	public String[][] listPlayers(){
		String[][] playerList = new String[2][];
		playerList[0] = team1.listOfPlayers();
		playerList[1] = team2.listOfPlayers();
		return playerList;
	}
	public Player getPlayerByName(String name){
		Player player = null;
		for(Player p1:players){
			if(p1.getName().equals(name))player=p1;
		}
		return player;
	}
	public Player getPlayerByQRId(String QRId){
		Player player = null;
		for(Player p1:players){
			if(p1.getQRId().equals(QRId))player=p1;
		}
		return player;
	}
	public String getMessage(){
		return message;
	}
	public JSONObject toJSON() throws JSONException{
		JSONArray playerArray = new JSONArray();
		for(Player player:players){
			playerArray.put(player.toJSON());
		}
		
		JSONObject obj = new JSONObject();
		obj.put("gameStarted", gameStarted);
		obj.put("team1", team1.toJSON());
		obj.put("team2", team2.toJSON());
		obj.put("players", playerArray);
		obj.put("message", message);
		return obj;
	}
	public static GameState fromJSON(JSONObject obj) throws JSONException{
		GameState state = new GameState();
		state.gameStarted = obj.getBoolean("gameStarted");
		state.team1 = Team.fromJSON(obj.getJSONObject("team1"));
		state.team2 = Team.fromJSON(obj.getJSONObject("team2"));
		JSONArray players = obj.getJSONArray("players");
		for(int i=0; i<players.length(); i++){
			state.players.add(Player.fromJSON(players.getJSONObject(i)));
		}
		state.message=obj.getString("message");
		return state;
	}
}
