package com.mau.tdclient.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.os.AsyncTask;
import android.util.Log;

import com.mau.tdclient.MainActivity;

public class NetworkConnection extends AsyncTask<Void, String, Void>{
	private Socket socket;
	private String host;
	private String username;
	private PrintWriter pw;
	private BufferedReader br;
	private MainActivity ma;
	
	public NetworkConnection(String host, String username, MainActivity ma){
		super();
		this.host = host;
		this.ma=ma;
		this.username = username;
	}
	@Override
	protected Void doInBackground(Void... params) {
		try {
			socket = new Socket();
			SocketAddress address = new InetSocketAddress(host, 1726);
			Log.d("Network Connection", "Attempting to connect to server.");
			socket.connect(address);
			Log.d("Network Connection", "Connected to server.");
			
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(socket.getOutputStream(), true);
			
			pw.println(username);

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
//separate threads for sending and receiving
