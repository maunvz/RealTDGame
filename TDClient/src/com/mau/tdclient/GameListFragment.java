package com.mau.tdclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mau.tdgame.models.Constants;

public class GameListFragment extends Fragment{
	MainActivity ma;
	ListView list;
	public GameListFragment(MainActivity ma){
		super();
		this.ma=ma;
	}
	public void onPause(){
		super.onPause();
	}
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		new GetGameList().execute();
	}
	public void onResume(){
		super.onResume();
		MainActivity.screenNo=MainActivity.GAME_LIST_SCREEN;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.game_list_fragment, container, false);
        list = (ListView) view.findViewById(R.id.game_list_listview);
        list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int port = Integer.parseInt(((String)list.getAdapter().getItem(position)).split("/")[1]);
				ma.joinGame(port);
			}        
        });
        return view;
	}
	public class GetGameList extends AsyncTask<Void, Void, String>{
		@Override
		protected String doInBackground(Void... params) {
			try{
				Socket socket = new Socket();
				SocketAddress address = new InetSocketAddress(Constants.host, 1726);
				socket.connect(address);
				
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				
				pw.println(Constants.JOIN_ROOM);
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
				ArrayList<String> sessions = new ArrayList<String>();
				JSONArray sessionJSONs = new JSONArray(result);
				for(int i=0; i<sessionJSONs.length(); i++){
					sessions.add(sessionJSONs.getJSONObject(i).getString("name")+"/"+sessionJSONs.getJSONObject(i).getInt("port"));
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(ma, android.R.layout.simple_list_item_1, sessions);
				list.setAdapter(adapter);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
