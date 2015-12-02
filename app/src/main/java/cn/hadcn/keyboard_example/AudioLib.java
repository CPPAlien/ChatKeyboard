package cn.hadcn.keyboard_example;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;

import java.io.File;
import java.io.IOException;


/**
 * AudioLib
 * Created by 90Chris on 2015/12/2.
 */
public class AudioLib {
    final String TAG = getClass().getSimpleName();
    private static AudioLib sAudioLib = null;
    private MediaRecorder recorder;
    private String mPath;

    public static AudioLib getInstance() {
        if ( sAudioLib == null ) {
            sAudioLib = new AudioLib();
        }
        return sAudioLib;
    }

    public void start( String path, OnAudioListener listener ) {
        LogUtil.d(TAG, "start recording");
        mPath = path;
        mListener = listener;
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile( path );
        try {
            recorder.prepare();
            recorder.start();
            updateMicStatus();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "IllegalStateException");
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "IOException");
        }
    }

    /**
     * cancel, not save the file
     * @return true, cancel success, false, cancel failed
     */
    public boolean cancel() {
        LogUtil.d(TAG, "cancel recording");
        if ( recorder == null ) {
            return false;
        }

        recorder.stop();
        recorder.release();
        recorder = null;
        File file = new File(mPath);
        return file.exists() && file.delete();
    }

    /**
     * record success
     */
    public void stop() {
        LogUtil.d(TAG, "stop recording");
        if ( recorder == null ) {
            LogUtil.e(TAG, "recorder is not start yet");
            return;
        }
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    public String generatePath(Context context) {
        return getDiskCacheDir(context) + File.separator + System.currentTimeMillis() + ".amr";
    }

    private String getDiskCacheDir(Context context) {
        final String CACHE_DIR_NAME = "audioCache";
        final String cachePath = context.getCacheDir().getPath();
        //return cachePath + File.separator + CACHE_DIR_NAME;
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/360";
    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    private void updateMicStatus() {
        if (recorder != null) {
            double ratio = (double)recorder.getMaxAmplitude();
            double db = 0;
            if (ratio > 1)
                db = 20 * Math.log10(ratio);
            LogUtil.d(TAG, "decibel = " + db);
            if ( mListener != null ) {
                mListener.onDbChange(db);
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, 500);
        }
    }

    private OnAudioListener mListener = null;
    public interface OnAudioListener {
        void onDbChange(double db);
    }
}
