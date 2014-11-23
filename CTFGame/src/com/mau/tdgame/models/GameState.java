package com.mau.tdgame.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameState {
	public static final String BASE_1_QR = "base_0";
	public static final String BASE_2_QR = "base_1";
	
	public static final int SCORE_VALUE=10;
	public static final int RETURN_VALUE=5;
	public static final int KILL_VALUE=2;
	public static final int KILL_PENALTY=1;
	public static final int DEFAULT_SENSITIVITY = 650;
	public static final int DEFAULT_MAX_SCORE = 100;
	
	private boolean gameStarted;
	private ArrayList<Player> players;
	private String message;
	public int maxScore;
	public String globalMessage;
	public String playerMessage;
	public int gameSensitivity;
	public Team team1;
	public Team team2;
	public String playerWithFlag1;
	public String playerWithFlag2;
	
	public GameState(){
		team1 = new Team(Team.TEAM_1);
		team2 = new Team(Team.TEAM_2);
		players = new ArrayList<Player>();
		gameStarted = false;
		message="";
		globalMessage="";
		playerMessage="";
		playerWithFlag1="";
		playerWithFlag2="";
		maxScore = DEFAULT_MAX_SCORE;
		gameSensitivity=DEFAULT_SENSITIVITY;
	}
	public boolean gameStarted(){
		return gameStarted;
	}
	public void startGame(){
		gameStarted=true;
	}
	public void endGame(){
		gameStarted=false;
	}
	public void processEvent(Event event){
		Player player1 = getPlayerByName(event.player1);
		message = "";
		switch(event.getType()){
		case Event.DIED:
			playerDies(player1);
			break;
		case Event.QR_EVENT:
			if(event.value1.equals(BASE_1_QR)){
				if(player1.getTeam()==Team.TEAM_1) playerReachedOwnBase(player1);
				else playerReachedEnemyBase(player1);
			}else if(event.value1.equals(BASE_2_QR)){
				if(player1.getTeam()==Team.TEAM_2) playerReachedOwnBase(player1);
				else playerReachedEnemyBase(player1);
			}else {
				if(killPlayer(player1, getPlayerByQRId(event.value1)))return;
			}
			break;
		}
		if(getTeamScores()[0]>=maxScore||getTeamScores()[1]>=maxScore){
			endGame();
		}
	}
	public void playerReachedOwnBase(Player player){
		if(playerRespawns(player))return;
		if(scoreFlag(player))return;
	}
	public void playerReachedEnemyBase(Player player){
		if(captureFlag(player))return;
	}
	public boolean playerRespawns(Player player){
		if(player.alive)return false;
		player.alive=true;
		message = player.getName()+" has respawned";
		return true;
	}
	public boolean playerDies(Player player){
		if(player==null)return false;
		if(!player.alive)return false;
		player.alive=false;
		message = player.getName() + " died.";
		if(playerWithFlag1==player.getName())playerWithFlag1="";
		if(playerWithFlag2==player.getName())playerWithFlag2="";
		return true;
	}
	public boolean killPlayer(Player killer, Player victim){
		if(killer==null||victim==null)return false;
		if(killer.getTeam()==victim.getTeam())return false;
		if(!killer.alive)return false;
		if(victim.getName()==playerWithFlag1)playerWithFlag1=killer.getName();
		if(victim.getName()==playerWithFlag2)playerWithFlag2=killer.getName();
		if(!playerDies(victim))return false;
		killer.score+=KILL_VALUE;
		victim.score-=KILL_PENALTY;
		message = killer.getName() +" killed "+victim.getName()+".";
		return true;
	}
	public boolean scoreFlag(Player player){
		if(!player.alive)return false;
		if(player.getName()==playerWithFlag1){
			playerWithFlag1="";
			if(player.getTeam()==Team.TEAM_1)
				player.score+=SCORE_VALUE;
			else 
				player.score+=RETURN_VALUE;
		}
		if(player.getName()==playerWithFlag2){
			playerWithFlag2="";
			if(player.getTeam()==Team.TEAM_2)
				player.score+=SCORE_VALUE;
			else 
				player.score+=RETURN_VALUE;
		}
		message = player.getName()+" just scored! Flag returns to base.";
		return true;
	}
	public boolean captureFlag(Player player){
		if(!player.alive)return false;
		if(player.getTeam()==Team.TEAM_1&&playerWithFlag2==""){
			playerWithFlag2=player.getName();
		} else if(player.getTeam()==Team.TEAM_2&&playerWithFlag1==""){
			playerWithFlag1=player.getName();
		}
		message = player.getName()+" has the flag! Go kill him now.";
		return true;
	}
	
	public void addPlayer(Player player){
		players.add(player);
		if(player.getTeam()==Team.TEAM_1)team1.addPlayer(player.getName());
		if(player.getTeam()==Team.TEAM_2)team2.addPlayer(player.getName());
	}
	public void removePlayer(Player player){
		players.remove(player);
		team1.removePlayer(player.getName());
		team2.removePlayer(player.getName());
	}
	public String[][] listPlayers(){
		String[][] playerList = new String[2][];
		playerList[0] = team1.listOfPlayers();
		playerList[1] = team2.listOfPlayers();
		return playerList;
	}
	public Player getPlayerByName(String name){
		Player player = null;
		for(Player p1:players){
			if(p1.getName().equals(name))player=p1;
		}
		return player;
	}
	public Player getPlayerByQRId(String QRId){
		Player player = null;
		for(Player p1:players){
			if(p1.getQRId().equals(QRId))player=p1;
		}
		return player;
	}
	public String getMessage(){
		return message;
	}
	public void clearMessage(){
		message="";
	}
	public int[] getTeamScores(){
		int t1 = 0;
		int t2 = 0;
		for(Player p1:players){
			if(p1.getTeam()==Team.TEAM_1)t1+=p1.score;
			if(p1.getTeam()==Team.TEAM_2)t2+=p1.score;
		}
		return new int[]{t1,t2};
	}
	public JSONObject toJSON() throws JSONException{
		JSONArray playerArray = new JSONArray();
		for(Player player:players){
			playerArray.put(player.toJSON());
		}		
		JSONObject obj = new JSONObject();
		obj.put("gameStarted", gameStarted);
		obj.put("team1", team1.toJSON());
		obj.put("team2", team2.toJSON());
		obj.put("players", playerArray);
		obj.put("message", message);
		obj.put("globalMessage", globalMessage);
		obj.put("playerMessage", playerMessage);
		obj.put("gameSensitivity", gameSensitivity);
		obj.put("playerWithFlag1", playerWithFlag1);
		obj.put("playerWithFlag2", playerWithFlag2);
		return obj;
	}
	public static GameState fromJSON(JSONObject obj) throws JSONException{
		GameState state = new GameState();
		state.gameStarted = obj.getBoolean("gameStarted");
		state.team1 = Team.fromJSON(obj.getJSONObject("team1"));
		state.team2 = Team.fromJSON(obj.getJSONObject("team2"));
		JSONArray players = obj.getJSONArray("players");
		for(int i=0; i<players.length(); i++){
			state.players.add(Player.fromJSON(players.getJSONObject(i)));
		}
		state.message=obj.getString("message");
		state.playerMessage=obj.getString("playerMessage");
		state.globalMessage=obj.getString("globalMessage");
		state.gameSensitivity=obj.getInt("gameSensitivity");
		state.playerWithFlag1=obj.getString("playerWithFlag1");
		state.playerWithFlag2=obj.getString("playerWithFlag2");
		return state;
	}
}
