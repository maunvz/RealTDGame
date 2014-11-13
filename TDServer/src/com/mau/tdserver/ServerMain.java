package com.mau.tdserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ServerMain {
	ServerSocket socket;
	int port;
	JTextArea textArea;
	public ServerMain(){
		port = 1726;
		createUI().setVisible(true);
	}
	public void startListening(){
		Thread listenThread = new Thread(){
			public void run(){
				try {
					socket = new ServerSocket(port);
					while(true){
						Socket client = socket.accept();
						ClientThread thread = new ClientThread(client, ServerMain.this);
						thread.execute();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		};
		listenThread.start();
	}
	public JFrame createUI(){
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		textArea = new JTextArea(20,40);
		textArea.setEditable(false);
		
		JButton start_button = new JButton("Start Listening");
		start_button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				startListening();
				System.out.println("listening");
			}
		});
		panel.add(start_button);
		panel.add(textArea);
		frame.setContentPane(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;
	}
	public void print(String str){
		textArea.append(str+"\n");
	}
	public static void main(String[] args) {
		new ServerMain();
	}
}
