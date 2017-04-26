package com.excellence.mediaplayer;

import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener, View.OnClickListener
{
	private static final long TRAVEL_TIME = 5 * 1000;
	private static final String TAG = MainActivity.class.getSimpleName();

	private SurfaceView mSurfaceView = null;
	// SurfaceHolder��SurfaceView�Ŀ��ƽӿ�
	private SurfaceHolder mSurfaceHolder = null;
	private MediaPlayer mMediaPlayer = null;
	private PopupWindow mControlWindow = null;
	private Button mBackBtn = null;
	private Button mStartBtn = null;
	private Button mNextBtn = null;

	private int position = 0;
	private boolean isFirstTravel = true;
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
		initControlWindow();
	}

	private void initControlWindow()
	{
		if (mControlWindow == null)
		{
			View controlView = LayoutInflater.from(this).inflate(R.layout.controller_layout, null);
			mControlWindow = new PopupWindow(controlView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
			mControlWindow.setBackgroundDrawable(new BitmapDrawable());
			mControlWindow.setAnimationStyle(R.style.pop_anim);
			mControlWindow.setFocusable(true);
			mControlWindow.setOutsideTouchable(true);

			mBackBtn = (Button) controlView.findViewById(R.id.back_btn);
			mStartBtn = (Button) controlView.findViewById(R.id.start_btn);
			mNextBtn = (Button) controlView.findViewById(R.id.next_btn);
			mStartBtn.requestFocus();

			mStartBtn.setOnClickListener(this);
			mBackBtn.setOnClickListener(this);
			mNextBtn.setOnClickListener(this);
		}
	}

	private void initView()
	{
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);

		// SurfaceHolder��SurfaceView�Ŀ��ƽӿ�
		mSurfaceHolder = mSurfaceView.getHolder();
		// ���ò���ʱ����Ļ
		mSurfaceHolder.setKeepScreenOn(true);
		mSurfaceHolder.addCallback(this);
		// ��ʾ�ķֱ���,������Ϊ��ƵĬ��
		// mSurfaceHolder.setFixedSize(320, 220);
		// Surface����
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void onClick(View v)
	{
		if (mMediaPlayer == null)
			return;

		switch (v.getId())
		{
		case R.id.start_btn:
			if (mMediaPlayer.isPlaying())
			{
				mMediaPlayer.pause();
				mStartBtn.setText("start");
			}
			else
			{
				mMediaPlayer.start();
				mStartBtn.setText("pause");
			}
			break;

		case R.id.next_btn:
			mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + 5 * 1000 > mMediaPlayer.getDuration() ? mMediaPlayer.getDuration() - 100 : mMediaPlayer.getCurrentPosition() + 5 * 1000);
			if (!mMediaPlayer.isPlaying())
			{
				mMediaPlayer.start();
				mMediaPlayer.pause();
			}
			break;

		case R.id.back_btn:
			mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - 5 * 1000 < 0 ? 0 : mMediaPlayer.getCurrentPosition() - 5 * 1000);
			if (!mMediaPlayer.isPlaying())
			{
				// ��ͣ��ѡ�����
				mMediaPlayer.start();
				mMediaPlayer.pause();
			}
			break;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		if (position == 0)
		{
			play();
		}
	}

	private void play()
	{
		// ������surface��������ܳ�ʼ��MediaPlayer,���򲻻���ʾͼ��
		try
		{
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnInfoListener(this);
			mMediaPlayer.setOnErrorListener(this);

			mMediaPlayer.reset();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setDataSource("/sdcard/434661.mkv");
			// �Ƿ�ѭ��
			mMediaPlayer.setLooping(true);
			// ������ʾ��Ƶ��ʾ��SurfaceView��
			mMediaPlayer.setDisplay(mSurfaceHolder);
			// mMediaPlayer.prepare();
			mMediaPlayer.prepareAsync();
			mControlWindow.showAtLocation(mSurfaceView, Gravity.BOTTOM, 0, 0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{

	}

	private Runnable hideOrShowMenu = new Runnable()
	{
		@Override
		public void run()
		{
			if (mControlWindow.isShowing())
				mControlWindow.dismiss();
			else
				mControlWindow.showAtLocation(mControlWindow.getContentView(), Gravity.BOTTOM, 0, 0);
		}
	};

	@Override
	protected void onResume()
	{
		super.onResume();
		if (!isFirstTravel)
		{
			/**
			 * ����start/pause
			 */
			try
			{
				// mMediaPlayer.prepare();
				mMediaPlayer.prepareAsync();
				mStartBtn.setText("start");
				mMediaPlayer.seekTo(position);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		isFirstTravel = false;
		if (mMediaPlayer.isPlaying())
		{
			// ��ǰ����λ��
			position = mMediaPlayer.getCurrentPosition();
			mMediaPlayer.stop();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (mMediaPlayer.isPlaying())
			mMediaPlayer.stop();
		// Activity����ʱֹͣ���ţ��ͷ���Դ�����������������ʹ�˳�������������Ƶ���ŵ�����
		mMediaPlayer.release();

		if (mSurfaceHolder != null)
			mSurfaceHolder.getSurface().release();
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
		if (mControlWindow != null)
			mControlWindow.dismiss();
		finish();
		return true;
	}

}
