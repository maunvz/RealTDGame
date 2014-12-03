package com.mau.tdserver;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mau.tdgame.models.Player;

public class PlayerOptionsPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	Player player;
	PlayerList playerList;
	ServerMain main;
	GameSession session;
	JLabel playerLabel;
	public PlayerOptionsPanel(final ServerMain main){
		super();
		this.main=main;
		setLayout(new BorderLayout());
		
		playerList = new PlayerList(main, this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0,1));
		
		JButton kickButton = new JButton("Kick");
		kickButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {	
				if(session==null)return;
				session.kickPlayer(playerList.getSelectedPlayer());
			}
		});
		JButton killButton = new JButton("Kill");
		killButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {	
				if(session==null)return;
				session.killPlayer(playerList.getSelectedPlayer());
			}
		});
		JButton respawnButton = new JButton("Respawn");
		respawnButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {	
				if(session==null)return;
				session.respawnPlayer(playerList.getSelectedPlayer());
			}
		});
		JButton givePowerButton = new JButton("Power");
		givePowerButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(session==null)return;
				int pow = Integer.parseInt(JOptionPane.showInputDialog("What power to give?"));
				session.powerPlayer(playerList.getSelectedPlayer(), pow);
			}
		});
		JButton sendMessageButton = new JButton("Message");
		sendMessageButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {	
				if(session==null)return;
				String message = JOptionPane.showInputDialog("What message to send?");
				session.messagePlayer(playerList.getSelectedPlayer(), message);
			}
		});
		buttonPanel.add(kickButton);
		buttonPanel.add(killButton);
		buttonPanel.add(respawnButton);
		buttonPanel.add(givePowerButton);
		buttonPanel.add(sendMessageButton);
		
		playerLabel = new JLabel();
		playerLabel.setPreferredSize(new Dimension(100,100));
		
		add(playerList, BorderLayout.WEST);
		add(buttonPanel, BorderLayout.CENTER);
		add(playerLabel, BorderLayout.EAST);
	}
	public void setPlayer(Player player){
		this.player = player;
		updatePlayerInfo();
	}
	public void updatePlayerInfo(){
		String text = player.getName()+"\n";
		playerLabel.setText(text);
	}
	public void setSession(GameSession session){
		this.session = session;
		playerList.setSession(session);
	}
}
