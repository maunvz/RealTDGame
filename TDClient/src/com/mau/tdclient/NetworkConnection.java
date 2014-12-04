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

import com.mau.tdgame.models.Constants;
import com.mau.tdgame.models.Event;
import com.mau.tdgame.models.GameState;
import com.mau.tdgame.models.Player;

public class NetworkConnection extends AsyncTask<Void, GameState, Void>{
	private Socket socket;
	private String username;
	private String QRId;
	private int teamNo;
	public int port;

	private PrintWriter pw;
	private BufferedReader br;
	private MainActivity ma;
	
	public NetworkConnection(int port, String username, String QRId, int teamNo, MainActivity ma){
		super();
		this.port=port;
		this.ma=ma;
		this.username = username;
		this.teamNo = teamNo;
		this.QRId = QRId;
	}
	@Override
	protected Void doInBackground(Void... params) {
		try {
			socket = new Socket();
			SocketAddress address = new InetSocketAddress(Constants.host, port);
			socket.connect(address);
			
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(socket.getOutputStream(), true);
			
			pw.println(username);
			pw.println(QRId);
			pw.println(teamNo);
			
			try{
				JSONObject playerJSON = new JSONObject(br.readLine());
				JSONObject gameStateJSON = new JSONObject(br.readLine());
				ma.joinGame(Player.fromJSON(playerJSON), GameState.fromJSON(gameStateJSON));
			} catch (JSONException e){
				
				socket.close();
				publishProgress();
				return null;
			}
			
			String str;
			while((str=br.readLine())!=null){
				if(str.equals(Constants.CLOSE_CONNECTION))break;
				try {
					GameState state = GameState.fromJSON(new JSONObject(str));
					publishProgress(state);
				} catch (JSONException e) {
					 e.printStackTrace();
				}
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	protected void onProgressUpdate(GameState... params){
		if(params.length>0)
			ma.updateGameState(params[0]);
		else
			ma.updateGameState(null);
	}
	public void closeConnection(){
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendEvent(Event event){
		try {
			pw.println(event.toJSON().toString());
		} catch (JSONException e) {
//			e.printStackTrace();
		}
	}
	protected void onPostExecute(Void result){
		if(ma.screenNo!=MainActivity.GAME_OVER_SCREEN)
		ma.reset();
	}
}
