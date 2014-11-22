package com.mau.tdclient;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
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
	private ShakeListener listener;
	private MediaPlayer mplayer;
	private NetworkConnection nc;
	public int screenNo;
	
	//Game state variables
	private String QRId;
	private boolean gameStarted;
	private Player player;
	private GameState gameState;
	
	//Called when the phone shakes too much
	public void youDie(){
		nc.sendEvent(new Event(Event.DIED, player.getName(), null));
	}
	//Called when the server sends an updated GameState
	public void updateGameState(GameState newGameState){
		//Before game starts GameState management
		if(newGameState==null){
			alertUser("Error","That username is taken for this server.");return;
		}
		gameState = newGameState;
		if(screenNo==GAME_SCREEN&&!newGameState.gameStarted()){//game ended
			reset(); return;
		}
		if(!gameState.gameStarted()){
			updateWaitRoom();return;
		}
		if(!gameStarted){
			startGame();return;
		}
		//During game GameState management
		if(screenNo==GAME_SCREEN){
			if(!gameState.getMessage().equals("")){
				((TextView)findViewById(R.id.server_message_textview)).append(gameState.getMessage()+"\n");
				((ScrollView)findViewById(R.id.message_scroll)).fullScroll(View.FOCUS_DOWN);
			}
			updateUIGameState();
		}
	}
	public void updateUIGameState(){
		Player tplayer = gameState.getPlayerByName(player.getName());
		if(!tplayer.alive){
			listener.turnOffListener();
			if(gameState.getMessage().contains("killed "+player.getName())){
				if(player.hasFlag){
					//Message saying you got killed by x, x has flag now
					((TextView)findViewById(R.id.status_message_textview)).setText(gameState.getMessage()+" Go respawn.");		
				}
				else{
					//Message saying you got killed by x
					((TextView)findViewById(R.id.status_message_textview)).setText(gameState.getMessage()+" Go respawn.");
				}
				mplayer.start();
			}
			else if(player.hasFlag&&gameState.getMessage().contains("died")&&gameState.getMessage().contains(player.getName())){
				//Message saying flag was returned to opponent base		
				((TextView)findViewById(R.id.status_message_textview)).setText("You died - flag is back at opponent base."+" Go respawn.");	
			}
			else if(gameState.getMessage().contains("died")&&gameState.getMessage().contains(player.getName())){
				//Message saying you died, go respawn
				((TextView)findViewById(R.id.status_message_textview)).setText("You died."+" Go respawn.");
				mplayer.start();
			}
		}
		else if(tplayer.alive){
			listener.turnOnListener();
			if(gameState.getMessage().contains(player.getName()+" killed")){
				//Message saying you killed x
				((TextView)findViewById(R.id.status_message_textview)).setText(gameState.getMessage());	
			}
			if(gameState.getMessage().contains(player.getName()+" just scored! Flag returns to base.")){
				//Message saying you scored the flag
				((TextView)findViewById(R.id.status_message_textview)).setText(gameState.getMessage());	
			}
			if(gameState.getMessage().contains(player.getName()+" has the flag!")){
				//Message saying you got the flag, go score it
				((TextView)findViewById(R.id.status_message_textview)).setText("You have the flag");	
			}
			if(gameState.getMessage().contains(player.getName()+" has respawned")){
				//Message saying congrats, you respawned
				((TextView)findViewById(R.id.status_message_textview)).setText("Congrats, you respawned.");
			}
		}
		player = tplayer;
		updateScoreDisplay();
	}
	public void updateScoreDisplay(){
		if(screenNo==GAME_SCREEN){
			((TextView)findViewById(R.id.score_text_team1)).setText(""+gameState.getTeamScores()[0]);
			((TextView)findViewById(R.id.score_text_team2)).setText(""+gameState.getTeamScores()[1]);
			((TextView)findViewById(R.id.score_text_player)).setText(""+player.score);
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
	public synchronized void setQRId(final String id){
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				QRId = id;
				TextView qrid = ((TextView)findViewById(R.id.qr_id_textview));
				if(qrid!=null)qrid.setText("QR Scanned. You may connect.");
			}			
		});
	}
	public void setScanEnabled(boolean enabled){
		Button scanButton = ((Button)findViewById(R.id.scan_button));
		if(scanButton==null)return;
		scanButton.setEnabled(enabled);
	}
	public synchronized String getQRId(){
		return QRId;
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
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		JoinGameFragment jgFrag = new JoinGameFragment(this);
		getFragmentManager().beginTransaction().add(R.id.fragment_holder, jgFrag).commit();
		gameStarted=false;
		screenNo=JOIN_SCREEN;
	}
	public void foundServer(String ip){
		if(screenNo==JOIN_SCREEN){
			((EditText)findViewById(R.id.ip_edit_text)).setText(ip);
		}
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
        if(username.equals("")||QRId==null||ip.equals("")){
        	alertUser("Error","Make sure you have an IP, username, and QR id.");
        	return;
        }
        nc = new NetworkConnection(ip, username, getQRId(), teamNo, MainActivity.this);
        nc.execute();
        System.out.println("Connecting...");
	}
	public void onScanClicked(View view){
		new ServerScanner(this).execute();
		setScanEnabled(false);
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
		listener = new ShakeListener(this);
		mSensorManager.registerListener(listener, mSensor, SensorManager.SENSOR_DELAY_GAME);
		gameStarted=true;
		getFragmentManager().beginTransaction().replace(R.id.fragment_holder, new GameFragment(this)).commit();
	}
	public void reset(){
		getFragmentManager().beginTransaction().replace(R.id.fragment_holder, new JoinGameFragment(this)).commit();
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
	public Player getPlayer(){
		return player;
	}
	public GameState getGameState(){
		return gameState;
	}
	public NetworkConnection getNC(){
		return nc;
	}
}
