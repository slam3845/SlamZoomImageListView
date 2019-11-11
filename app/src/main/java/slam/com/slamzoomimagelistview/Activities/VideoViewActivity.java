package slam.com.slamzoomimagelistview.Activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import slam.com.slamzoomimagelistview.Data.HotelData;
import slam.com.slamzoomimagelistview.R;

/**
 * Created by slam on 07/08/2018.
 */
public class VideoViewActivity extends Activity {

	private class PlayerHandler implements MediaPlayer.OnPreparedListener,
										   MediaPlayer.OnSeekCompleteListener,
										   MediaPlayer.OnCompletionListener,
										   MediaPlayer.OnErrorListener
	{
		/**
		 * When the PLAY buton is clicked or _videoView.start() is called.
		 *
		 * @param mediaPlayer
		 */
		@Override
		public void onPrepared(final MediaPlayer mediaPlayer) {
			mediaPlayer.setLooping(_looping);
			_videoView.start();

			mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
				@Override
				public void onSeekComplete(MediaPlayer mediaPlayer) {
				}
			});
			mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
				@Override
				public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
					Log.d("PlayerHandler", "onInfo, what = " + what);
					switch (what) {
						case MediaPlayer.MEDIA_INFO_UNKNOWN: // = 1
						case MediaPlayer.MEDIA_INFO_AUDIO_NOT_PLAYING:		// = 804
						case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:       // = 800
						case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:           // = 801
						case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:   // = 901
						case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:    	// = 902
						case MediaPlayer.MEDIA_INFO_BUFFERING_START:        // = 701
						case MediaPlayer.MEDIA_INFO_BUFFERING_END:          // = 702
						case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:        // = 802
						case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:    // = 700
						case MediaPlayer.MEDIA_INFO_VIDEO_NOT_PLAYING:      // = 805
							break;

						case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: // = 3
							// video started; hide the placeholder.
							// View placeholder = (View) findViewById(R.id.placeholder);
							// placeholder.setVisibility(View.GONE);

							// When the PLAY buton is clicked or  _videoView.start() is called.
							return true;
					}
					return false;
				}
			});
		}

		@Override
		public boolean onError(MediaPlayer mediaPlayer, int arg1, int arg2) {
			return true;
		}

		@Override
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.stop();
      		finish();
		}

		@Override
		public void onSeekComplete(MediaPlayer mediaPlayer) {
			mediaPlayer.stop();
		}
	}

	private HotelData _hotelData;
	private String _urlVideoClip;
	private VideoView _videoView;
	private MediaController _mediaController;
	private boolean _looping = false;
	private int _position = 0;

	public VideoViewActivity() {
	}

	public VideoViewActivity(String urlVideoClip, boolean looping) {
		this._urlVideoClip = urlVideoClip;
		this._looping = looping;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		_hotelData = (HotelData)intent.getSerializableExtra("HotelData");
		_urlVideoClip = intent.getStringExtra("urlVideoClip");

		setContentView(R.layout.activity_videoview);
		_mediaController = new MediaController(this);

		_videoView = (VideoView) findViewById(R.id.video_view);
		_videoView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (_mediaController != null) {
						if (_mediaController.isShowing()) {
							_mediaController.hide();
						} else {
							_mediaController.show();
						}
					}
				}
				return true;
			}
		});
		_mediaController.setAnchorView(_videoView);

		PlayerHandler playerHandler = new PlayerHandler();
		_videoView.setOnPreparedListener(playerHandler);
		_videoView.setOnErrorListener(playerHandler);
		_videoView.setOnCompletionListener(playerHandler);
		_videoView.setMediaController(_mediaController);
	}


	@Override
	protected void onResume() {
		super.onResume();

		_videoView.setVideoURI(Uri.parse(_urlVideoClip));
		_videoView.requestFocus();
        _videoView.seekTo(_position);
//		_videoView.setZOrderOnTop(true);
      	_videoView.start();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		_position = _videoView.getCurrentPosition();
		savedInstanceState.putInt("Position", _position);
		// _videoView.pause();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		_position = savedInstanceState.getInt("Position");
	}
}
