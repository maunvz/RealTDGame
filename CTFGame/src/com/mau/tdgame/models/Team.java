package com.mau.tdgame.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Team {
	public static final int TEAM_1=1;
	public static final int TEAM_2=2;
		
	private ArrayList<String> players;
	public boolean flagAtBase;
	private int teamNo;
	
	public Team(int teamNo){
		this.teamNo = teamNo;
		flagAtBase = true;
		players = new ArrayList<String>();
	}
	public void addPlayer(String playerName){ 
		players.add(playerName);
	}
	public String[] listOfPlayers(){
		String[] playerList = new String[players.size()];
		for(int i=0; i<players.size(); i++){
			playerList[i] = players.get(i);
		}
		return playerList;
	}
	public JSONObject toJSON() throws JSONException{
		JSONArray playersArray = new JSONArray();
		for(String playerName:players)playersArray.put(playerName);
		JSONObject obj = new JSONObject();
		obj.put("teamNo", teamNo);
		obj.put("flagAtBase", flagAtBase);
		obj.put("players", playersArray);
		return obj;
	}
	public static Team fromJSON(JSONObject obj) throws JSONException{
		Team team = new Team(obj.getInt("teamNo"));
		team.flagAtBase = obj.getBoolean("flagAtBase");
		JSONArray players = obj.getJSONArray("players");
		for(int i=0; i<players.length(); i++){
			team.addPlayer(players.getString(i));
		}
		return team;
	}
}
