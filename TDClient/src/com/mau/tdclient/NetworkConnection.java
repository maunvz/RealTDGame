package com.mau.tdclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.mau.tdgame.models.Event;
import com.mau.tdgame.models.GameState;
import com.mau.tdgame.models.Player;

public class NetworkConnection extends AsyncTask<Void, GameState, Void>{
	private Socket socket;
	private String host;
	private String username;
	private int teamNo;

	private PrintWriter pw;
	private BufferedReader br;
	private MainActivity ma;
	
	public NetworkConnection(String host, String username, int teamNo, MainActivity ma){
		super();
		this.host = host;
		this.ma=ma;
		this.username = username;
		this.teamNo = teamNo;
	}
	@Override
	protected Void doInBackground(Void... params) {
		try {
			socket = new Socket();
			SocketAddress address = new InetSocketAddress(host, 1726);
			socket.connect(address);
			
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(socket.getOutputStream(), true);
			
			pw.println(username);
			pw.println(teamNo);
			
			try{
				JSONObject playerJSON = new JSONObject(br.readLine());
				JSONObject gameStateJSON = new JSONObject(br.readLine());
				ma.joinGame(Player.fromJSON(playerJSON), GameState.fromJSON(gameStateJSON));
			} catch (JSONException e){
				e.printStackTrace();
			}
			
			String str;
			while((str=br.readLine())!=null){
				try {
					GameState state = GameState.fromJSON(new JSONObject(str));
					publishProgress(state);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	protected void onProgressUpdate(GameState... params){
		ma.updateGameState(params[0]);
	}
	public void sendEvent(Event event){
		try {
			pw.println(event.toJSON().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
