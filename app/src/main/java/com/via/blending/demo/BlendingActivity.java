package com.via.blending.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


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
		verifyStoragePermissions(this);
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
