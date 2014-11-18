package me.dm7.barcodescanner.zbar;

import me.dm7.barcodescanner.core.DisplayUtils;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.text.TextUtils;

public class LiveScanThread extends Thread{
	byte[] data;
	Camera camera;
	Context c;
	ImageScanner i;
	int result = 0;
	static boolean isRunning = false;
	static boolean scanFinished = false;
	static {
        System.loadLibrary("iconv");
    }
	public LiveScanThread(byte[] data,Camera camera,Context c,ImageScanner i){
		this.data = data;
		this.camera = camera;
		this.c = c;
		this.i = i;
	}
	public int getResult(){
		return result;
	}
	public void run(){
		Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        int width = size.width;
        int height = size.height;
        
    	        if(DisplayUtils.getScreenOrientation(c) == Configuration.ORIENTATION_PORTRAIT) {
              byte[] rotatedData = new byte[data.length];
              for (int y = 0; y < height; y++) {
                  for (int x = 0; x < width; x++)
                      rotatedData[x * height + height - y - 1] = data[x + y * width];
              }
              int tmp = width;
              width = height;
              height = tmp;
              data = rotatedData;
          }

          Image barcode = new Image(width, height, "Y800");
          barcode.setData(data);
	          int result = i.scanImage(barcode);    
//	          scanFinished = true;
//	          if(result != 0) System.out.println("I gots somethin");
          isRunning = false;
	}
}
