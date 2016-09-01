package com.vividvr.vividvr_unity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import at.jumpch.sdk.JCVideoCapture;
import at.jumpch.sdk.JCVideoCaptureCallback;
import imageProcessing.ColorBlobDetector;

public class MainActivity extends Activity implements OnTouchListener {   //  CvCameraViewListener2

    private static final String    TAG                 = "HandPose::MainActivity";
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;

    private final AtomicReference<Mat> mRgba = new AtomicReference<Mat>();
    private Mat 					mIntermediateMat;

    private int                    mDetectorType       = JAVA_DETECTOR;

    private List<Size> mResolutionList;
    
    private SeekBar minTresholdSeekbar = null;
    private TextView minTresholdSeekbarText = null;
    private TextView numberOfFingersText = null;

    double iThreshold = 0;

    private boolean				mIsColorSelected = false;


    private ImageView im_view;
    private VividUnityPlugin plugin = new VividUnityPlugin();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Mat img) {
        // convert to bitmap for display

//        Core.flip(img, img, -1);  This corrects the view but is too slow
        Bitmap bm = Bitmap.createBitmap(img.cols(), img.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img, bm);
        im_view.setImageBitmap(bm);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Integer numberOfFingers) {
        numberOfFingersText.setText(String.valueOf(numberOfFingers));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        EventBus.getDefault().register(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main_surface_view);

        minTresholdSeekbarText = (TextView) findViewById(R.id.textView3);

        im_view = (ImageView) findViewById(R.id.imageView);

        numberOfFingersText = (TextView) findViewById(R.id.numberOfFingers);
        
        minTresholdSeekbar = (SeekBar)findViewById(R.id.seekBar1);        
        minTresholdSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
	        	int progressChanged = 0;
	        	 
	            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
	                progressChanged = progress;
	                minTresholdSeekbarText.setText(String.valueOf(progressChanged));
	            }

	            public void onStartTrackingTouch(SeekBar seekBar) {}
	 
	            public void onStopTrackingTouch(SeekBar seekBar) {
	            	minTresholdSeekbarText.setText(String.valueOf(progressChanged));
	            }
		});


        minTresholdSeekbar.setProgress(8700);
        plugin.init_plugin(this, 6000);
        im_view.setOnTouchListener(MainActivity.this);
        super.onCreate(savedInstanceState);
    }

    public void restart_jc(View v) {
        plugin.stop_capturing();
        mIsColorSelected = false;
        start_jc(v);
    }

    public void start_jc(View v) {
        plugin.start_capturing();
    }

    public boolean onTouch(View v, MotionEvent event) {
        int cols = 480; // copy.cols();
        int rows = 640; // copy.rows();

        int xOffset = (im_view.getWidth() - cols) / 2;
        int yOffset = (im_view.getHeight() - rows) / 2;

        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

        plugin.calibrate(x, y);
        return false; // don't need subsequent touch events
    }

}
