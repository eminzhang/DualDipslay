package com.via.blending.demo;

import android.media.MediaPlayer;
import android.view.SurfaceHolder;

/**
 * Created by eminzhang    2018/8/3
 */
public class CustomMediaPlayer extends MediaPlayer implements MediaPlayer.OnPreparedListener,SurfaceHolder.Callback{



    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
