package com.mau.tdclient;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.mau.tdgame.models.Event;
import com.mau.tdgame.models.GameState;
import com.mau.tdgame.models.Player;
import com.mau.tdgame.models.Team;

public class MainActivity extends ActionBarActivity {
	public static final int JOIN_SCREEN=0;
	public static final int WAIT_SCREEN=1;
	public static final int GAME_SCREEN=2;
	
	//Technical stuff
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private SensorEventListener listener;
	private MediaPlayer mplayer;
	private NetworkConnection nc;
	public int screenNo;
	
	//Game state variables
	private boolean gameStarted;
	private Player player;
	private GameState gameState;
	
	//Called when the phone shakes too much
	public void youDie(){
		mplayer.start();
		nc.sendEvent(new Event(Event.DIED, player.getName(), null, 0));
	}
	public void scannedQR(/*some parameter*/){	
		//send the QR data to the server, who will respond telling you if you killed someone
		//or captured a flag or came back to life, or got a power up, etc.
	}
	//Called when the server sends an updated GameState
	public void updateGameState(GameState newGameState){
		//Before game starts GameState management
		if(newGameState==null){
			alertUser("Error","That username is taken for this server.");return;
		}
		gameState = newGameState;
		if(!gameState.gameStarted()){
			updateWaitRoom();return;
		}
		if(!gameStarted){
			startGame();return;
		}
		//During game GameState management
		if(screenNo==GAME_SCREEN){
			((TextView)findViewById(R.id.server_message_textview)).append(gameState.getMessage()+"\n");
			//TODO - update UI based on server's response
		}
	}
	//called when a new player joins the waiting room adds all the names of the players in gameState to their respective lists
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
	//Called when server adds player to game, move to waiting room
	public void joinGame(Player player, GameState gameState){
		this.player = player;
		this.gameState = gameState;
		getFragmentManager().beginTransaction().replace(R.id.fragment_holder, new WaitingRoomFragment(this)).commit();
	}
	//Technical stuff below here, manages UI, sensors, sound, networking, etc.
	//------------------------------------------------------------------------
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
        nc = new NetworkConnection(ip, username, teamNo, MainActivity.this);
        nc.execute();
        System.out.println("Connecting...");
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
	public void startGame(){
		prepareAudio();
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		listener = new ShakeListener(this, player.getSensitivity());
		mSensorManager.registerListener(listener, mSensor, SensorManager.SENSOR_DELAY_GAME);
		gameStarted=true;
		getFragmentManager().beginTransaction().replace(R.id.fragment_holder, new GameFragment(this)).commit();
	}
	//creates a popup dialog box that tells the player message
	public void alertUser(String title, String message){
		new AlertDialog.Builder(this)
	    .setTitle(title)
	    .setMessage(message)
	    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {}
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	     .show();
	}
	//TEMPORARY
	public void respawn(View view){
		nc.sendEvent(new Event(Event.RESPAWNED, player.getName(), null, 0));
	}
	public void kill(View view){
		final EditText input = new EditText(this);
		getInput("Who to kill?", "Type name of player to kill.", input,
				new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int whichButton) {
			    		String playerToKill = input.getText().toString();
			    		nc.sendEvent(new Event(Event.KILLED, player.getName(), playerToKill, 0));
			    	}
			    });
	}
	public void capture(View view){
		nc.sendEvent(new Event(Event.CAPTURED_FLAG, player.getName(), null, 0));
	}
	public void score(View view){
		nc.sendEvent(new Event(Event.SCORED, player.getName(), null, 0));
	}
	public void effect(View view){
		final EditText input = new EditText(this);
		getInput("Effect", "Write the effect number.", input,
		new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	    		int no = 0;
	    		String effectNo = input.getText().toString();
	        	try{no=Integer.parseInt(effectNo);}catch(NumberFormatException e){};
	    		nc.sendEvent(new Event(Event.GOT_EFFECT, player.getName(), null, no));
	        }
	    });
	}
	public void getInput(String title, String message, EditText input, DialogInterface.OnClickListener listener){
		new AlertDialog.Builder(this)
	    .setTitle(title)
	    .setMessage(message)
	    .setView(input)
	    .setPositiveButton("Ok", listener).show();
	}
	public Player getPlayer(){
		return player;
	}
	public GameState getGameState(){
		return gameState;
	}
}
