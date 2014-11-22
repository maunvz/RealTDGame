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
	JLabel playerLabel;
	PlayerList playerList;
	ServerMain main;
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
				main.kickPlayer(playerList.getSelectedPlayer());
			}
		});
		JButton killButton = new JButton("Kill");
		killButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {	
				main.killPlayer(playerList.getSelectedPlayer());
			}
		});
		JButton respawnButton = new JButton("Respawn");
		respawnButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {	
				main.respawnPlayer(playerList.getSelectedPlayer());
			}
		});
		JButton givePowerButton = new JButton("Power");
		givePowerButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				int pow = Integer.parseInt(JOptionPane.showInputDialog("What player sensitivity?"));
				main.powerPlayer(playerList.getSelectedPlayer(), pow);
			}
		});
		JButton sendMessageButton = new JButton("Message");
		sendMessageButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {	
				String message = JOptionPane.showInputDialog("What message to send?");
				main.messagePlayer(playerList.getSelectedPlayer(), message);
			}
		});
		JButton setSensitivityButton = new JButton("Sensitivity");
		setSensitivityButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {	
				float sens = Float.parseFloat(JOptionPane.showInputDialog("What player sensitivity?"));
				main.setPlayerSensitivity(playerList.getSelectedPlayer(), sens);
			}
		});
		buttonPanel.add(kickButton);
		buttonPanel.add(killButton);
		buttonPanel.add(respawnButton);
		buttonPanel.add(givePowerButton);
		buttonPanel.add(sendMessageButton);
		buttonPanel.add(setSensitivityButton);
		
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
}
