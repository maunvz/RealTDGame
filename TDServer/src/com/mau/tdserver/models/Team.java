package com.mau.tdserver.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Team {
	public static final int TEAM_1=1;
	public static final int TEAM_2=2;
		
	private ArrayList<String> players;
	private int score;
	private boolean flagAtBase;
	private int teamNo;
	
	public Team(int teamNo){
		this.teamNo = teamNo;
		flagAtBase = true;
		score = 0;
		players = new ArrayList<String>();
	}
	public void addPlayer(Player player){ 
		players.add(player.getName());
	}
	public int getScore(){
		return score;
	}
	public int getTeamNumber(){
		return teamNo;
	}
	public boolean flagAtBase(){
		return flagAtBase;
	}
	public JSONObject toJSON() throws JSONException{
		JSONArray playersArray = new JSONArray();
		for(String playerName:players)playersArray.put(playerName);
		JSONObject obj = new JSONObject();
		obj.put("teamNo", teamNo);
		obj.put("flagAtBase", flagAtBase);
		obj.put("score", score);
		obj.put("players", playersArray);
		return obj;
	}
}
