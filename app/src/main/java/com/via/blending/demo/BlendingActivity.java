package com.via.blending.demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.media.MediaPlayer;


public class BlendingActivity extends Activity {

	private static final String TAG = "BlendingActivity";
    private DisplayManager mDisplayManager;
	protected LinearLayout mContain_Display;
	protected TextView mText_Display;
	private ArrayList<VideoPresentation> mVideoPresentations = new ArrayList<>();

	//读写权限
	private final int REQUEST_EXTERNAL_STORAGE = 1;
	private String[] PERMISSIONS_STORAGE = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE};
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get the display manager service.
        mDisplayManager = (DisplayManager)getSystemService(Context.DISPLAY_SERVICE);
        
        Display[] displays = mDisplayManager.getDisplays();
        Log.e(TAG, "Currently " + displays.length + " displays connected.");
        for (Display display : displays) {
        	showPresentation(display);
        }
	}


	private void showPresentation(Display display) {

        VideoPresentation presentation = new VideoPresentation(this, display);
        mVideoPresentations.add(presentation);
        presentation.show();
        
    }

	
    /**
     * The video presentation to show on dual displays.
     *
     * Note that the presentation display may have different metrics from the display on which
     * the main activity is showing so we must be careful to use the presentation's
     * own {@link Context} whenever we load resources.
     */
    private final class VideoPresentation extends Presentation {

		private String TAG = "VideoPresentation";
		private TextView mTextView;

		private SurfaceView mSurfaceView1;
		private MediaPlayer player1;
		private SurfaceHolder holder1;

		private SurfaceView mSurfaceView2;
		private MediaPlayer player2;
		private SurfaceHolder holder2;

		private static final String videoName1 = "video1.mp4";
		private static final String videoName2 = "video2.mp4";
		private static final String videoName3 = "video3.mp4";
		private static final String videoName4 = "video4.mp4";
		private String uri1 = "";
		private String uri2 = "";

		public VideoPresentation(Context context, Display display) {
			super(context, display);
			if(display.getDisplayId() == 0){
				uri1 = Environment.getExternalStorageDirectory() + "/" + videoName1;
				uri2 = Environment.getExternalStorageDirectory() + "/" + videoName2;
			}else {
				uri1 = Environment.getExternalStorageDirectory() + "/" + videoName3;
				uri2 = Environment.getExternalStorageDirectory() + "/" + videoName4;
			}
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// Be sure to call the super class.
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_blending);
			initViews();
			initPlayer1();
			initPlayer2();
		}

		private void initViews() {
			mSurfaceView1 = findViewById(R.id.surfaceView1);
			mSurfaceView2 = findViewById(R.id.surfaceView2);
			mSurfaceView2.setZOrderOnTop(true);//避免被下层surfaceview遮挡
			mSurfaceView2.setZOrderMediaOverlay(true);//如果不添加，mSurfaceView2上面的view将会被覆盖不显示
			mTextView = findViewById(R.id.textView);
			mTextView.setSelected(true);

		}

		private void initPlayer1(){
			Log.d(TAG,"video file path : " + uri1);
			if(!new File(uri1).exists()) {
				Log.d(TAG,"video file not found");
				return;
			}
			player1 = new MediaPlayer();
			try {
				player1.setDataSource(uri1);
				holder1 = mSurfaceView1.getHolder();
				holder1.addCallback(new SurfaceHolder.Callback() {
					@Override
					public void surfaceCreated(SurfaceHolder holder) {
						Log.d(TAG,"surface created");
						player1.setDisplay(holder);
					}

					@Override
					public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

					}

					@Override
					public void surfaceDestroyed(SurfaceHolder holder) {

					}
				});
				player1.prepare();
				player1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						Log.d(TAG,"player 1 start to play video");
						player1.start();
						player1.setLooping(true);
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void initPlayer2(){
			Log.d(TAG,"video file path : " + uri2);
			if(!new File(uri2).exists()) {
				Log.d(TAG,"video file not found");
				return;
			}
			player2 = new MediaPlayer();
			try {
				player2.setDataSource(uri2);
				holder2 = mSurfaceView2.getHolder();
				holder2.addCallback(new SurfaceHolder.Callback() {
					@Override
					public void surfaceCreated(SurfaceHolder holder) {
						Log.d(TAG,"surface created");
						player2.setDisplay(holder);
					}

					@Override
					public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

					}

					@Override
					public void surfaceDestroyed(SurfaceHolder holder) {

					}
				});
				player2.prepare();
				player2.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						Log.d(TAG,"player 2 start to play video");
						player2.start();
						player2.setLooping(true);
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void destroy(){
			if(player1 != null){
				player1.release();
				player1 = null;
				holder1 = null;
			}
			if(player2 != null){
				player2.release();
				player2 = null;
				holder1 = null;
			}
		}

	}

	/**
	 * 检查应用程序是否有权写入设备存储
	 * 如果应用程序没有权限，则会提示用户授予权限
	 *
	 * @param activity 所在的Activity
	 */
	public void verifyStoragePermissions(Activity activity) {
		//检查应用程序是否有权写入设备存储
		int permission = ActivityCompat.checkSelfPermission(activity,
				Manifest.permission.WRITE_EXTERNAL_STORAGE);

		if (permission != PackageManager.PERMISSION_GRANTED) {
			//如果应用程序没有权限，则会提示用户授予权限
			ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
					REQUEST_EXTERNAL_STORAGE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (VideoPresentation presentation : mVideoPresentations) {
			presentation.destroy();
		}
		mVideoPresentations.clear();
		mVideoPresentations = null;
	}


}
