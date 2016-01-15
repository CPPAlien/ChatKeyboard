package cn.hadcn.keyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * RecordingLayout
 * Created by 90Chris on 2015/12/2.
 */
public class RecordingLayout extends RelativeLayout{
    LinearLayout llRecordingStart;
    ImageView ivRecordingCancel;
    ImageView ivVoiceLevel;
    ProgressBar pbLoading;
    TextView tvNotify;
    int mCurrentVoiceLevel = 0;
    List<Runnable> levelActions = new ArrayList<>();

    public RecordingLayout(Context context) {
        super(context);
        init(context);
    }

    public RecordingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecordingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.had_recording_view, this);
        llRecordingStart = (LinearLayout)findViewById(R.id.had_recording_start);
        ivRecordingCancel = (ImageView)findViewById(R.id.had_recording_cancel);
        pbLoading = (ProgressBar)findViewById(R.id.had_recording_loading);
        tvNotify = (TextView)findViewById(R.id.had_recording_notify);
        ivVoiceLevel = (ImageView)findViewById(R.id.had_recording_level);
    }

    public void hide() {
        setVisibility(GONE);
    }

    /**
     * show recording area
     * @param state 0 for canceling state, other for recording state
     */
    public void show( int state ) {
        setVisibility(VISIBLE);
        if ( state == 0 ) {
            pbLoading.setVisibility(GONE);
            llRecordingStart.setVisibility(GONE);
            ivRecordingCancel.setVisibility(VISIBLE);
            tvNotify.setText(getResources().getString(R.string.recording_cancel));
            tvNotify.setBackgroundResource(R.drawable.recording_text_bg);
        } else if ( state == 1 ) {
            pbLoading.setVisibility(GONE);
            llRecordingStart.setVisibility(VISIBLE);
            ivRecordingCancel.setVisibility(GONE);
            tvNotify.setText(getResources().getString(R.string.recording_cancel_notice));
            tvNotify.setBackgroundResource(R.drawable.transparent_bg);
        } else {
            pbLoading.setVisibility(VISIBLE);
            llRecordingStart.setVisibility(GONE);
            ivRecordingCancel.setVisibility(GONE);
            tvNotify.setText(getResources().getString(R.string.recording_cancel_notice));
            tvNotify.setBackgroundResource(R.drawable.transparent_bg);
        }
    }

    public void setVoiceLevel( int level ) {
        //if set voice again, cancel actions already in queue
        if ( level == mCurrentVoiceLevel ) {
            return;
        }
        for ( Runnable r : levelActions ) {
            removeCallbacks(r);
        }
        levelActions.clear();

        if ( mCurrentVoiceLevel > level ) {
            for ( int i = mCurrentVoiceLevel; i >= level; --i) {
                levelActions.add(new LevelAction(i));
            }
        } else {
            for ( int i = mCurrentVoiceLevel; i <= level; ++i) {
                levelActions.add(new LevelAction(i));
            }
        }
        for ( int i = 0; i < levelActions.size(); ++i ) {
            postDelayed(levelActions.get(i), 10 * (i + 1));
        }
    }

    private class LevelAction implements Runnable {
        int level;

        public LevelAction(int level) {
            this.level = level;
        }

        @Override
        public void run() {
            ivVoiceLevel.setImageResource(chooseLevelDrawable(level));
            mCurrentVoiceLevel = level;
        }
    }

    private int chooseLevelDrawable( int level ) {
        switch ( level ) {
            case 0:
                return R.drawable.recording_level_1;
            case 1:
                return R.drawable.recording_level_2;
            case 2:
                return R.drawable.recording_level_3;
            case 3:
                return R.drawable.recording_level_4;
            case 4:
                return R.drawable.recording_level_5;
            case 5:
                return R.drawable.recording_level_6;
            default:
                return R.drawable.recording_level_7;
        }
    }
}
