package com.mau.tdclient;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GameListAdapter extends BaseAdapter{
	Context context;
	ArrayList<String> names = new ArrayList<String>();
	LayoutInflater inflater;
	public GameListAdapter(Context cx,ArrayList<String> names){
		this.names = names;
		context = cx;
		inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return names.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return names.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = null;
        if (vi == null)
            vi = inflater.inflate(R.layout.game_item, null);
        TextView name = ((TextView)vi.findViewById(R.id.game_name));
        name.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/LCD Display Grid.ttf"));
        name.setText(names.get(position));
        return vi;
	}

}
