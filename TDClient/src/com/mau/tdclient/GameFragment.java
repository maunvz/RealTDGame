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
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.mau.tdgame.models.Event;
import com.mau.tdgame.models.Player;
import com.mau.tdgame.models.Team;

public class GameFragment extends Fragment implements ResultHandler{
	public static boolean QREnabled;
//	public static Animation myFadeOutAnimation;
	public static boolean animationCancelled;
	public static View view;
	int QR_time = 5000;
	
	public static MainActivity ma;
	private ZBarScannerView mScannerView;
	public GameFragment(MainActivity ma){
		super();
		GameFragment.ma=ma;
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
		if (view != null) {
	        ViewGroup parent = (ViewGroup) view.getParent();
	        if (parent != null)
	            parent.removeView(view);
	    }
	    try {
	        view = inflater.inflate(R.layout.game_fragment, container, false);
	    } catch (Exception e) {
	        /* map is already there, just return view as it is */
	    }
		mScannerView = (ZBarScannerView)(view.findViewById(R.id.zbarscan));
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
		return view;
	}
	@Override
	public void handleResult(Result rawResult) {
		String qr = rawResult.getContents();
		//prevent from sending random QRs
		if(!qr.contains("player")&&!qr.contains("flag")&&!qr.contains("base")&&!qr.contains("power"))return;
		
		//prevent from sending when player scans other teams's base
		if(qr.equals("base_0")&&ma.getPlayer().getTeam()==Team.TEAM_2||qr.equals("base_1")&&ma.getPlayer().getTeam()==Team.TEAM_1)return;
		
		//prevent from sending when at own base without flag and alive
		if((qr.equals("base_0")&&ma.getPlayer().getTeam()==Team.TEAM_1)&&
			(ma.getPlayer().alive&&!(ma.getGameState().playerWithFlag2.equals(ma.getPlayer().getName())||
				ma.getGameState().playerWithFlag1.equals(ma.getPlayer().getName()))))
					return;
		if((qr.equals("base_1")&&ma.getPlayer().getTeam()==Team.TEAM_2)&&
			(ma.getPlayer().alive&&!(ma.getGameState().playerWithFlag1.equals(ma.getPlayer().getName())||
				ma.getGameState().playerWithFlag1.equals(ma.getPlayer().getName()))))
					return;
		//prevent dead players from scanning anything but their flag
		if(!ma.getPlayer().alive&&!qr.contains("base"))return;
		//prevent from sending qrs of dead players or players on the same team
		if(qr.contains("player")){
			Player QRPlayer = ma.getGameState().getPlayerByQRId(qr); 
			if(QRPlayer==null)return;if(!QRPlayer.alive)return;
			if(ma.getPlayer().getTeam()==QRPlayer.getTeam())return;
		}
		//prevent from scanning a flag is already captured, or scanning your own flag
		if(qr.contains("flag_0")&&(!ma.getGameState().playerWithFlag1.equals("")||ma.getPlayer().getTeam()==Team.TEAM_1))return;
		if(qr.contains("flag_1")&&(!ma.getGameState().playerWithFlag2.equals("")||ma.getPlayer().getTeam()==Team.TEAM_2))return;
		//send event
		ma.getNC().sendEvent(new Event(Event.QR_EVENT, ma.getPlayer().getName(), qr, 0));
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
		if(this.getActivity()==null||MainActivity.destroyed){
			stopQRAfterAnimation();
			return;
		}
		final Animation myFadeOutAnimation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.fade_in);
		final ImageView v = (ImageView) ma.findViewById(R.id.fader);
		v.setBackgroundColor(Color.BLACK);
		v.startAnimation(myFadeOutAnimation);
		myFadeOutAnimation.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation){
				stopQRAfterAnimation();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}
      		
      	});
	}
	public synchronized void stopQRAfterAnimation(){
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
}
