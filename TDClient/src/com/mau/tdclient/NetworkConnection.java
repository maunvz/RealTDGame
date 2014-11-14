package com.mau.tdclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.os.AsyncTask;
import android.util.Log;

public class NetworkConnection extends AsyncTask<Void, String, Void>{
	private Socket socket;
	private String host;
	private String username;
	private PrintWriter pw;
	private BufferedReader br;
	private MainActivity ma;
	private int teamNo;
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
			
			String str;
			while((str=br.readLine())!=null){
				Log.d("Server Response", str);
				this.publishProgress(str);
				if(str.equals("bye")){
					break;
				}
				pw.println(ma.getUserInput());
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
