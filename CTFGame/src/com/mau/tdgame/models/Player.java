package com.mau.tdgame.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Player {
	public static final int NUKE = 0;//kill everyone but self
	public static final int SNIPER = 1;//kill one person from anywhere
	public static final int STRONGER = 2;//sensitivity cut by half
	public static final int RESPAWN_ANYWHERE = 3;//click the button to respawn
	public static final String[] POWER_UP_NAMES = {"NUKE","SNIPER","STRONGER","RESPAWN"};
	public boolean alive;
	public boolean connected;
	public int score;
	private int deaths;
	private int kills;
	private int team;
	private String name;
	private String QRId;
	private float sensitivity;
	public boolean stronger;
	public int killCount;
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
		stronger=false;
		powerups = new ArrayList<Integer>();
		killCount=0;
		connected = true;
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
		obj.put("stronger", stronger);
		obj.put("killCount", killCount);
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
		player.stronger = obj.getBoolean("stronger");
		player.killCount = obj.getInt("killCount");
		return player;
	}
}
