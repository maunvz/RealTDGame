package me.dm7.barcodescanner.zbar;

import java.util.Collection;
import java.util.List;

import me.dm7.barcodescanner.core.BarcodeScannerView;
import me.dm7.barcodescanner.core.DisplayUtils;
import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.text.TextUtils;
import me.dm7.barcodescanner.core.BarcodeScannerView;
import me.dm7.barcodescanner.core.DisplayUtils;

public class LiveScanThread extends Thread {
	byte[] data;
	Camera camera;
	Context c;
	int result = 0;
	private ImageScanner mScanner;
	static boolean isRunning = false;
	static boolean scanFinished = false;
//	static {
//        System.loadLibrary("iconv");
//    }
	public LiveScanThread(){
		
	}
	public LiveScanThread(byte[] data,Camera camera,Context c){
		this.data = data;
		this.camera = camera;
		this.c = c;
		setupScanner();
	}
	public int getResult(){
		return result;
	}
	public void setupScanner() {
        mScanner = new ImageScanner();
        mScanner.setConfig(0, Config.X_DENSITY, 3);
        mScanner.setConfig(0, Config.Y_DENSITY, 3);

        mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
        for(BarcodeFormat format : getFormats()) {
            mScanner.setConfig(format.getId(), Config.ENABLE, 1);
        }
    }
    private List<BarcodeFormat> mFormats;

	public Collection<BarcodeFormat> getFormats() {
        if(mFormats == null) {
            return BarcodeFormat.ALL_FORMATS;
        }
        return mFormats;
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
	      int result = mScanner.scanImage(barcode);
	      barcode.destroy();
	      data = null;
//	      scanFinished = true;
	      if (result != 0) {
//        stopCamera();
	    	  if(ZBarScannerView.mResultHandler != null) {
	    		  SymbolSet syms = mScanner.getResults();
	              Result rawResult = new Result();
	              for (Symbol sym : syms) {
	              	String symData = sym.getData();
	                if (!TextUtils.isEmpty(symData)) {
	                	rawResult.setContents(symData);
	                	rawResult.setBarcodeFormat(BarcodeFormat.getFormatById(sym.getType()));
	                	break;
	                }
	              }
	              ZBarScannerView.mResultHandler.handleResult(rawResult);
	            }
	      	}
          isRunning = false;
	}
}
