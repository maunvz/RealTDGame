package com.mau.tdserver;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mau.tdgame.models.Player;

public class PlayerOptionsPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	Player player;
	JLabel playerLabel;
	ServerMain main;
	public PlayerOptionsPanel(ServerMain main){
		super();
		this.main=main;
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		
		JButton kickButton = new JButton("Kick");
		JButton killButton = new JButton("Kill");
		JButton respawnButton = new JButton("Respawn");
		JButton givePowerButton = new JButton("Power");
		JButton sendMessageButton = new JButton("Send");
		JButton setSensitivityButton = new JButton("Sens");
		
		buttonPanel.add(kickButton);
		buttonPanel.add(killButton);
		buttonPanel.add(respawnButton);
		buttonPanel.add(givePowerButton);
		buttonPanel.add(sendMessageButton);
		buttonPanel.add(setSensitivityButton);
		
		playerLabel = new JLabel();
		add(buttonPanel);
		add(playerLabel);
	}
	public void setPlayer(Player player){
		this.player = player;
		updatePlayerInfo();
	}
	public void updatePlayerInfo(){
		String text = player.getName()+"\n";
		playerLabel.setText(text);
	}
}
