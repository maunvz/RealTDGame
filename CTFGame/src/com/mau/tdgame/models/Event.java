package com.mau.tdgame.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Event {
	public static final int DIED = 0;
	public static final int QR_EVENT = 1;
	
	int type;
	String player1;
	String value1;
	
	public Event(int type, String player1, String value1){
		this.type = type;
		this.player1 = player1;
		this.value1 = value1;
	}
	public JSONObject toJSON() throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put("type", type);
		obj.put("player1", player1);
		obj.put("value1", value1);
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
			str += "scanned QR "+value1;
			break;				
		}
		return str;
	}
	public static Event fromJSON(JSONObject obj) {
		return new Event(obj.optInt("type"), obj.optString("player1"), obj.optString("value1"));
	}
}
