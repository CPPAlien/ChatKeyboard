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
import cn.hadcn.keyboard.emoticon.view.EmoticonLayout;
import cn.hadcn.keyboard.emoticon.view.EmoticonsToolBarView;
import cn.hadcn.keyboard.media.MediaBean;
import cn.hadcn.keyboard.media.MediaLayout;
import cn.hadcn.keyboard.emoticon.util.EmoticonsKeyboardBuilder;
import cn.hadcn.keyboard.utils.Utils;
import cn.hadcn.keyboard.view.HadEditText;
import cn.hadcn.keyboard.view.SoftHandleLayout;


public class ChatKeyboardLayout extends SoftHandleLayout implements EmoticonsToolBarView.OnToolBarItemClickListener {

    public int FUNC_EMOTICON_POS = 0; //display emoticons area
    public int FUNC_MEDIA_POS = 0;    //display medias area
    public int FUNC_ORDER_COUNT = 0;

    public int mChildViewPosition = -1;
    
    private HadEditText etInputArea;
    private RelativeLayout rl_input;
    private LinearLayout lyBottomLayout;
    private ImageView btnEmoticon;
    private ImageView btnMedia;
    private Button btnSend;
    private Button btnRecording;
    private ImageView btnVoiceOrText;
    private Context mContext;
    private boolean isShowMediaButton = false;   //media func button on or off

    public ChatKeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_keyboardbar, this);
        initView();
    }

    private void initView() {
        rl_input = (RelativeLayout) findViewById(R.id.rl_input);
        lyBottomLayout = (LinearLayout) findViewById(R.id.ly_foot_func);
        btnEmoticon = (ImageView) findViewById(R.id.btn_face);
        btnVoiceOrText = (ImageView) findViewById(R.id.btn_voice_or_text);
        btnRecording = (Button) findViewById(R.id.bar_recording);
        btnMedia = (ImageView) findViewById(R.id.btn_multimedia);
        btnSend = (Button) findViewById(R.id.btn_send);
        etInputArea = (HadEditText) findViewById(R.id.et_chat);

        setAutoHeightLayoutView(lyBottomLayout);
        btnVoiceOrText.setOnClickListener(new VoiceTextClickListener());
        btnMedia.setOnClickListener(new MediaClickListener());
        btnMedia.setVisibility(GONE);
        btnEmoticon.setOnClickListener(new FaceClickListener());
        btnEmoticon.setVisibility(GONE);
        btnSend.setOnClickListener(new SendClickListener());
        btnRecording.setOnTouchListener(new RecordingTouchListener());

        etInputArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!etInputArea.isFocused()) {
                    etInputArea.setFocusable(true);
                    etInputArea.setFocusableInTouchMode(true);
                }
                return false;
            }
        });
        etInputArea.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    setEditableState(true);
                } else {
                    setEditableState(false);
                }
            }
        });
        etInputArea.setOnTextChangedInterface(new HadEditText.OnTextChangedInterface() {
            @Override
            public void onTextChanged(CharSequence arg0) {
                if ( !isShowMediaButton ) {
                    return;
                }
                String str = arg0.toString();
                if (TextUtils.isEmpty(str)) {
                    btnMedia.setVisibility(VISIBLE);
                    btnSend.setVisibility(GONE);
                } else {
                    btnMedia.setVisibility(GONE);
                    btnSend.setVisibility(VISIBLE);
                }
            }
        });
    }

    private void setEditableState(boolean b) {
        if (b) {
            etInputArea.setFocusable(true);
            etInputArea.setFocusableInTouchMode(true);
            etInputArea.requestFocus();
            rl_input.setBackgroundResource(R.drawable.input_bg_green);
        } else {
            etInputArea.setFocusable(false);
            etInputArea.setFocusableInTouchMode(false);
            rl_input.setBackgroundResource(R.drawable.input_bg_gray);
        }
    }

    public HadEditText getInputArea() {
        return etInputArea;
    }

    public void clearInputArea(){
        etInputArea.setText("");
    }

    public void del(){
        if(etInputArea != null){
            int action = KeyEvent.ACTION_DOWN;
            int code = KeyEvent.KEYCODE_DEL;
            KeyEvent event = new KeyEvent(action, code);
            etInputArea.onKeyDown(KeyEvent.KEYCODE_DEL, event);
        }
    }

    /**
     * hide keyboard or emoticon area or media area
     */
    public void hideBottomPop() {
        hideAutoView();
        btnEmoticon.setImageResource(R.drawable.icon_face_nomal);
        closeSoftKeyboard(etInputArea);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (lyBottomLayout != null && lyBottomLayout.isShown()) {
                    hideAutoView();
                    btnEmoticon.setImageResource(R.drawable.icon_face_nomal);
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
                mOnChatKeyBoardListener.onSendBtnClick(etInputArea.getText().toString());
            }
        }
    }

    private class VoiceTextClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            if ( rl_input.isShown() ) {
                // switch to voice recording bar
                hideAutoView();
                closeSoftKeyboard(etInputArea);
                rl_input.setVisibility(GONE);
                btnRecording.setVisibility(VISIBLE);
                btnVoiceOrText.setImageResource(R.drawable.keyboard_icon);
                btnSend.setVisibility(GONE);
                if ( isShowMediaButton ) {
                    btnMedia.setVisibility(VISIBLE);
                }
            } else {
                // switch to text input bar
                rl_input.setVisibility(VISIBLE);
                btnRecording.setVisibility(GONE);
                setEditableState(true);
                openSoftKeyboard(etInputArea);
                btnVoiceOrText.setImageResource(R.drawable.recording_icon);
                if ( !TextUtils.isEmpty(etInputArea.getText().toString()) ) {
                    btnMedia.setVisibility(GONE);
                    btnSend.setVisibility(VISIBLE);
                }
                if ( !isShowMediaButton ) {    //if media button not be shown, show button send every time
                    btnSend.setVisibility(VISIBLE);
                }
            }
        }
    }

    private class FaceClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            switch (mKeyboardState){
                case KEYBOARD_STATE_BOTH:
                    closeSoftKeyboard(etInputArea);
                    show(FUNC_EMOTICON_POS);
                    btnEmoticon.setImageResource(R.drawable.icon_face_pop);
                    break;
                case KEYBOARD_STATE_NONE:
                    btnEmoticon.setImageResource(R.drawable.icon_face_pop);
                    etInputArea.setFocusableInTouchMode(true);
                    etInputArea.requestFocus();
                    showAutoView();
                    show(FUNC_EMOTICON_POS);
                    break;
                case KEYBOARD_STATE_FUNC:
                    if ( mChildViewPosition == FUNC_EMOTICON_POS ) {
                        btnEmoticon.setImageResource(R.drawable.icon_face_nomal);
                        openSoftKeyboard(etInputArea);
                    } else {
                        show(FUNC_EMOTICON_POS);
                        btnEmoticon.setImageResource(R.drawable.icon_face_pop);
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
                    closeSoftKeyboard(etInputArea);
                    show(FUNC_MEDIA_POS);
                    break;
                case KEYBOARD_STATE_NONE:
                    btnEmoticon.setImageResource(R.drawable.icon_face_nomal);
                    rl_input.setVisibility(VISIBLE);
                    btnRecording.setVisibility(GONE);
                    btnVoiceOrText.setImageResource(R.drawable.recording_icon);
                    etInputArea.setFocusableInTouchMode(true);
                    etInputArea.requestFocus();
                    showAutoView();
                    show(FUNC_MEDIA_POS);
                    break;
                case KEYBOARD_STATE_FUNC:
                    btnEmoticon.setImageResource(R.drawable.icon_face_nomal);
                    if(mChildViewPosition == FUNC_MEDIA_POS){
                        openSoftKeyboard(etInputArea);
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
        btnEmoticon.setImageResource(R.drawable.icon_face_nomal);
    }

    public void showMedias(List<MediaBean> mediaContents) {
        btnMedia.setVisibility(VISIBLE);
        btnSend.setVisibility(GONE);
        isShowMediaButton = true;
        MediaLayout mediaLayout = new MediaLayout(mContext);
        mediaLayout.setContents(mediaContents);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lyBottomLayout.addView(mediaLayout, params);
        FUNC_MEDIA_POS = FUNC_ORDER_COUNT;
        ++FUNC_ORDER_COUNT;
    }

    public void showEmoticons( ) {
        btnEmoticon.setVisibility(VISIBLE);
        EmoticonsKeyboardBuilder builder = EmoticonsUtils.getBuilder(mContext);
        EmoticonLayout layout = new EmoticonLayout(mContext);
        layout.setContents(builder, new EmoticonLayout.OnEmoticonListener() {
            @Override
            public void onEmoticonItemClicked(EmoticonBean bean) {
                if (etInputArea != null) {
                    etInputArea.setFocusable(true);
                    etInputArea.setFocusableInTouchMode(true);
                    etInputArea.requestFocus();

                    if (bean.getEventType() == EmoticonBean.FACE_TYPE_DEL) {
                        int action = KeyEvent.ACTION_DOWN;
                        int code = KeyEvent.KEYCODE_DEL;
                        KeyEvent event = new KeyEvent(action, code);
                        etInputArea.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                        return;
                    } else if (bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF) {
                        mOnChatKeyBoardListener.onUserDefEmoticonClicked(bean.getTag(), bean.getIconUri());
                        return;
                    }

                    int index = etInputArea.getSelectionStart();
                    Editable editable = etInputArea.getEditableText();
                    if (index < 0) {
                        editable.append(bean.getTag());
                    } else {
                        editable.insert(index, bean.getTag());
                    }
                }
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lyBottomLayout.addView(layout, params);
        FUNC_EMOTICON_POS = FUNC_ORDER_COUNT;
        ++FUNC_ORDER_COUNT;
    }

    public void show(int position){
        int childCount = lyBottomLayout.getChildCount();
        if(position < childCount){
            for(int i = 0 ; i < childCount ; i++){
                if(i == position){
                    lyBottomLayout.getChildAt(i).setVisibility(VISIBLE);
                    mChildViewPosition  = i;
                } else{
                    lyBottomLayout.getChildAt(i).setVisibility(GONE);
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
        void onUserDefEmoticonClicked(String name, String uri);
    }

    public enum RecordingAction {
        START,
        COMPLETE,
        CANCELED
    }
}