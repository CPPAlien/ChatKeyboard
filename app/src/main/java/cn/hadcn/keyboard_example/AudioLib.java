package cn.hadcn.keyboard_example;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * AudioLib
 * Created by 90Chris on 2015/12/2.
 */
public class AudioLib {
    private static final String TAG = "AudioLib";
    private static AudioLib sAudioLib = null;
    private MediaRecorder recorder;
    private String mPath;
    private int mPeriod = 0;
    private static final int MIN_LENGTH = 2;

    public static AudioLib getInstance() {
        if (sAudioLib == null) {
            sAudioLib = new AudioLib();
        }
        return sAudioLib;
    }

    public AudioLib() {
        new Timer().schedule(new AudioTimerTask(), 0, 1000);
    }

    private class AudioTimerTask extends TimerTask {
        @Override
        public void run() {
            ++mPeriod;
        }
    }

    public synchronized void start(String path, OnAudioListener listener) {
        LogUtil.d(TAG, "start recording");
        mPeriod = 0;

        mListener = listener;
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(path);

        try {
            recorder.prepare();
            recorder.start();
            updateMicStatus();
            LogUtil.d(TAG, "record start success");
        } catch (IllegalStateException e) {
            LogUtil.e(TAG, "IllegalStateException", e);
        } catch (IOException e) {
            LogUtil.e(TAG, "IOException", e);
        }

        mPath = path;
    }

    /**
     * cancel, not save the file
     *
     * @return true, cancel success, false, cancel failed
     */
    public synchronized boolean cancel() {
        LogUtil.d(TAG, "cancel recording");
        if (recorder == null) {
            LogUtil.e(TAG, "recorder is null ");
            return false;
        }
        try {
            stopRecord();
        } catch (IllegalStateException e) {
            LogUtil.e(TAG, "illegal state happened when cancel", e);
        }

        File file = new File(mPath);
        return file.exists() && file.delete();
    }

    /**
     * complete the recording
     *
     * @return recording last time
     */
    public synchronized int complete() {
        LogUtil.i(TAG, "complete recording");

        if (recorder == null) {
            LogUtil.e(TAG, "recorder is null ");
            return -1;
        }

        try {
            stopRecord();
        } catch (IllegalStateException e) {
            LogUtil.e(TAG, "illegal state happened when complete", e);
            return -1;
        }

        if (mPeriod < MIN_LENGTH) {
            LogUtil.i(TAG, "record time is too short");
            return -1;
        }

        return mPeriod;
    }

    public String generatePath(Context context) {
        boolean isSuccess = true;
        final String cachePath = context.getCacheDir().getAbsolutePath() + File.separator +
                "audioCache";
        File file = new File(cachePath);
        if (!file.exists()) {
            isSuccess = file.mkdirs();
        }
        if (isSuccess) {
            return cachePath + File.separator + System.currentTimeMillis() + ".amr";
        } else {
            return null;
        }
    }

    private synchronized void stopRecord() {
        //mHandler.removeCallbacks(mUpdateMicStatusTimer);

        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    private void updateMicStatus() {
        if (recorder != null) {
            double ratio = (double) recorder.getMaxAmplitude();
            double db = 0;
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
            }
            if (mListener != null) {
                mListener.onDbChange(db);
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, 500);
        }
    }

    private OnAudioListener mListener = null;

    public interface OnAudioListener {
        void onDbChange(double db);
    }

    private MediaPlayer mMediaPlayer = null;
    private String mCurrentPlayingAudioPath = null;
    private OnMediaPlayComplete mPlayListener = null;

    /**
     * play the audio
     *
     * @param path path of the audio file
     */
    public synchronized void playAudio(String path, OnMediaPlayComplete listener) {
        mPlayListener = listener;
        if (mMediaPlayer != null) {
            stopPlay();
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlay();
                if (mPlayListener != null) {
                    mPlayListener.onPlayComplete(true);
                }
            }
        });
        try {
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            mPlayListener.onPlayComplete(false);
        }

        mMediaPlayer.start();

        mCurrentPlayingAudioPath = path;
    }

    public synchronized void stopPlay() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
        mCurrentPlayingAudioPath = null;
    }

    public boolean isPlaying(String path) {
        return (mMediaPlayer != null) && mMediaPlayer.isPlaying() && (path.equals
                (mCurrentPlayingAudioPath));
    }

    public interface OnMediaPlayComplete {
        void onPlayComplete(boolean isSuccess);
    }
}
