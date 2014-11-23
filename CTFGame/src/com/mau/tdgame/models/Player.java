package com.mau.tdgame.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Player {
	public static final int NUKE = 1;//kill everyone but self
	public static final int SNIPER = 2;//kill one person from anywhere
	public static final int IMMORTAL = 3;//can be killed once and not die
	public static final int HALF_SENSITIVITY = 4;//sensitivity cut by half
	public static final int RESPAWN_ANYWHERE = 5;//click the button to respawn
	public static final int FLAG_STEAL = 6;//steal the flag from any player, including your own
	
	public boolean alive;
	public int score;
	private int deaths;
	private int kills;
	private int team;
	private String name;
	private String QRId;
	private float sensitivity;
	public ArrayList<Integer> powerups;
	
	public Player(String name, String QRId, int team){
		this.name = name;
		this.team = team;
		this.QRId = QRId;
		alive = true;
		score = 0;
		deaths = 0;
		kills = 0;
		sensitivity = 1;
		powerups = new ArrayList<Integer>();
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
		obj.put("deaths", deaths);
		obj.put("kills", kills);	
		obj.put("sensitivity", sensitivity);
		JSONArray powerups = new JSONArray();
		for(int i=0; i<this.powerups.size();i++){
			powerups.put(this.powerups.get(i));
		}
		obj.put("powerups", powerups);
		return obj;
	}
	public static Player fromJSON(JSONObject obj) throws JSONException{		
		Player player = new Player(obj.getString("name"),obj.getString("QRId"),obj.getInt("team"));
		player.alive = obj.getBoolean("alive");
		player.score = obj.getInt("score");
		player.deaths = obj.getInt("deaths");
		player.kills = obj.getInt("kills");
		player.sensitivity = (float) obj.getDouble("sensitivity");
		player.powerups = new ArrayList<Integer>();
		JSONArray powerups = obj.getJSONArray("powerups");
		for(int i=0; i<powerups.length(); i++){
			player.powerups.add(powerups.getInt(i));
		}
		return player;
	}
}
