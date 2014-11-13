package com.mau.tdserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
	ServerSocket socket;
	int port;
	public ServerMain(){
		port = 1726;
		try {
			socket = new ServerSocket(port);
			while(true){
				Socket client = socket.accept();
				ClientThread thread = new ClientThread(client);
				System.out.println("client connected");
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new ServerMain();
	}
}
