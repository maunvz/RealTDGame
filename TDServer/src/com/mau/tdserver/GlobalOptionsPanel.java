package com.mau.tdserver;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.mau.tdgame.models.GameState;

public class GlobalOptionsPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	JTextField maxScoreField;
	JSpinner sensitivitySpinner;
	JButton startGameButton;
	JButton endGameButton;
	ServerMain main;
	public GlobalOptionsPanel(ServerMain main){
		super();
		this.main=main;
		setLayout(new GridLayout(0,2));
		
		JLabel ssLabel = new JLabel("Sensitivity");
		sensitivitySpinner = new JSpinner();
		sensitivitySpinner.setModel(new SpinnerNumberModel(GameState.DEFAULT_SENSITIVITY,200,1000,50));
		sensitivitySpinner.setEditor(new JSpinner.NumberEditor(sensitivitySpinner,"###"));
		
		JLabel msLabel = new JLabel("Max Score");
		maxScoreField = new JTextField(4);
		maxScoreField.setText(""+GameState.DEFAULT_MAX_SCORE);
		
		startGameButton = new JButton("Start Game");
		startGameButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				 GlobalOptionsPanel.this.main.startGame();
			}
		});
		endGameButton = new JButton("End Game");
		
		add(ssLabel);
		add(sensitivitySpinner);
		add(msLabel);
		add(maxScoreField);
		add(startGameButton);
		add(endGameButton);
	}
}
