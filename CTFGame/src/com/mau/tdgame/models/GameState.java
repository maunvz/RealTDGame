package com.mau.tdgame.models;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameState {
	public static final String BASE_1_QR = "base_0";
	public static final String BASE_2_QR = "base_1";
	public static final String FLAG_1_QR = "flag_0";
	public static final String FLAG_2_QR = "flag_1";
	
	public static final int SCORE_VALUE=10;
	public static final int RETURN_VALUE=5;
	public static final int KILL_VALUE=2;
	public static final int KILL_PENALTY=1;
	public static final int DEFAULT_SENSITIVITY = 4;
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
	public int activePlayers;
	public boolean gameEnded;
	
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
		activePlayers=0;
		gameEnded = false;
	}
	public boolean gameStarted(){
		return gameStarted;
	}
	public void startGame(){
		gameStarted=true;
	}
	public void endGame(){
		gameStarted=false;
		gameEnded=true;
	}
	public void processEvent(Event event){
		Player player1 = getPlayerByName(event.player1);
		message = "";
		switch(event.getType()){
		case Event.START_GAME:
			startGame();
		case Event.DIED:
			playerDies(player1);
			break;
		case Event.QR_EVENT:
			if(event.string1.equals(BASE_1_QR)&&player1.getTeam()==Team.TEAM_1||event.string1.equals(BASE_2_QR)&&player1.getTeam()==Team.TEAM_2){
				playerReachedOwnBase(player1);
			} else if(event.string1.equals(FLAG_1_QR)&&player1.getTeam()==Team.TEAM_2||event.string1.equals(FLAG_2_QR)&&player1.getTeam()==Team.TEAM_1){
				playerReachedEnemyBase(player1);
			} else {
				if(killPlayer(player1, getPlayerByQRId(event.string1)))return;
				processPower(player1, event);
			}
			break;
		case Event.POWERUP_USED:
			for(int i=0; i<player1.powerups.size(); i++){
				if(player1.powerups.get(i).equals(event.int1)){
					player1.powerups.remove(i);
					break;
				}
			}
			usePower(player1, event.int1, event.string1);
			break;
		}
		if(getTeamScores()[0]>=maxScore||getTeamScores()[1]>=maxScore){
			endGame();
		}
	}
	public void processPower(Player player, Event event){
		if(!player.alive)return;
		if(event.string1.equals("respawn_power")){
			player.powerups.add(Player.RESPAWN_ANYWHERE);
		}
		if(event.string1.equals("stronger_power")){
			player.powerups.add(Player.STRONGER);	
		}
	}
	public void usePower(Player player, int power, String str){
		switch(power){
		case Player.NUKE:
			for(Player p:players)
				if(!p.equals(player))
					playerDies(p);
			break;
		case Player.RESPAWN_ANYWHERE:
			playerRespawns(player);	
			break;
		case Player.SNIPER:
			killPlayer(player, randomPlayer());
			break;
		case Player.STRONGER:
			player.stronger=true;
			break;
		}
	}
	public Player randomPlayer(){
		Random r = new Random();
		return players.get(r.nextInt(players.size()));
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
		player.stronger = false;
		player.killCount=0;
		message = player.getName() + " died.";
		if(playerWithFlag1==player.getName())playerWithFlag1="";
		if(playerWithFlag2==player.getName())playerWithFlag2="";
		boolean respawn = false;
		if(player.powerups.contains(Player.RESPAWN_ANYWHERE))respawn=true;
		player.powerups.clear();
		if(respawn)player.powerups.add(Player.RESPAWN_ANYWHERE);
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
		killer.killCount++;
		victim.score-=KILL_PENALTY;
		message = killer.getName() +" killed "+victim.getName()+".";
		if(killer.killCount==5)killer.powerups.add(Player.NUKE);
		if(killer.killCount==2)killer.powerups.add(Player.SNIPER);
		return true;
	}
	public boolean scoreFlag(Player player){
		if(!player.alive)return false;
		if(player.getName()==playerWithFlag1){
			playerWithFlag1="";
			if(player.getTeam()==Team.TEAM_2){
				player.score+=SCORE_VALUE;				
				message = player.getName()+" just scored! Flag returns to base.";
				globalMessage = message;
			} else {
				player.score+=RETURN_VALUE;
				message = player.getName()+" returned the flag to base!";
			}
		}
		if(player.getName()==playerWithFlag2){
			playerWithFlag2="";
			if(player.getTeam()==Team.TEAM_1){
				player.score+=SCORE_VALUE;				
				message = player.getName()+" just scored! Flag returns to base.";
				globalMessage = message;
			} else {
				player.score+=RETURN_VALUE;				
				message = player.getName()+" returned the flag to base!";
			}
		}
		return true;
	}
	public boolean captureFlag(Player player){
		if(!player.alive)return false;
		if(player.getTeam()==Team.TEAM_1&&playerWithFlag2==""){
			playerWithFlag2=player.getName();
		} else if(player.getTeam()==Team.TEAM_2&&playerWithFlag1==""){
			playerWithFlag1=player.getName();
		}
		message = player.getName()+" has the flag! Go kill them now.";
		globalMessage = message;
		return true;
	}
	
	public void addPlayer(Player player){
		activePlayers++;
		players.add(player);
		if(player.getTeam()==Team.TEAM_1)team1.addPlayer(player.getName());
		if(player.getTeam()==Team.TEAM_2)team2.addPlayer(player.getName());
	}
	public void removePlayer(Player player){
		activePlayers--;
		if(playerWithFlag1.equals(player.getName()))playerWithFlag1="";
		if(playerWithFlag2.equals(player.getName()))playerWithFlag2="";
		players.remove(player);
		team1.removePlayer(player.getName());
		team2.removePlayer(player.getName());
	}
	public void disconnectPlayer(Player player){
		player.connected = false;
		if(playerWithFlag1.equals(player.getName()))playerWithFlag1="";
		if(playerWithFlag2.equals(player.getName()))playerWithFlag2="";
		activePlayers--;
	}
	public void reconnectPlayer(Player player){
		activePlayers++;
		player.connected=true;
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
