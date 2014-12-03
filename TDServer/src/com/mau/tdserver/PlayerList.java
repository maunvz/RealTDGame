package com.mau.tdserver;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mau.tdgame.models.Player;

public class PlayerList extends JPanel{
	private static final long serialVersionUID = 1L;
	JList<String> team1List;
	DefaultListModel<String> lm1;
	ServerMain main;
	PlayerOptionsPanel pop;
	GameSession session;
	public PlayerList(ServerMain main, PlayerOptionsPanel pop){
		super();
		this.pop=pop;
		this.main=main;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEtchedBorder());
		lm1 = new DefaultListModel<String>();
		
		team1List = new JList<String>();
		team1List.setModel(lm1);
		team1List.setVisibleRowCount(20);
		team1List.setFixedCellWidth(150);
		add(team1List, BorderLayout.CENTER);
		team1List.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
			}
		});
		updateLists();
	}
	public void setSession(GameSession session){
		this.session = session;
		updateLists();
	}
	public void updateLists(){
		lm1.addElement(" ");
		if(session==null)return;
		if(session.getGameState()==null)return;
		lm1.removeAllElements();
		String[][] players = session.getGameState().listPlayers();
		for(int i=0; i<players[0].length; i++){
			lm1.addElement(players[0][i]);
		}
		for(int i=0; i<players[1].length; i++){
			lm1.addElement(players[1][i]);
		}
		if(lm1.isEmpty())lm1.addElement(" ");
	}
	public Player getSelectedPlayer(){
		return session.getGameState().getPlayerByName(team1List.getSelectedValue());
	}
}
