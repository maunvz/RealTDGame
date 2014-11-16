package com.mau.tdgame.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameState {
	public static final int DEFAULT_SENSITIVITY = 650;
	private boolean gameStarted;
	private Team team1;
	private Team team2;
	private ArrayList<Player> players;
	
	public GameState(){
		team1 = new Team(Team.TEAM_1);
		team2 = new Team(Team.TEAM_2);
		players = new ArrayList<Player>();
		gameStarted = false;
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
		return state;
	}
	public boolean gameStarted(){
		return gameStarted;
	}
	public void startGame(){
		gameStarted=true;
	}
	public void processEvent(Event event){
		switch(event.getType()){
		case Event.DIED:
			
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
}
