package com.mau.tdclient;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.os.AsyncTask;

public class ServerScanner extends AsyncTask<Void, Integer, String>{
	String server_ip;
	MainActivity ma;
	int finished;
	public ServerScanner(MainActivity ma){
		this.ma=ma;
		server_ip="";
		finished=0;
	}
	@Override
	protected String doInBackground(Void... params) {
		long start = System.nanoTime();
		System.out.println("Scanning for server");
		ExecutorService ex = Executors.newFixedThreadPool(512);			
		try{
			ExecutorService exq = Executors.newFixedThreadPool(512);
			final byte[] myIp = getIp().getAddress();
			for(int i=0; i<256; i++){
				exq.execute(new TryIpThread(myIp, myIp[2], i));
			}
			exq.shutdown();
			exq.awaitTermination(1000, TimeUnit.MILLISECONDS);
			if(!server_ip.equals(""))return server_ip;
			for(int j=0; j<256; j++){
				for(int i=0; i<256; i++){
					ex.execute(new TryIpThread(myIp, j, i));
				}
			}
		} catch (NullPointerException | InterruptedException e) {
			e.printStackTrace();
		}
		ex.shutdown();
		System.out.println("Done adding, " + ((System.nanoTime()-start)/1000000));
		while(!ex.isTerminated()){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done searching, " + ((System.nanoTime()-start)/1000000)+" ip: "+server_ip);
		return server_ip;
	}
	protected void onProgressUpdate(Integer... values){
		ma.foundServer("scanning..."+(values[0]/256+1)+"/256");
	}
	private synchronized void setServerIp(String ip){
		server_ip=ip;
	}
	private synchronized void finishedThread(){
		finished++;
		if(finished%256==0)publishProgress(finished);
	}
	class TryIpThread implements Runnable{
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
			finishedThread();
		}
	}
	protected void onPostExecute(String ip){
		ma.foundServer(ip);
		ma.setScanEnabled(true);
	}
	public void tryIp(byte[] myIp, int secondLastByte, int lastByte) throws Exception{
		InetAddress ia = InetAddress.getByAddress(new byte[]{myIp[0],myIp[1],(byte)secondLastByte,(byte)lastByte});
		Socket s = new Socket();
		s.connect(new InetSocketAddress(ia,1726), 120);
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
