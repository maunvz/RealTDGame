package com.mau.tdclient;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import me.dm7.barcodescanner.zbar.ZBarScannerView.ResultHandler;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.mau.tdgame.models.Event;
import com.mau.tdgame.models.Team;

public class GameFragment extends Fragment implements ResultHandler{
	public static boolean QREnabled;
	public static Animation myFadeOutAnimation;
	public static boolean animationCancelled;
	int QR_time = 5000;
	
	public static MainActivity ma;
	private ZBarScannerView mScannerView;
	public GameFragment(MainActivity ma){
		super();
		this.ma=ma;
		QREnabled=false;
	}
	public void onPause(){
		super.onPause();
		disableQR();
	}
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.game_fragment,container,false);
		mScannerView = (ZBarScannerView)(v.findViewById(R.id.zbarscan));
		mScannerView.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					if(QREnabled){
						Toast.makeText(ma, "Still Fading", Toast.LENGTH_SHORT).show();
					}
					else{
						enableQR();
					}
				}
				return false;
			}	
		});
		return v;
	}
	@Override
	public void handleResult(Result rawResult) {
		if(!rawResult.getContents().contains("player")&&!rawResult.getContents().contains("flag")&&
				!rawResult.getContents().contains("base")&&!rawResult.getContents().contains("power")){
			return;
		}
		else if(rawResult.getContents().equals("base_0")&&ma.getPlayer().getTeam()==Team.TEAM_1){
			if(ma.getPlayer().alive&&!(ma.getGameState().playerWithFlag2.equals(ma.getPlayer().getName())||
					ma.getGameState().playerWithFlag1.equals(ma.getPlayer().getName()))){
//				Toast.makeText(getActivity(), "You already got the flag",Toast.LENGTH_SHORT).show();
				
				return;
			}
		}
		else if(rawResult.getContents().equals("base_0")&&ma.getPlayer().getTeam()==2){
			return;
		}
		else if(rawResult.getContents().equals("base_1")&&ma.getPlayer().getTeam()==Team.TEAM_2){
			if(ma.getPlayer().alive&&!(ma.getGameState().playerWithFlag1.equals(ma.getPlayer().getName())||
					ma.getGameState().playerWithFlag1.equals(ma.getPlayer().getName()))){
//				Toast.makeText(getActivity(), "You already got the flag",Toast.LENGTH_SHORT).show();
				return;
			}
		}
		else if(rawResult.getContents().equals("base_1")&&ma.getPlayer().getTeam()==Team.TEAM_1){
			return;
		}
		else if(rawResult.getContents().contains("player")){
			if(ma.getPlayer().getTeam() == Team.TEAM_1){
				if(ma.getGameState().getPlayerByQRId(rawResult.getContents()).getTeam()==Team.TEAM_1){
					return;
				}
				else if(ma.getGameState().getPlayerByQRId(rawResult.getContents()).getTeam()==Team.TEAM_2&&
						!ma.getGameState().getPlayerByQRId(rawResult.getContents()).alive){
					return;
				}
			}
			else{
				if(ma.getGameState().getPlayerByQRId(rawResult.getContents()).getTeam()==Team.TEAM_2){
					return;
				}
				else if(ma.getGameState().getPlayerByQRId(rawResult.getContents()).getTeam()==Team.TEAM_1&&
						!ma.getGameState().getPlayerByQRId(rawResult.getContents()).alive){
					return;
				}
			}
		}
		else if(rawResult.getContents().contains("flag")){
			if(!ma.getGameState().playerWithFlag1.equals("")){
//				String actualMessage = ma.getGameState().playerWithFlag1.replaceAll(ma.getPlayer().getName(), "you");
//				actualMessage = actualMessage.substring(0,1).toUpperCase() + actualMessage.substring(1).toLowerCase();
//				if(actualMessage.toLowerCase().contains("you"))
//					actualMessage += " already have the flag";
//				else
//					actualMessage += " has the flag.";
//				ma.makeAToast(actualMessage);
				return;
			}
			else if(!ma.getGameState().playerWithFlag2.equals("")){
//				String actualMessage = ma.getGameState().playerWithFlag1.replaceAll(ma.getPlayer().getName(), "you");
//				actualMessage = actualMessage.substring(0,1).toUpperCase() + actualMessage.substring(1).toLowerCase();
//				if(actualMessage.toLowerCase().contains("you"))
//					actualMessage += " already have the flag";
//				else
//					actualMessage += " has the flag.";
//				ma.makeAToast(actualMessage);
				return;
			}
			else if(rawResult.getContents().contains("0")&&ma.getPlayer().getTeam()==Team.TEAM_1){
				return;
			}
			else if(rawResult.getContents().contains("1")&&ma.getPlayer().getTeam()==Team.TEAM_2){
				return;
			}
		}
		ma.getNC().sendEvent(new Event(Event.QR_EVENT, ma.getPlayer().getName(), rawResult.getContents(), 0));
	}
	public void onResume(){
		super.onResume();
		ma.screenNo = MainActivity.GAME_SCREEN;
		ma.updateStateDisplay();
		enableQR();
	}
	public void enableQR(){
		if(QREnabled)return;Log.d("CAM", "Enabling Camera");
		if(!animationCancelled){
			mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
			mScannerView.startCamera();
		}
		animationCancelled = false;
        new Thread(){
        	public void run(){
        		try {
					Thread.sleep(QR_time);
					ma.runOnUiThread(new Runnable(){
						@Override
						public void run() {
							
							disableQR();
						}					
					});
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        }.start();
	}
	public synchronized void disableQR(){
	
		myFadeOutAnimation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.fade_in);
		final ImageView v = (ImageView) ma.findViewById(R.id.fader);
		v.setBackgroundColor(Color.BLACK);
		v.startAnimation(myFadeOutAnimation);
		myFadeOutAnimation.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation){
				if(!QREnabled)return;
				Thread t = new Thread(){
					public void run(){
						if(mScannerView != null){
							mScannerView.stopCamera();
							QREnabled=false;
						}

					}
				};
				t.start();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}
      		
      	});
		
		
	}
}
