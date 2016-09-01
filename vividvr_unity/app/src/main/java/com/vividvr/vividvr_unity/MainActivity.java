package com.vividvr.vividvr_unity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class MainActivity extends Activity implements OnTouchListener {

    private static final String    TAG = "VividVR::MainActivity";

    private TextView minTresholdSeekbarText = null;
    private TextView numberOfFingersText = null;

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

        SeekBar minTresholdSeekbar = (SeekBar) findViewById(R.id.seekBar1);
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
