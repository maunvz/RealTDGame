package com.mau.tdserver;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class SessionOptionsPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	GameSession session;
	GameSessionList sessionList;
	ServerMain main;
	public SessionOptionsPanel(final ServerMain main){
		super();
		this.main=main;
		setLayout(new BorderLayout());
		
		sessionList = new GameSessionList(main);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0,1));
		
		JButton killButton = new JButton("Kill");
		killButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				main.killSelectedSession();
			}
		});
		buttonPanel.add(killButton);
		
		add(sessionList, BorderLayout.WEST);
		add(buttonPanel, BorderLayout.CENTER);
	}
}
