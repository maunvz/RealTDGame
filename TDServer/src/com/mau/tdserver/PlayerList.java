package com.mau.tdserver;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PlayerList extends JPanel{
	private static final long serialVersionUID = 1L;
	JList<String> team1List;
	DefaultListModel<String> lm1;
	ServerMain main;
	public PlayerList(ServerMain main){
		super();
		this.main=main;
		
		lm1 = new DefaultListModel<String>();
		
		team1List = new JList<String>();
		team1List.setModel(lm1);
		team1List.setVisibleRowCount(20);
		team1List.setFixedCellWidth(150);
		add(team1List);
		team1List.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {

			}
		});
		updateLists();
	}
	public void updateLists(){
		if(main.getGameState()==null)return;
		lm1.removeAllElements();
		String[][] players = main.getGameState().listPlayers();
		for(int i=0; i<players[0].length; i++){
			lm1.addElement(players[0][i]);
		}
		for(int i=0; i<players[1].length; i++){
			lm1.addElement(players[1][i]);
		}
	}
}
