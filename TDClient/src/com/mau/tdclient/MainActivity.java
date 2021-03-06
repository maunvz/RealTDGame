package com.mau.tdclient;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mau.tdgame.models.Constants;
import com.mau.tdgame.models.Event;
import com.mau.tdgame.models.GameState;
import com.mau.tdgame.models.Player;
import com.mau.tdgame.models.Team;

public class MainActivity extends ActionBarActivity {
	public static final int JOIN_SCREEN=0;
	public static final int WAIT_SCREEN=1;
	public static final int GAME_SCREEN=2;
	public static final int GAME_OVER_SCREEN=3;
	public static final int HOME_SCREEN=4;
	public static final int CREATE_GAME_SCREEN=5;
	public static final int GAME_LIST_SCREEN=6;
	public static final int ABOUT_SCREEN=7;
	
	//Fragments
	private CreateGameFragment createGameFrag;
	private GameEndedFragment gameEndedFrag;
	private HomeScreenFragment homeScreenFrag;
	private GameFragment gameFrag;
	private WaitingRoomFragment waitingRoomFrag;
	private GameListFragment gameListFrag;
	private JoinGameFragment joinGameFrag;
	private AboutScreenFragment aboutScreenFrag;
	
	//Technical stuff
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private ShakeListener listener;
	private MediaPlayer mplayer;
	private NetworkConnection nc;
	private int port;
	public static int screenNo;
	public static boolean destroyed;
	//Game state variables
	private String QRId;
	private boolean gameStarted;
	private Player player;
	private GameState gameState;
	
	//User Interface
	Button[] buttons;
	
	//Called when the phone shakes too much
	public void youDie(){
		if(nc==null)return;
		nc.sendEvent(new Event(Event.DIED, player.getName(), null, 0));
	}
	//Called when the server sends an updated GameState
	public void updateGameState(GameState newGameState){
		//Before game starts GameState management
		if(newGameState==null){
			alertUser("Error","That username or QR is taken for this server.");return;
		}
		gameState = newGameState;
		if(screenNo==GAME_SCREEN&&!newGameState.gameStarted()){
			endGame();
			return;
		}
		if(!gameState.gameStarted()){
			updateWaitRoom();return;
		}
		if(!gameStarted){
			startGame();return;
		}
		//During game GameState management
		if(screenNo==GAME_SCREEN){
			updateUIGameState();
			//Server messages
			if(!gameState.playerMessage.equals("")){
				String playerName = gameState.playerMessage.split("`")[0];
				if(playerName.equals(player.getName())&&gameState.playerMessage.split("`").length==2){
					Toast.makeText(this, gameState.playerMessage.split("`")[1], Toast.LENGTH_SHORT).show();				
				}
			} 
			if(!gameState.globalMessage.equals("")){
				if(!gameState.globalMessage.contains(getPlayer().getName()))
					Toast.makeText(this, gameState.globalMessage, Toast.LENGTH_SHORT).show();
			}
		}
	}
	public void animateStatus(final TextView tv){
		Animation anim_out = AnimationUtils.loadAnimation(this, R.anim.fade_out_long);
		tv.startAnimation(anim_out);
		anim_out.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				tv.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}
			
		});
	}
	public void updateUIGameState(){
		Player tplayer = gameState.getPlayerByName(player.getName());
		((TextView)findViewById(R.id.status_message_textview)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LCD Display Grid.ttf"));
		if(!tplayer.alive){
			listener.turnOffListener();
			if(gameState.getMessage().contains("killed "+player.getName())){
					//Message saying you got killed by x
				final TextView tvToChange = ((TextView)findViewById(R.id.status_message_textview));
				tvToChange.setVisibility(View.VISIBLE);
				String actualMessage = gameState.getMessage().replaceAll(tplayer.getName(), "you");
				actualMessage = actualMessage.substring(0,1).toUpperCase() + actualMessage.substring(1).toLowerCase();
				tvToChange.setText(actualMessage+" Go respawn.");
				mplayer.start();
				vibrate();
				animateStatus(tvToChange);
			}
			else if(gameState.getMessage().contains("died")&&gameState.getMessage().contains(player.getName())){
				//Message saying you died, go respawn
				final TextView tvToChange = ((TextView)findViewById(R.id.status_message_textview));
				tvToChange.setVisibility(View.VISIBLE);
				tvToChange.setText("You died."+" Go respawn.");
				mplayer.start();
				vibrate();
				animateStatus(tvToChange);
			}
		}
		else if(tplayer.alive){
			listener.turnOnListener();
			if(gameState.getMessage().contains(player.getName()+" killed")){
				final TextView tvToChange = ((TextView)findViewById(R.id.status_message_textview));
				tvToChange.setVisibility(View.VISIBLE);
				String actualMessage = gameState.getMessage().replaceAll(tplayer.getName(), "you");
				actualMessage = actualMessage.substring(0,1).toUpperCase() + actualMessage.substring(1).toLowerCase();
				tvToChange.setText(actualMessage);	
				animateStatus(tvToChange);
			}
			if(gameState.getMessage().contains(player.getName()+" just scored! Flag returns to base.")){
				//Message saying you scored the flag
				final TextView tvToChange = ((TextView)findViewById(R.id.status_message_textview));
				tvToChange.setVisibility(View.VISIBLE);
				String actualMessage = gameState.getMessage().replaceAll(tplayer.getName(), "you");
				actualMessage = actualMessage.substring(0,1).toUpperCase() + actualMessage.substring(1).toLowerCase();
				tvToChange.setText(actualMessage);	
				animateStatus(tvToChange);
			}
			if(gameState.getMessage().contains(player.getName()+" has the flag!")){
				//Message saying you got the flag, go score it
				final TextView tvToChange = ((TextView)findViewById(R.id.status_message_textview));
				tvToChange.setVisibility(View.VISIBLE);
				tvToChange.setText("You have the flag");
				animateStatus(tvToChange);
			}
			if(gameState.getMessage().contains(player.getName()+" has respawned")){
				//Message saying congrats, you respawned
				final TextView tvToChange = ((TextView)findViewById(R.id.status_message_textview));
				tvToChange.setVisibility(View.VISIBLE);
				tvToChange.setText("You have respawned.");
				animateStatus(tvToChange);
			}
		}
		player = tplayer;
		updateStateDisplay();
		updatePowerupButtons();
	}
	public void updatePowerupButtons(){
//		((TextView)findViewById(R.id.status_message_textview)).setText("Buttons");
		LinearLayout buttonLayout = (LinearLayout)findViewById(R.id.powerup_button_holder);
		buttonLayout.removeAllViews();
		boolean[] added = new boolean[6];
		for(Integer power:player.powerups){
			if(added[power])continue;
			added[power] = true;
			buttonLayout.addView(buttons[power]);
		}
	}
	public void updateStateDisplay(){
		if(screenNo!=GAME_SCREEN)return;
		if(getPlayer().getTeam() == Team.TEAM_1)
			findViewById(R.id.teamIdentifier).setBackgroundResource(R.drawable.scoreboard_red);
		if(getPlayer().getTeam() == Team.TEAM_2)
			findViewById(R.id.teamIdentifier).setBackgroundResource(R.drawable.scoreboard_blue);
		float min = 0.2f;
		float team1_ratio = ((gameState.getTeamScores()[0]+0.001f)/(gameState.getTeamScores()[1]+0.001f))/2.0f;
		if(team1_ratio < min){
			team1_ratio = min;
		}
		else if(team1_ratio > 1.0f-min){
			team1_ratio = 1.0f-min;
		}
		RelativeLayout team1_layout = ((RelativeLayout)findViewById(R.id.team1_holder));
		RelativeLayout team2_layout = ((RelativeLayout)findViewById(R.id.team2_holder));
		ResizeAnimation team1_animation = new ResizeAnimation(team1_layout,((LinearLayout.LayoutParams)team1_layout.getLayoutParams()).weight,team1_ratio);
		ResizeAnimation  team2_animation = new ResizeAnimation(team2_layout,((LinearLayout.LayoutParams)team2_layout.getLayoutParams()).weight,1.0f-team1_ratio);
		team1_layout.startAnimation(team1_animation);
		team2_layout.startAnimation(team2_animation);
//		team1_layout.setLayoutParams(new LinearLayout.LayoutParams(0,
//				LayoutParams.MATCH_PARENT,team1_ratio));
//		team2_layout.setLayoutParams(new LinearLayout.LayoutParams(0,
//				LayoutParams.MATCH_PARENT,1.0f-team1_ratio));
		String team1_text="";
		String team2_text="";
		((TextView)findViewById(R.id.team1_state)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LCD Display Grid.ttf"));
		((TextView)findViewById(R.id.team2_state)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LCD Display Grid.ttf"));
		team1_text+=gameState.getTeamScores()[0];
		team2_text+=gameState.getTeamScores()[1];
//		team1_text+="Flag: "+(gameState.playerWithFlag1.equals("")?"At Base":gameState.playerWithFlag1)+"\n";
//		team2_text+="Flag: "+(gameState.playerWithFlag2.equals("")?"At Base":gameState.playerWithFlag2)+"\n";

		((TextView)findViewById(R.id.team1_state)).setText(team1_text);
		((TextView)findViewById(R.id.team2_state)).setText(team2_text);
		ImageView redFlag = (ImageView)findViewById(R.id.teamHasFlagRed);
		ImageView blueFlag = (ImageView)findViewById(R.id.teamHasFlagBlue);
		((TextView)findViewById(R.id.player_who_has_red_blue)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LCD Display Grid.ttf"));
		((TextView)findViewById(R.id.player_who_has_red_flag)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LCD Display Grid.ttf"));

		if(!getGameState().playerWithFlag1.equals("")){
			RelativeLayout.LayoutParams rl1 = (RelativeLayout.LayoutParams)((ImageView)findViewById(R.id.person_has_flag_blue)).getLayoutParams();
			rl1.width = redFlag.getHeight();
			((ImageView)findViewById(R.id.person_has_flag_blue)).setLayoutParams(rl1);
			redFlag.setVisibility(View.GONE);
			((TextView)findViewById(R.id.player_who_has_red_blue)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.colonRight)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.player_who_has_red_blue)).setText(gameState.playerWithFlag1);
			((ImageView)findViewById(R.id.person_has_flag_blue)).setVisibility(View.VISIBLE);
		}
		else if(getGameState().playerWithFlag1.equals("")){
			redFlag.setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.player_who_has_red_blue)).setVisibility(View.GONE);
			((TextView)findViewById(R.id.colonRight)).setVisibility(View.GONE);
			((ImageView)findViewById(R.id.person_has_flag_blue)).setVisibility(View.GONE);
			
		}
		if(!getGameState().playerWithFlag2.equals("")){
			RelativeLayout.LayoutParams rl2 = (RelativeLayout.LayoutParams)((ImageView)findViewById(R.id.person_has_flag_red)).getLayoutParams();
			rl2.width = blueFlag.getHeight();
			((ImageView)findViewById(R.id.person_has_flag_red)).setLayoutParams(rl2);
			blueFlag.setVisibility(View.GONE);
			((TextView)findViewById(R.id.player_who_has_red_flag)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.colonLeft)).setVisibility(View.VISIBLE);
			((ImageView)findViewById(R.id.person_has_flag_red)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.player_who_has_red_flag)).setText(gameState.playerWithFlag2);
		}
		else if(getGameState().playerWithFlag2.equals("")){
			blueFlag.setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.player_who_has_red_flag)).setVisibility(View.GONE);
			((TextView)findViewById(R.id.colonLeft)).setVisibility(View.GONE);
			((ImageView)findViewById(R.id.person_has_flag_red)).setVisibility(View.GONE);
			
		}
//		((TextView)findViewById(R.id.player_who_has_red_blue)).setText("Flag: "+(gameState.playerWithFlag1.equals("")?"At Base":gameState.playerWithFlag1));
//		((TextView)findViewById(R.id.player_who_has_red_flag)).setText("Flag: "+(gameState.playerWithFlag2.equals("")?"At Base":gameState.playerWithFlag2));
		((TextView)findViewById(R.id.player_score_textview)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LCD Display Grid.ttf"));
		((TextView)findViewById(R.id.player_score_textview)).setText(""+player.score);
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
	public void updateGameOverScreen(){
		if(screenNo!=GAME_OVER_SCREEN){
			
		}
	}
	public void onStartGameClick(View v) {
		nc.sendEvent(new Event(Event.START_GAME, "", "", 0));
		startGame();
	}
	public synchronized void setQRId(final String id){
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				QRId = id;
				String playerNumber = id;
				TextView qrid = ((TextView)findViewById(R.id.qr_id_textview));
				if(qrid!=null&&playerNumber.contains("player")){
					qrid.setText("QR Scanned. You are player "+id.charAt(id.length()-1));
					playerNumber = playerNumber.replace("player_","");
					RadioButton team1button = (RadioButton)findViewById(R.id.team1_button);
					RadioButton team2button = (RadioButton)findViewById(R.id.team2_button);
					if(team1button==null||team2button==null)return;
					if(Integer.parseInt(playerNumber)>=8&&team1button.isChecked()){
						team1button.setChecked(false);
						team2button.setChecked(true);
					}
					else if(Integer.parseInt(playerNumber)<8&&team2button.isChecked()){
						team1button.setChecked(true);
						team2button.setChecked(false);
					}
				}
				else if(!playerNumber.contains("player")){

						if(qrid!=null)qrid.setText("That isn't a valid player id. Rescan.");
				}
			}
		});
	}
	public synchronized void makeAToast(final String message){
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				Toast.makeText(GameFragment.ma, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
	public synchronized String getQRId(){
		return QRId;
	}
	//Called when server adds player to game, move to waiting room
	public void joinGame(Player player, GameState gameState){
		this.player = player;
		this.gameState = gameState;
		
		transitionFragments(waitingRoomFrag);
	}
	//Technical stuff below here, manages UI, sensors, sound, networking, etc.
	//------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		

		homeScreenFrag = new HomeScreenFragment(this);
		createGameFrag = new CreateGameFragment(this);
		gameEndedFrag = new GameEndedFragment(this);
		gameFrag = new GameFragment(this);
		waitingRoomFrag = new WaitingRoomFragment(this);
		gameListFrag = new GameListFragment(this);
		joinGameFrag = new JoinGameFragment(this);
		aboutScreenFrag = new AboutScreenFragment(this);
		
		getFragmentManager().beginTransaction().add(R.id.fragment_holder, homeScreenFrag).commit();
		
		gameStarted=false;
		screenNo=HOME_SCREEN;
		destroyed=false;
		createButtons();
	}
	//when the home screen's join game button is pressed
	public void onJoinClicked(View view){
		transitionFragments(gameListFrag);
	}
	//when the home screen's create game button is pressed
	public void onCreateClicked(View view){
		transitionFragments(createGameFrag);
	}
	public void onAboutClicked(View view){
		transitionFragments(aboutScreenFrag);
	}
	//when the join screen's join button is pressed
	public void joinGame(int port){
		transitionFragments(joinGameFrag);
		this.port = port;
	}
	public void onConnectClicked(View view){
		//Hide keyboard
		try{
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		} catch (Exception e){
			e.printStackTrace();
		}
		//Opens a connection based on the input
		String ip = Constants.host;
        String username = ((EditText)findViewById(R.id.username_edit_text)).getEditableText().toString();
        int selectedId = ((RadioGroup)findViewById(R.id.team_radio_group)).getCheckedRadioButtonId();
        int teamNo = selectedId==R.id.team1_button?Team.TEAM_1:Team.TEAM_2;
        if(username.equals("")||QRId==null||ip.equals("")){
        	alertUser("Error","Make sure you have an IP, username, and QR id.");
        	return;
        }
        nc = new NetworkConnection(port, username, getQRId(), teamNo, MainActivity.this);
        nc.execute();
        System.out.println("Connecting...");
	}
	public void vibrate(){
		((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(200);
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
		try{
			nc.closeConnection(); nc=null;
			if(gameStarted)mplayer.release();
			destroyed=true;
		}catch(Exception e){}
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
		if(gameFrag == null)gameFrag = new GameFragment(this);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		listener = new ShakeListener(this);
		mSensorManager.registerListener(listener, mSensor, SensorManager.SENSOR_DELAY_GAME);
		gameStarted=true;
		transitionFragments(gameFrag);
	}
	public void reset(){
		if(destroyed)return;
		transitionFragments(homeScreenFrag);
//		GameFragment.resetAllStatic();
//		ShakeListener.resetAllStatic();
//		CameraPreviewT.resetAllStatic();
//		CameraUtils.resetAllStatic();
//		ZBarScannerView.resetAllStatic();
		screenNo = HOME_SCREEN;
		port = -1;
		System.out.println("Everything Gone.");
		destroyed = true;
		gameFrag = null;
		player = null;
		gameState = null;
		listener.turnOffListener();
//		System.out.println("This thing destroyed yet? "+getFragmentManager().isDestroyed());
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
	public void createButtons(){
		buttons = new Button[4];
		for(int i=0; i<buttons.length; i++){
			final int p = i;
			buttons[i] = new Button(this);
			buttons[i].setText(Player.POWER_UP_NAMES[i]);
			buttons[i].setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					nc.sendEvent(new Event(Event.POWERUP_USED, player.getName(),"",p));
				}
			});
		}
	}
	public void endGame(){
		((TextView)findViewById(R.id.status_message_textview)).setVisibility(View.GONE);
		transitionFragments(gameEndedFrag);
		gameEndedFrag.update();
		screenNo = GAME_OVER_SCREEN;
	}
	public void onDoneClicked(View view){
		reset();
	}
	public void transitionFragments(Fragment frag){
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		ft.replace(R.id.fragment_holder, frag);
		ft.addToBackStack("");
		ft.commit();
	}
	@Override
	public void onBackPressed() {
		if(screenNo==GAME_SCREEN)return;
		else if(screenNo==HOME_SCREEN){
			new AlertDialog.Builder(this)
		    .setTitle("Exit")
		    .setMessage("Are you sure you want to exit the game?")
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            finish();
		        }
		     })
		    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // do nothing
		        }
		     })
		    .setIcon(android.R.drawable.ic_dialog_alert)
		     .show();
		}
		else if (!getFragmentManager().popBackStackImmediate()) {
	        super.onBackPressed();
	    }
        System.out.println(screenNo);
	}
}
