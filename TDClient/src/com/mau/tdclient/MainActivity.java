package com.mau.tdclient;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mau.tdgame.models.GameState;
import com.mau.tdgame.models.Player;
import com.mau.tdgame.models.Team;

public class MainActivity extends ActionBarActivity {
	public static final int JOIN_SCREEN=0;
	public static final int WAIT_SCREEN=1;
	public static final int GAME_SCREEN=2;
	
	//Sensor Stuff
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private SensorEventListener listener;

	private MediaPlayer mplayer;
	private boolean gameStarted;
	
	private Player player;
	private GameState gameState;
	public int screenNo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		JoinGameFragment jgFrag = new JoinGameFragment();
		getFragmentManager().beginTransaction().add(R.id.fragment_holder, jgFrag).commit();
		gameStarted=false;
		screenNo=JOIN_SCREEN;
	}
	public void onConnectClicked(View view){
		//Hide keyboard
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        //Opens a connection based on the input
		String ip = ((EditText)findViewById(R.id.ip_edit_text)).getEditableText().toString();
        String username = ((EditText)findViewById(R.id.username_edit_text)).getEditableText().toString();
        int selectedId = ((RadioGroup)findViewById(R.id.team_radio_group)).getCheckedRadioButtonId();
        int teamNo = selectedId==R.id.team1_button?Team.TEAM_1:Team.TEAM_2;
        new NetworkConnection(ip, username, teamNo, MainActivity.this).execute();
	}
	public void prepareAudio(){
	    try {
			AssetFileDescriptor afd = getAssets().openFd("buzz.mp3");
			mplayer = new MediaPlayer();
			mplayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
			mplayer.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void joinGame(Player player, GameState gameState){
		this.player = player;
		this.gameState = gameState;
		getFragmentManager().beginTransaction().replace(R.id.fragment_holder, new WaitingRoomFragment(this)).commit();
	}
	public void startGame(){
		prepareAudio();
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		listener = new ShakeListener(this, player.getSensitivity());
		mSensorManager.registerListener(listener, mSensor, SensorManager.SENSOR_DELAY_GAME);
		gameStarted=true;
	}
	public void youDie(){
		mplayer.start();
	}
	public void onPause(){
		super.onPause();
		if(gameStarted)
			mSensorManager.unregisterListener(listener);
	}
	public void onResume(){
		super.onResume();
		if(gameStarted)
			mSensorManager.registerListener(listener, mSensor, SensorManager.SENSOR_DELAY_GAME);
	}
	public void onDestroy(){
		super.onDestroy();
		if(gameStarted)mplayer.release();
	}
	public void updateServer(String str){
		((TextView)findViewById(R.id.server_state_text)).setText("Server Response\n"+str);
	}
	public void updateGameState(GameState newGameState){
		this.gameState = newGameState;
		if(!gameState.gameStarted()){
			updateWaitRoom();
		} else {
			//update other stuff
		}
	}
	public void updateWaitRoom(){
		if(screenNo!=WAIT_SCREEN)return;
		String team1="";
		String team2="";
		String[][] playerList = gameState.listPlayers();
		for(int i=0; i<playerList[0].length; i++){
			team1+=(i+1)+". "+playerList[0][i]+"\n";
		}
		for(int i=0; i<playerList[1].length; i++){
			team2+=(i+1)+". "+playerList[1][i]+"\n";
		}
		((TextView)findViewById(R.id.team1_list)).setText(team1);
		((TextView)findViewById(R.id.team2_list)).setText(team2);
	}
}
