package com.excellence.netmediaplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener,
		MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener
{
	private static final String TAG = MainActivity.class.getSimpleName();

	private MediaPlayer mMediaPlayer = null;
	private SurfaceHolder mSurfaceHolder = null;
	@BindView(R.id.surfaceView)
	SurfaceView mSurfaceView;
	@BindView(R.id.back_btn)
	Button mBackBtn;
	@BindView(R.id.start_btn)
	Button mStartBtn;
	@BindView(R.id.next_btn)
	Button mNextBtn;
	@BindView(R.id.buffering_layout)
	RelativeLayout mBufferingLayout;
	@BindView(R.id.buffering_progress)
	ProgressBar mBufferingProgress;
	@BindView(R.id.buffering_text)
	TextView mBufferingText;

	private int mLastPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);
		init();
	}

	private void init()
	{
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.setKeepScreenOn(true);
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.setFixedSize(1280, 720);
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnBufferingUpdateListener(this);
		mMediaPlayer.setOnInfoListener(this);
		mMediaPlayer.setOnErrorListener(this);
		mMediaPlayer.setOnCompletionListener(this);
	}

	@OnClick({ R.id.back_btn, R.id.start_btn, R.id.next_btn })
	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.back_btn:
			break;
		case R.id.start_btn:
			if (mMediaPlayer.isPlaying())
			{
				mMediaPlayer.pause();
			}
			else
				mMediaPlayer.start();
			break;
		case R.id.next_btn:
			break;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		if (mLastPosition == 0)
		{
			try
			{
				play();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void play() throws Exception
	{
		mMediaPlayer.reset();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mMediaPlayer.setLooping(true);
		// mMediaPlayer.setDataSource("/sdcard/MVaux41A4lkuWloBbGUGaQ__.mp4");
//		mMediaPlayer.setDataSource("http://dlqncdn.miaopai.com/stream/MVaux41A4lkuWloBbGUGaQ__.mp4");
		mMediaPlayer.setDataSource("http://185.53.11.167:8081/MOV000880426093027?AuthInfo=e4069b146f8f4f1ff67df6054271be90adee55c20a77fe872e2329e66a52ab609920ea6e619e1f8ea684e39891e7f2f6a97e7561777da0a0cffd60cc89d556a3dd9f6e0649a2fc45e45e12d076e98697");
		mMediaPlayer.setDisplay(mSurfaceHolder);
		mMediaPlayer.prepareAsync();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{

	}

	@Override
	public void onPrepared(MediaPlayer mp)
	{
		mMediaPlayer.start();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (mMediaPlayer != null)
		{
			if (mMediaPlayer.isPlaying())
				mMediaPlayer.stop();
			mMediaPlayer.release();
		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent)
	{
		//?? 缓存进度还是播放进度 ??
		Log.e(TAG, "buffering..." + percent);
		mBufferingText.setText(percent + "%");
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra)
	{
		Log.e(TAG, "info : " + what + " *** " + extra);
		switch (what)
		{
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			mBufferingLayout.setVisibility(View.VISIBLE);
			if (mMediaPlayer.isPlaying())
				mMediaPlayer.pause();
			break;

		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			mBufferingLayout.setVisibility(View.GONE);
			if (!mMediaPlayer.isPlaying())
				mMediaPlayer.start();
			break;

		case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:// 当文件中的音频和视频数据不正确的交错时，将触发如下操作。
			// 在一个正确交错的媒体文件中，音频和视频样本依序排列，从而使得播放能够有效平稳的进行。
			Log.v(TAG, "MEDIA_INFO_BAD_INTERLEAVING extar is :" + extra);
			break;
		case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:// 当心的元数据可用时，将触发它，android
			// 2.0以上版本可用。
			Log.v(TAG, "MEDIA_INFO_METADATA_UPDATE extar is :" + extra);
			break;
		case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:// 媒体不能正确定位，意味着它可能是一个在线流
			Log.v(TAG, "MEDIA_INFO_NOT_SEEKABLE extar is :" + extra);
			break;
		case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:// 当无法播放视频时，可能是将要播放视频，但是视频太复杂
			Log.v(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING extar is :" + extra);
			break;
		case MediaPlayer.MEDIA_INFO_UNKNOWN:
			Log.v(TAG, "MEDIA_INFO_UNKNOWN extar is :" + extra);
			break;
		}
		return false;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra)
	{
		Log.e(TAG, "error : " + what + " *** " + extra);
		// 用于错误时，寻找进度重新播放
		if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED)
		{
			Log.v(TAG, "MEDIA_ERROR_SERVER_DIED");
			mMediaPlayer.reset();// 可调用此方法重置
		}
		else if (what == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK)
		{
			Log.v(TAG, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
		}
		else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN)
		{
			Log.v(TAG, "MEDIA_ERROR_UNKNOWN");
		}
		return false;// 返回false，表示错误没有被处理
	}

	@Override
	public void onCompletion(MediaPlayer mp)
	{
		Log.e(TAG, "complete playing");
	}
}
