package com.mau.tdclient;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mau.tdgame.models.Player;
import com.mau.tdgame.models.Team;

public class GameEndedFragment extends Fragment{
	MainActivity ma;
	TextView whoWonTextView;
	TextView team1TextView;
	TextView team2TextView;
	
	public GameEndedFragment(MainActivity ma){
		super();
		this.ma=ma;
	}
	public void onPause(){
		super.onPause();
	}
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	public void onResume(){
		super.onResume();
		MainActivity.screenNo=MainActivity.GAME_OVER_SCREEN;
		ma.updateGameOverScreen();
	}
	public void update(){
		if(whoWonTextView==null)return;
		int[] scores = ma.getGameState().getTeamScores();
		if(scores[0]>scores[1]){
			whoWonTextView.setText("Red Team Wins!");
		} else {
			whoWonTextView.setText("Blue Team Wins!");			
		}
		ArrayList<Player> players =	ma.getGameState().getPlayers();
		String team1Scores = "";
		String team2Scores = "";
		for(int i=0; i<players.size(); i++){
			if(players.get(i).getTeam()==Team.TEAM_1){
				team1Scores += players.get(i).getName()+" "+players.get(i).score +" \n";
			} else {
				team2Scores += players.get(i).getName()+" "+players.get(i).score +" \n";
			}
		}
		team1TextView.setText(team1Scores);
		team2TextView.setText(team2Scores);
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.game_ended_fragment, container, false);
        whoWonTextView = (TextView) view.findViewById(R.id.who_won_textview);
        team1TextView = (TextView) view.findViewById(R.id.red_team_scores_textview);
        team2TextView = (TextView) view.findViewById(R.id.blue_team_scores_textview);
        try{
        	update();
        }catch(Exception e){}
        return view;
	}
}
