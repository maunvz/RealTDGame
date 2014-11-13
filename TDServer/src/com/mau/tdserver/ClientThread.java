package com.mau.tdserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread{
	private Socket socket;
	private PrintWriter pw;
	private BufferedReader br;
	
	public ClientThread(Socket socket){
		this.socket = socket;
	}
	public void run(){
		try {
			pw = new PrintWriter(socket.getOutputStream(), true);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String str;
			while((str=br.readLine())!=null){
				System.out.println(str);
				if(str.equals("bye")){
					break;
				}
				pw.println(str+" from server");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
