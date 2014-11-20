package com.mau.tdclient;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.os.AsyncTask;

public class ServerScanner extends AsyncTask<Void, Void, String>{
	String server_ip;
	MainActivity ma;
	public ServerScanner(MainActivity ma){
		this.ma=ma;
	}
	@Override
	protected String doInBackground(Void... params) {
		try{
			final byte[] myIp = getIp().getAddress();
			int div=3;
			for(int j=myIp[2]-div; j<myIp[2]+div; j++){
				for(int i=0; i<256; i++){
					new TryIpThread(myIp, j, i).start();
				}				
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return server_ip;
	}
	private synchronized void setServerIp(String ip){
		server_ip=ip;
	}
	class TryIpThread extends Thread{
		byte[] myIp;
		int lastByte;
		int secondLastByte;
		public TryIpThread(byte[] ip, int j, int i){
			myIp=ip;
			secondLastByte=j;
			lastByte=i;
		}
		public void run(){
			try {
				tryIp(myIp, secondLastByte, lastByte);
			} catch (Exception e) {}
		}
	}
	protected void onPostExecute(String ip){
		ma.foundServer(ip);
	}
	public void tryIp(byte[] myIp, int secondLastByte, int lastByte) throws Exception{
		InetAddress ia = InetAddress.getByAddress(new byte[]{myIp[0],myIp[1],(byte)secondLastByte,(byte)lastByte});
		Socket s = new Socket();
		s.connect(new InetSocketAddress(ia,1726), 200);
		PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
		pw.println("server_scan");
		s.close();
		setServerIp(ia.getHostAddress());
	}
	public static InetAddress getIp(){
		try {
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements()){
				NetworkInterface ni = e.nextElement();
				Enumeration<InetAddress> a = ni.getInetAddresses();
				while(a.hasMoreElements()){
					InetAddress ia = a.nextElement();
					if(ia.isSiteLocalAddress())
						return ia;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

}
