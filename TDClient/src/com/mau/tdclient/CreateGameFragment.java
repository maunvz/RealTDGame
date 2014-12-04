package com.mau.tdclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mau.tdgame.models.Constants;
import com.mau.tdgame.models.GameState;

public class CreateGameFragment extends Fragment{
	MainActivity ma;
	EditText name;
	EditText maxScore;
	
	public CreateGameFragment(MainActivity ma){
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
		MainActivity.screenNo=MainActivity.CREATE_GAME_SCREEN;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.create_game_fragment, container, false);
        ((EditText)view.findViewById(R.id.max_score_edittext)).setText(""+GameState.DEFAULT_MAX_SCORE);
        ((Button)view.findViewById(R.id.create_game_send_button)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				new CreateGameTask().execute(name.getText().toString(), maxScore.getText().toString());
				MainActivity.destroyed = false;
			}        
        });
        maxScore = ((EditText)view.findViewById(R.id.max_score_edittext));
        name = ((EditText)view.findViewById(R.id.game_name_edittext));
        return view;
	}
	public class CreateGameTask extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... params) {
			try{
				Socket socket = new Socket();
				SocketAddress address = new InetSocketAddress(Constants.host, 1726);
				socket.connect(address);
				
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				
				pw.println(Constants.CREATE_ROOM);
				pw.println(params[0]);
				pw.println(params[1]);
				
				String response = br.readLine();
				socket.close();
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		protected void onPostExecute(String result){
			if(result==null)return;
			try {
				JSONObject session = new JSONObject(result);
				int port = session.getInt("port");
				ma.joinGame(port);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
