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

import com.mau.tdgame.models.Player;

public class NetworkConnection extends AsyncTask<Void, String, Void>{
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
				ma.startGame(Player.fromJSON(playerJSON));				
			} catch (JSONException e){
				e.printStackTrace();
			}
			
			String str;
			while((str=br.readLine())!=null){
				this.publishProgress(str);
				//create json object and update game state based on server's response
				//either here, or on another thread, send local events to server
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	protected void onProgressUpdate(String... params){
		ma.updateServer(params[0]);
	}
}
