package com.excellence.videodemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener
{
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final String VIDEO_URL1 = "http://dlqncdn.miaopai.com/stream/MVaux41A4lkuWloBbGUGaQ__.mp4";
	private static final String VIDEO_URL2 = "http://185.53.11.167:8081/MOV000880426093027?AuthInfo=e4069b146f8f4f1ff67df6054271be90adee55c20a77fe872e2329e66a52ab609920ea6e619e1f8ea684e39891e7f2f6a97e7561777da0a0cffd60cc89d556a3dd9f6e0649a2fc45e45e12d076e98697";

	private VideoView mVideoView = null;
	private CustomMediaController controller = null;
	private String currentUrl = null;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mVideoView = (VideoView) findViewById(R.id.videoView);
		controller = new CustomMediaController(this);
		mVideoView.setMediaController(controller);
		currentUrl = VIDEO_URL1;
		mVideoView.setVideoPath(currentUrl);
		mVideoView.setKeepScreenOn(true);

		mVideoView.setOnInfoListener(this);
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnPreparedListener(this);
	}

	@Override
	public void onPrepared(MediaPlayer mp)
	{
		Log.e(TAG, "prepare");
		// mp.setLooping(true);
		mVideoView.start();
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra)
	{
		Log.e(TAG, "info ********* " + what + " ********** " + extra);
		return false;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra)
	{
		Log.e(TAG, "error ********* " + what + " ************* " + extra);
		finish();
		return true;
	}

	@Override
	public void onCompletion(MediaPlayer mp)
	{
		Log.e(TAG, "complete");
		if (currentUrl.equals(VIDEO_URL1))
			currentUrl = VIDEO_URL2;
		else
			currentUrl = VIDEO_URL1;
		mVideoView.setVideoPath(currentUrl);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

}
