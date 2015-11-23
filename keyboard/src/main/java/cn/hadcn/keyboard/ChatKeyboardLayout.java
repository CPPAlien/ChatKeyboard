package cn.hadcn.keyboard;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import cn.hadcn.keyboard.emoticon.EmoticonBean;
import cn.hadcn.keyboard.emoticon.EmoticonLayout;
import cn.hadcn.keyboard.emoticon.EmoticonsToolBarView;
import cn.hadcn.keyboard.media.MediaBean;
import cn.hadcn.keyboard.media.MediaLayout;
import cn.hadcn.keyboard.utils.EmoticonsKeyboardBuilder;
import cn.hadcn.keyboard.utils.Utils;
import cn.hadcn.keyboard.view.HadEditText;
import cn.hadcn.keyboard.view.SoftHandleLayout;


public class ChatKeyboardLayout extends SoftHandleLayout implements EmoticonsToolBarView.OnToolBarItemClickListener {

    public int FUNC_EMOTICON_POS = 0; //display emoticons area
    public int FUNC_MEDIA_POS = 0;    //display medias area
    public int FUNC_ORDER_COUNT = 0;

    public int mChildViewPosition = -1;
    
    private HadEditText et_chat;
    private RelativeLayout rl_input;
    private LinearLayout ly_foot_func;
    private ImageView btn_face;
    private ImageView btn_multimedia;
    private Button btn_send;
    private Button btnRecording;
    private ImageView btn_voice_or_text;
    private Context mContext;

    private boolean mIsMultimediaVisibility = true;

    public ChatKeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_keyboardbar, this);
        initView();
    }

    private void initView() {
        rl_input = (RelativeLayout) findViewById(R.id.rl_input);
        ly_foot_func = (LinearLayout) findViewById(R.id.ly_foot_func);
        btn_face = (ImageView) findViewById(R.id.btn_face);
        btn_voice_or_text = (ImageView) findViewById(R.id.btn_voice_or_text);
        btnRecording = (Button) findViewById(R.id.bar_recording);
        btn_multimedia = (ImageView) findViewById(R.id.btn_multimedia);
        btn_send = (Button) findViewById(R.id.btn_send);
        et_chat = (HadEditText) findViewById(R.id.et_chat);

        setAutoHeightLayoutView(ly_foot_func);
        btn_voice_or_text.setOnClickListener(new VoiceTextClickListener());
        btn_multimedia.setOnClickListener(new MediaClickListener());
        btn_face.setOnClickListener(new FaceClickListener());
        btn_send.setOnClickListener(new SendClickListener());
        btnRecording.setOnTouchListener(new RecordingTouchListener());

        et_chat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!et_chat.isFocused()) {
                    et_chat.setFocusable(true);
                    et_chat.setFocusableInTouchMode(true);
                }
                return false;
            }
        });
        et_chat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    setEditableState(true);
                } else {
                    setEditableState(false);
                }
            }
        });
        et_chat.setOnTextChangedInterface(new HadEditText.OnTextChangedInterface() {
            @Override
            public void onTextChanged(CharSequence arg0) {
                String str = arg0.toString();
                if (TextUtils.isEmpty(str)) {
                    if(mIsMultimediaVisibility){
                        btn_multimedia.setVisibility(VISIBLE);
                        btn_send.setVisibility(GONE);
                    }
                    else{
                        btn_send.setBackgroundResource(R.drawable.btn_send_bg_disable);
                    }
                }
                else {
                    if(mIsMultimediaVisibility){
                        btn_multimedia.setVisibility(GONE);
                        btn_send.setVisibility(VISIBLE);
                        btn_send.setBackgroundResource(R.drawable.btn_send_bg);
                    }
                    else{
                        btn_send.setBackgroundResource(R.drawable.btn_send_bg);
                    }
                }
            }
        });
    }

    private void setEditableState(boolean b) {
        if (b) {
            et_chat.setFocusable(true);
            et_chat.setFocusableInTouchMode(true);
            et_chat.requestFocus();
            rl_input.setBackgroundResource(R.drawable.input_bg_green);
        } else {
            et_chat.setFocusable(false);
            et_chat.setFocusableInTouchMode(false);
            rl_input.setBackgroundResource(R.drawable.input_bg_gray);
        }
    }

    public HadEditText getInputArea() {
        return et_chat;
    }

    public void clearInputArea(){
        if(et_chat != null){
            et_chat.setText("");
        }
    }

    public void del(){
        if(et_chat != null){
            int action = KeyEvent.ACTION_DOWN;
            int code = KeyEvent.KEYCODE_DEL;
            KeyEvent event = new KeyEvent(action, code);
            et_chat.onKeyDown(KeyEvent.KEYCODE_DEL, event);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (ly_foot_func != null && ly_foot_func.isShown()) {
                    hideAutoView();
                    btn_face.setImageResource(R.drawable.icon_face_nomal);
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }

    private class SendClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            if(mOnChatKeyBoardListener != null){
                mOnChatKeyBoardListener.onSendBtnClick(et_chat.getText().toString());
            }
        }
    }

    private class VoiceTextClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            if ( rl_input.isShown() ) {
                hideAutoView();
                closeSoftKeyboard(et_chat);
                rl_input.setVisibility(GONE);
                btnRecording.setVisibility(VISIBLE);
                btn_voice_or_text.setImageResource(R.drawable.keyboard_icon);
            } else {
                rl_input.setVisibility(VISIBLE);
                btnRecording.setVisibility(GONE);
                setEditableState(true);
                openSoftKeyboard(et_chat);
                btn_voice_or_text.setImageResource(R.drawable.recording_icon);
            }
        }
    }

    private class FaceClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            switch (mKeyboardState){
                case KEYBOARD_STATE_BOTH:
                    closeSoftKeyboard(et_chat);
                    show(FUNC_EMOTICON_POS);
                    btn_face.setImageResource(R.drawable.icon_face_pop);
                    break;
                case KEYBOARD_STATE_NONE:
                    btn_face.setImageResource(R.drawable.icon_face_pop);
                    et_chat.setFocusableInTouchMode(true);
                    et_chat.requestFocus();
                    showAutoView();
                    show(FUNC_EMOTICON_POS);
                    break;
                case KEYBOARD_STATE_FUNC:
                    if ( mChildViewPosition == FUNC_EMOTICON_POS ) {
                        btn_face.setImageResource(R.drawable.icon_face_nomal);
                        openSoftKeyboard(et_chat);
                    } else {
                        show(FUNC_EMOTICON_POS);
                        btn_face.setImageResource(R.drawable.icon_face_pop);
                    }
                    break;
            }
        }
    }

    private class MediaClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            switch (mKeyboardState){
                case KEYBOARD_STATE_BOTH:
                    closeSoftKeyboard(et_chat);
                    show(FUNC_MEDIA_POS);
                    break;
                case KEYBOARD_STATE_NONE:
                    btn_face.setImageResource(R.drawable.icon_face_nomal);
                    rl_input.setVisibility(VISIBLE);
                    btnRecording.setVisibility(GONE);
                    btn_voice_or_text.setImageResource(R.drawable.recording_icon);
                    showAutoView();
                    show(FUNC_MEDIA_POS);
                    break;
                case KEYBOARD_STATE_FUNC:
                    btn_face.setImageResource(R.drawable.icon_face_nomal);
                    if(mChildViewPosition == FUNC_MEDIA_POS){
                        openSoftKeyboard(et_chat);
                    } else {
                        show(FUNC_MEDIA_POS);
                    }
                    break;
            }
        }
    }

    private class RecordingTouchListener implements OnTouchListener {
        float startY;
        float endY;
        boolean isCanceled = false;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if ( motionEvent.getAction() == MotionEvent.ACTION_DOWN ) {
                startY = motionEvent.getRawY();
                btnRecording.setText(getResources().getString(R.string.recording_end));
                btnRecording.setBackgroundResource(R.drawable.recording_p);
                if ( mOnChatKeyBoardListener != null ) {
                    mOnChatKeyBoardListener.onRecordingAction(RecordingAction.START );
                }
            } else if ( motionEvent.getAction() == MotionEvent.ACTION_UP ) {
                btnRecording.setText(getResources().getString(R.string.recording_start));
                btnRecording.setBackgroundResource(R.drawable.recording_n);
                if ( mOnChatKeyBoardListener != null && !isCanceled ) {
                    mOnChatKeyBoardListener.onRecordingAction(RecordingAction.COMPLETE);
                } else if ( mOnChatKeyBoardListener != null && isCanceled ) {
                    mOnChatKeyBoardListener.onRecordingAction(RecordingAction.CANCELED);
                }
            } else if ( motionEvent.getAction() == MotionEvent.ACTION_MOVE ) {
                //todo the num can be set by up layer
                endY = motionEvent.getRawY();
                if ( startY - endY > Utils.dip2px(mContext, 50)) {
                    btnRecording.setText(getResources().getString(R.string.recording_cancel));
                    isCanceled = true;
                } else {
                    btnRecording.setText(getResources().getString(R.string.recording_end));
                    isCanceled = false;
                }
            }
            return false;
        }
    }

    @Override
    public void OnSoftKeyboardPop(int height) {
        super.OnSoftKeyboardPop(height);
        btn_face.setImageResource(R.drawable.icon_face_nomal);
    }

    public void setMediaContents( List<MediaBean> mediaContents ){
        MediaLayout mediaLayout = new MediaLayout(mContext);
        mediaLayout.setContents(mediaContents);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ly_foot_func.addView(mediaLayout, params);
        FUNC_MEDIA_POS = FUNC_ORDER_COUNT;
        ++FUNC_ORDER_COUNT;
    }

    public void setEmoticonContents(EmoticonsKeyboardBuilder builder) {
        EmoticonLayout layout = new EmoticonLayout(mContext);
        layout.setContents(builder, new EmoticonLayout.OnEmoticonListener() {
            @Override
            public void onEmoticonItemClicked(EmoticonBean bean) {
                if (et_chat != null) {
                    et_chat.setFocusable(true);
                    et_chat.setFocusableInTouchMode(true);
                    et_chat.requestFocus();

                    if (bean.getEventType() == EmoticonBean.FACE_TYPE_DEL) {
                        int action = KeyEvent.ACTION_DOWN;
                        int code = KeyEvent.KEYCODE_DEL;
                        KeyEvent event = new KeyEvent(action, code);
                        et_chat.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                        return;
                    } else if (bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF) {
                        return;
                    }

                    int index = et_chat.getSelectionStart();
                    Editable editable = et_chat.getEditableText();
                    if (index < 0) {
                        editable.append(bean.getContent());
                    } else {
                        editable.insert(index, bean.getContent());
                    }
                }
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ly_foot_func.addView(layout, params);
        FUNC_EMOTICON_POS = FUNC_ORDER_COUNT;
        ++FUNC_ORDER_COUNT;
    }

    public void show(int position){
        int childCount = ly_foot_func.getChildCount();
        if(position < childCount){
            for(int i = 0 ; i < childCount ; i++){
                if(i == position){
                    ly_foot_func.getChildAt(i).setVisibility(VISIBLE);
                    mChildViewPosition  = i;
                } else{
                    ly_foot_func.getChildAt(i).setVisibility(GONE);
                }
            }
        }
    }

    OnChatKeyBoardListener mOnChatKeyBoardListener;
    public void setOnKeyBoardBarListener( OnChatKeyBoardListener l ) { this.mOnChatKeyBoardListener = l; }

    @Override
    public void onToolBarItemClick(int position) {

    }

    public interface OnChatKeyBoardListener {
        void onSendBtnClick(String msg);
        void onRecordingAction(RecordingAction action);
    }

    public enum RecordingAction {
        START,
        COMPLETE,
        CANCELED
    }
}