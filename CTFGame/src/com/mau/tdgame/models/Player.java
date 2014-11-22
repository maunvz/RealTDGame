package com.mau.tdgame.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Player {
	//effects
	public static final int NO_EFFECT = 0;
	public static final int HAS_NUKE = 1;
	public static final int HAS_SNIPER = 2;
	public static final int SUPER_SPEED = 3;
	
	public boolean alive;
	public int score;
	private int effect;
	private int effectValue;
	private int deaths;
	private int kills;
	private int team;
	private String name;
	private String QRId;
	private float sensitivity;
	
	public Player(String name, String QRId, int team){
		this.name = name;
		this.team = team;
		this.QRId = QRId;
		alive = true;
		score = 0;
		effect = NO_EFFECT;
		effectValue = 0;
		deaths = 0;
		kills = 0;
		this.sensitivity = 1;
	}
	public String getName(){
		return name;
	}
	public String getQRId(){
		return QRId;
	}
	public int getTeam(){
		return team;
	}
	public float getSensitivity(){
		return sensitivity;
	}
	public void effect(int effect){
		this.effect=effect;
	}
	public void setSensitivity(float sensitivity){
		this.sensitivity=sensitivity;
	}
	public JSONObject toJSON() throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("team", team);
		obj.put("QRId", QRId);
		obj.put("alive", alive);
		obj.put("score", score);
		obj.put("effect", effect);
		obj.put("effectValue", effectValue);
		obj.put("deaths", deaths);
		obj.put("kills", kills);	
		obj.put("sensitivity", sensitivity);
		return obj;
	}
	public static Player fromJSON(JSONObject obj) throws JSONException{		
		Player player = new Player(obj.getString("name"),obj.getString("QRId"),obj.getInt("team"));
		player.alive = obj.getBoolean("alive");
		player.score = obj.getInt("score");
		player.effect = obj.getInt("effect");
		player.effectValue = obj.getInt("effectValue");
		player.deaths = obj.getInt("deaths");
		player.kills = obj.getInt("kills");
		player.sensitivity = (float) obj.getDouble("sensitivity");
		return player;
	}
}
