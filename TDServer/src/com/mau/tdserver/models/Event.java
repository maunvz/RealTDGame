package com.mau.tdserver.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Event {
	public static final int DIED = 0;
	public static final int RESPAWNED = 1;
	public static final int CAPTURED_FLAG = 2;
	public static final int SCORED = 3;
	public static final int KILLED = 4;
	public static final int GOT_EFFECT = 5;
	
	int type;
	String player1;
	String player2;
	int value1;
	
	public Event(int type, String player1, String player2, int value1){
		this.type = type;
		this.player1 = player1;
		this.player2 = player2;
		this.value1 = value1;
	}
	public JSONObject toJSON() throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put("type", type);
		obj.put("player1", player1);
		obj.put("player2", player2);
		obj.put("value1", value1);
		return obj;
	}
}
