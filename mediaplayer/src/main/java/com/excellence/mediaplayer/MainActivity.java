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

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, PopupWindow.OnDismissListener, View.OnKeyListener, MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener
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
			mControlWindow.setOnDismissListener(this);

			mBackBtn = (Button) controlView.findViewById(R.id.back_btn);
			mStartBtn = (Button) controlView.findViewById(R.id.start_btn);
			mNextBtn = (Button) controlView.findViewById(R.id.next_btn);
			mStartBtn.requestFocus();

			mStartBtn.setOnKeyListener(this);
			mBackBtn.setOnKeyListener(this);
			mNextBtn.setOnKeyListener(this);
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
	public boolean onKey(View v, int keyCode, KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_DOWN)
		{
			switch (keyCode)
			{
			case KeyEvent.KEYCODE_DPAD_CENTER:
				return clickEvent(v);
			}
		}
		return false;
	}

	private boolean clickEvent(View v)
	{
		if (mMediaPlayer == null)
			return true;

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
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		if (position == 0)
		{
			play();
			mMediaPlayer.seekTo(position);
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
			mMediaPlayer.setDataSource("/sdcard/dao.mp4");
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
	public void onDismiss()
	{

	}

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
