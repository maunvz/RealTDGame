package com.mau.tdserver;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Console extends JPanel{
	private static final long serialVersionUID = 1L;
	JTextArea textArea;
	public Console(){
		super();
		textArea = new JTextArea(20,40);
		textArea.setEditable(false);
		JScrollPane pane = new JScrollPane(textArea);
		add(pane);
	}
	public synchronized void setSession(GameSession session){
		textArea.setText(session.consoleText.toString());
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}
