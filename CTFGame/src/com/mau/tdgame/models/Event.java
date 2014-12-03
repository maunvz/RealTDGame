package com.mau.tdgame.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Event {
	public static final int DIED = 0;
	public static final int QR_EVENT = 1;
	public static final int POWERUP_USED = 2;
	public static final int START_GAME = 3;
	
	int type;
	String player1;
	String string1;
	int int1;
	public Event(int type, String player1, String string1, int int1){
		this.type = type;
		this.player1 = player1;
		this.string1 = string1;
		this.int1 = int1;
	}
	public JSONObject toJSON() throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put("type", type);
		obj.put("player1", player1);
		obj.put("string1", string1);
		obj.put("int1", int1);
		return obj;
	}
	public int getType(){
		return type;
	}
	public String toString(){
		String str = player1 + " ";
		switch(type){
		case DIED:
			str += "died.";
			break;
		case QR_EVENT:
			str += "scanned QR "+string1;
			break;
		case POWERUP_USED:
			str+= "used "+Player.POWER_UP_NAMES[int1];
			break;
		}
		return str;
	}
	public static Event fromJSON(JSONObject obj) {
		return new Event(obj.optInt("type"), obj.optString("player1"), obj.optString("string1"), obj.optInt("int1"));
	}
}
