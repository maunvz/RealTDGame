package me.dm7.barcodescanner.zbar;

import java.util.List;

import me.dm7.barcodescanner.core.BarcodeScannerView;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

public class ZBarScannerView extends BarcodeScannerView {
	static int result = 0;
	LiveScanThread lst = new LiveScanThread();
    public interface ResultHandler {
        public void handleResult(Result rawResult);
    }

    static {
        System.loadLibrary("iconv");
    }

//    private ImageScanner mScanner;
    static ResultHandler mResultHandler;

    public ZBarScannerView(Context context) {
        super(context);
//        setupScanner();
    }

    public ZBarScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
//        setupScanner();
    }

    public void setFormats(List<BarcodeFormat> formats) {
//        mFormats = formats;
//        setupScanner();
    }

    public void setResultHandler(ResultHandler resultHandler) {
        mResultHandler = resultHandler;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
      	lst = new LiveScanThread(data,camera,getContext());
    	lst.start();
        camera.setOneShotPreviewCallback(this);
    }
}