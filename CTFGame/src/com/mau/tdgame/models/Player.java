package com.mau.tdgame.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Player {
	//effects
	public static final int NO_EFFECT = 0;
	public static final int HAS_NUKE = 1;
	public static final int HAS_SNIPER = 2;
	public static final int SUPER_SPEED = 3;
	
	private boolean alive;
	private boolean hasFlag;
	private int score;
	private int effect;
	private int effectValue;
	private int deaths;
	private int kills;
	private int team;
	private String name;
	private int sensitivity;
	
	public Player(String name, int team){
		this.name = name;
		this.team = team;
		alive = true;
		hasFlag = false;
		score = 0;
		effect = NO_EFFECT;
		effectValue = 0;
		deaths = 0;
		kills = 0;
		this.sensitivity = GameState.DEFAULT_SENSITIVITY;
	}
	public void die(){
		alive = false;
	}
	public void respawn(){
		alive = true;
	}
	public String getName(){
		return name;
	}
	public int getTeam(){
		return team;
	}
	public int getSensitivity(){
		return sensitivity;
	}
	public JSONObject toJSON() throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("team", team);
		obj.put("alive", alive);
		obj.put("hasFlag", hasFlag);
		obj.put("score", score);
		obj.put("effect", effect);
		obj.put("effectValue", effectValue);
		obj.put("deaths", deaths);
		obj.put("kills", kills);	
		obj.put("sensitivity", sensitivity);
		return obj;
	}
	public static Player fromJSON(JSONObject obj) throws JSONException{		
		Player player = new Player(obj.getString("name"),obj.getInt("team"));
		player.alive = obj.getBoolean("alive");
		player.hasFlag = obj.getBoolean("hasFlag");
		player.score = obj.getInt("score");
		player.effect = obj.getInt("effect");
		player.effectValue = obj.getInt("effectValue");
		player.deaths = obj.getInt("deaths");
		player.kills = obj.getInt("kills");
		player.sensitivity = obj.getInt("sensitivity");
		return player;
	}
}
