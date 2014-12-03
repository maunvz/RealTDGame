package com.mau.tdserver;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GameSessionList extends JPanel{
	private static final long serialVersionUID = 1L;
	JList<String> sessionList;
	DefaultListModel<String> lm1;
	ServerMain main;
	public GameSessionList(final ServerMain main){
		this.main = main;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEtchedBorder());
		lm1 = new DefaultListModel<String>();
		sessionList = new JList<String>();
		sessionList.setModel(lm1);
		sessionList.setVisibleRowCount(20);
		sessionList.setFixedCellWidth(150);
		add(sessionList, BorderLayout.CENTER);
		sessionList.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				main.setSelectedSession(sessionList.getSelectedIndex());
			}
		});
		updateLists();
	}
	public void updateLists(){
		lm1.removeAllElements();
		String[] sessions = main.getSessionNames();
		for(int i=0; i<sessions.length; i++){
			lm1.addElement(sessions[i]);
		}
		if(lm1.isEmpty())lm1.addElement(" ");
	}
}
