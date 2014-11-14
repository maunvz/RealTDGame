package com.mau.tdserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.SwingWorker;

import com.mau.tdgame.models.Player;

public class ClientThread extends SwingWorker<Void, Integer>{
	private Socket socket;
	private PrintWriter pw;
	private BufferedReader br;
	private Player player;
	private ServerMain main;
	public ClientThread(Socket socket, ServerMain main){
		this.socket = socket;
		this.main = main;
	}
	@Override
	protected Void doInBackground() throws Exception {
		try {
			pw = new PrintWriter(socket.getOutputStream(), true);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			//Create the player
			String username = br.readLine();
			int team = Integer.parseInt(br.readLine());
			player = new Player(username, team);
			main.print(player.getName()+" has joined team "+player.getTeam());

			//Send the player to the client
			pw.println(player.toJSON().toString());
			
			//Listen for events
			String str;
			while((str=br.readLine())!=null){
				main.print(username+": "+str);
				if(str.equals("bye")){
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
//separate threads for sending and receiving
