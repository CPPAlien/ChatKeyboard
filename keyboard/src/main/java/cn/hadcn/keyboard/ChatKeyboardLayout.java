package cn.hadcn.keyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
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

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hadcn.keyboard.emoticon.EmoticonBean;
import cn.hadcn.keyboard.emoticon.EmoticonSetBean;
import cn.hadcn.keyboard.emoticon.db.EmoticonDBHelper;
import cn.hadcn.keyboard.emoticon.util.DefEmoticons;
import cn.hadcn.keyboard.emoticon.util.EmoticonHandler;
import cn.hadcn.keyboard.emoticon.util.EmoticonsKeyboardBuilder;
import cn.hadcn.keyboard.emoticon.view.EmoticonLayout;
import cn.hadcn.keyboard.emoticon.view.EmoticonsToolBarView;
import cn.hadcn.keyboard.media.MediaBean;
import cn.hadcn.keyboard.media.MediaLayout;
import cn.hadcn.keyboard.utils.EmoticonBase;
import cn.hadcn.keyboard.utils.HadLog;
import cn.hadcn.keyboard.utils.Utils;
import cn.hadcn.keyboard.view.HadEditText;
import cn.hadcn.keyboard.view.SoftHandleLayout;

public class ChatKeyboardLayout extends SoftHandleLayout implements EmoticonsToolBarView
        .OnToolBarItemClickListener {
    public int FUNC_EMOTICON_POS = 0; //display emoticons area
    public int FUNC_MEDIA_POS = 0;    //display medias area
    public int FUNC_ORDER_COUNT = 0;
    public int mChildViewPosition = -1;
    private HadEditText etInputArea;
    private RelativeLayout rlInput;
    private LinearLayout lyBottomLayout;
    private ImageView btnEmoticon;
    private ImageView btnMedia;
    private Button btnSend;
    private Button btnRecording;
    private ImageView btnVoiceOrText;
    private boolean isShowMediaButton = false;   //media func button on or off
    private boolean isLimitedOnlyText = false;
    private Drawable mBtnSendBg = null;

    public ChatKeyboardLayout(Context context) {
        super(context, null);
        initView(context);
    }

    public ChatKeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable
                .ChatKeyboardLayout);
        mBtnSendBg = typedArray.getDrawable(R.styleable.ChatKeyboardLayout_sendBtnBg);
        typedArray.recycle();
    }

    private void initView(Context context) {
        // must be before inflate
        EmoticonHandler.getInstance(context).loadEmoticonsToMemory();
        LayoutInflater.from(context).inflate(R.layout.view_keyboardbar, this);

        rlInput = (RelativeLayout) findViewById(R.id.rl_input);
        lyBottomLayout = (LinearLayout) findViewById(R.id.ly_foot_func);
        btnEmoticon = (ImageView) findViewById(R.id.btn_face);
        btnVoiceOrText = (ImageView) findViewById(R.id.btn_voice_or_text);
        btnRecording = (Button) findViewById(R.id.bar_recording);
        btnMedia = (ImageView) findViewById(R.id.btn_multimedia);
        btnSend = (Button) findViewById(R.id.btn_send);
        if (mBtnSendBg != null) {
            btnSend.setBackgroundDrawable(mBtnSendBg);
        }
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
                if (!isShowMediaButton || isLimitedOnlyText) {
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
            rlInput.setBackgroundResource(R.drawable.input_bg_green);
        } else {
            etInputArea.setFocusable(false);
            etInputArea.setFocusableInTouchMode(false);
            rlInput.setBackgroundResource(R.drawable.input_bg_gray);
        }
    }

    public Button getSendButton() {
        return btnSend;
    }

    public HadEditText getInputArea() {
        return etInputArea;
    }

    public void clearInputArea() {
        etInputArea.setText("");
    }

    public void del() {
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        etInputArea.onKeyDown(KeyEvent.KEYCODE_DEL, event);
    }

    public void hideLayout() {
        hideKeyboard();
        findViewById(R.id.keyboard_bar_id).setVisibility(GONE);
        mOnChatKeyBoardListener.onKeyboardHeightChanged(0);
    }

    public void showLayout() {
        findViewById(R.id.keyboard_bar_id).setVisibility(VISIBLE);
        int barHeight = getResources().getDimensionPixelOffset(R.dimen.keyboard_bar_height);
        mOnChatKeyBoardListener.onKeyboardHeightChanged(barHeight);
    }

    public boolean isLayoutVisible() {
        return VISIBLE == findViewById(R.id.keyboard_bar_id).getVisibility();
    }

    public void limitOnlyText() {
        btnEmoticon.setVisibility(GONE);
        btnMedia.setVisibility(GONE);
        btnSend.setVisibility(VISIBLE);
        etInputArea.setVisibility(VISIBLE);
        btnRecording.setVisibility(GONE);
        btnVoiceOrText.setVisibility(GONE);
        isLimitedOnlyText = true;
    }

    /**
     * hide soft keyboard
     */
    public void hideKeyboard() {
        hideAutoView();
        btnEmoticon.setImageResource(R.drawable.icon_face_nomal);
        closeSoftKeyboard(etInputArea);
    }

    /**
     * pop soft keyboard
     */
    public void popKeyboard() {
        showLayout();
        showAutoView();
        openSoftKeyboard(etInputArea);
    }

    public boolean isKeyboardPopped() {
        return mKeyboardState != KEYBOARD_STATE_NONE;
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
            if (mOnChatKeyBoardListener != null) {
                mOnChatKeyBoardListener.onSendBtnClick(etInputArea.getText().toString());
            }
        }
    }

    private class VoiceTextClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (rlInput.isShown()) {
                // switch to voice recording bar
                hideAutoView();
                closeSoftKeyboard(etInputArea);
                rlInput.setVisibility(GONE);
                btnRecording.setVisibility(VISIBLE);
                btnVoiceOrText.setImageResource(R.drawable.keyboard_icon);
                btnSend.setVisibility(GONE);
                if (isShowMediaButton) {
                    btnMedia.setVisibility(VISIBLE);
                }
            } else {
                // switch to text input bar
                rlInput.setVisibility(VISIBLE);
                btnRecording.setVisibility(GONE);
                setEditableState(true);
                openSoftKeyboard(etInputArea);
                btnVoiceOrText.setImageResource(R.drawable.recording_icon);
                if (!TextUtils.isEmpty(etInputArea.getText().toString())) {
                    btnMedia.setVisibility(GONE);
                    btnSend.setVisibility(VISIBLE);
                }
                if (!isShowMediaButton) {    //if media button not be shown, show button send
                    // every time
                    btnSend.setVisibility(VISIBLE);
                }
            }
        }
    }

    private class FaceClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            switch (mKeyboardState) {
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
                    if (mChildViewPosition == FUNC_EMOTICON_POS) {
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
            switch (mKeyboardState) {
                case KEYBOARD_STATE_BOTH:
                    closeSoftKeyboard(etInputArea);
                    show(FUNC_MEDIA_POS);
                    break;
                case KEYBOARD_STATE_NONE:
                    btnEmoticon.setImageResource(R.drawable.icon_face_nomal);
                    rlInput.setVisibility(VISIBLE);
                    btnRecording.setVisibility(GONE);
                    btnVoiceOrText.setImageResource(R.drawable.recording_icon);
                    etInputArea.setFocusableInTouchMode(true);
                    etInputArea.requestFocus();
                    showAutoView();
                    show(FUNC_MEDIA_POS);
                    break;
                case KEYBOARD_STATE_FUNC:
                    btnEmoticon.setImageResource(R.drawable.icon_face_nomal);
                    if (mChildViewPosition == FUNC_MEDIA_POS) {
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
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                startY = motionEvent.getRawY();
                btnRecording.setText(getResources().getString(R.string.recording_end));
                btnRecording.setBackgroundResource(R.drawable.recording_p);
                if (mOnChatKeyBoardListener != null) {
                    mOnChatKeyBoardListener.onRecordingAction(RecordingAction.START);
                }
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                btnRecording.setText(getResources().getString(R.string.recording_start));
                btnRecording.setBackgroundResource(R.drawable.recording_n);
                if (mOnChatKeyBoardListener != null && !isCanceled) {
                    mOnChatKeyBoardListener.onRecordingAction(RecordingAction.COMPLETE);
                } else if (mOnChatKeyBoardListener != null) {
                    mOnChatKeyBoardListener.onRecordingAction(RecordingAction.CANCELED);
                }
            } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                //todo the num can be set by up layer
                endY = motionEvent.getRawY();
                if (startY - endY > Utils.dip2px(mContext, 50)) {
                    btnRecording.setText(getResources().getString(R.string.recording_cancel));
                    isCanceled = true;
                    if (mOnChatKeyBoardListener != null) {
                        mOnChatKeyBoardListener.onRecordingAction(RecordingAction.WILLCANCEL);
                    }
                } else {
                    if (mOnChatKeyBoardListener != null) {
                        mOnChatKeyBoardListener.onRecordingAction(RecordingAction.RESTORE);
                    }
                    btnRecording.setText(getResources().getString(R.string.recording_end));
                    isCanceled = false;
                }
            }
            return false;
        }
    }

    public void showMedias(List<MediaBean> mediaContents) {
        btnMedia.setVisibility(VISIBLE);
        btnSend.setVisibility(GONE);
        isShowMediaButton = true;
        MediaLayout mediaLayout = new MediaLayout(mContext);
        mediaLayout.setContents(mediaContents);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lyBottomLayout.addView(mediaLayout, params);
        FUNC_MEDIA_POS = FUNC_ORDER_COUNT;
        ++FUNC_ORDER_COUNT;
    }

    public void showEmoticons() {
        btnEmoticon.setVisibility(VISIBLE);
        EmoticonsKeyboardBuilder builder = getBuilder(mContext);
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
                        if (mOnChatKeyBoardListener != null) {
                            mOnChatKeyBoardListener.onUserDefEmoticonClicked(bean.getTag(), bean
                                    .getIconUri());
                        }
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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lyBottomLayout.addView(layout, params);
        FUNC_EMOTICON_POS = FUNC_ORDER_COUNT;
        ++FUNC_ORDER_COUNT;
    }

    private void show(int position) {
        int childCount = lyBottomLayout.getChildCount();
        if (position < childCount) {
            for (int i = 0; i < childCount; i++) {
                if (i == position) {
                    lyBottomLayout.getChildAt(i).setVisibility(VISIBLE);
                    mChildViewPosition = i;
                } else {
                    lyBottomLayout.getChildAt(i).setVisibility(GONE);
                }
            }
        }
    }

    private OnChatKeyBoardListener mOnChatKeyBoardListener;

    public void setOnChatKeyBoardListener(OnChatKeyBoardListener l) {
        this.mOnChatKeyBoardListener = l;
    }

    @Override
    public void onToolBarItemClick(int position) {
        //
    }

    public static boolean isEmoticonInitSuccess(Context context) {
        return Utils.isInitDb(context);
    }

    public static void initEmoticonsDB(final Context context, final boolean isShowEmoji, final
    List<EmoticonEntity> emoticonEntities) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EmoticonDBHelper emoticonDbHelper = EmoticonHandler.getInstance(context)
                        .getEmoticonDbHelper();
                if (isShowEmoji) {
                    List<EmoticonBean> emojiArray = Utils.parseData(DefEmoticons.emojiArray,
                            EmoticonBean.FACE_TYPE_NORMAL, EmoticonBase.Scheme.DRAWABLE);
                    EmoticonSetBean emojiEmoticonSetBean = new EmoticonSetBean("emoji", 3, 7);
                    emojiEmoticonSetBean.setIconUri("drawable://icon_emoji");
                    emojiEmoticonSetBean.setItemPadding(25);
                    emojiEmoticonSetBean.setVerticalSpacing(10);
                    emojiEmoticonSetBean.setShowDelBtn(true);
                    emojiEmoticonSetBean.setEmoticonList(emojiArray);
                    emoticonDbHelper.insertEmoticonSet(emojiEmoticonSetBean);
                }

                List<EmoticonSetBean> emoticonSetBeans = new ArrayList<>();
                for (EmoticonEntity entity : emoticonEntities) {
                    try {
                        EmoticonSetBean bean = Utils.ParseEmoticons(context, entity.getPath(),
                                entity.getScheme());
                        emoticonSetBeans.add(bean);
                    } catch (IOException e) {
                        HadLog.e(String.format("read %s config.xml error", entity.getPath()), e);
                    } catch (XmlPullParserException e) {
                        HadLog.e(String.format("parse %s config.xml error", entity.getPath()), e);
                    }
                }

                for (EmoticonSetBean setBean : emoticonSetBeans) {
                    emoticonDbHelper.insertEmoticonSet(setBean);
                }
                emoticonDbHelper.cleanup();

                if (emoticonSetBeans.size() == emoticonEntities.size()) {
                    Utils.setIsInitDb(context, true);
                }
            }
        }).start();
    }

    private EmoticonsKeyboardBuilder getBuilder(Context context) {
        if (context == null) {
            throw new RuntimeException("Context is null, cannot create db helper");
        }
        EmoticonDBHelper emoticonDbHelper = new EmoticonDBHelper(context);
        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = emoticonDbHelper.queryAllEmoticonSet();
        emoticonDbHelper.cleanup();

        return new EmoticonsKeyboardBuilder.Builder()
                .setEmoticonSetBeanList(mEmoticonSetBeanList)
                .build();
    }

    @Override
    protected void OnSoftKeyboardPop(int height) {
        super.OnSoftKeyboardPop(height);
        btnEmoticon.setImageResource(R.drawable.icon_face_nomal);
        int barHeight = getResources().getDimensionPixelOffset(R.dimen.keyboard_bar_height);
        mOnChatKeyBoardListener.onKeyboardHeightChanged(height + barHeight);
    }

    @Override
    protected void OnSoftKeyboardClose() {
        super.OnSoftKeyboardClose();
        int barHeight = getResources().getDimensionPixelOffset(R.dimen.keyboard_bar_height);
        if (findViewById(R.id.keyboard_bar_id).getVisibility() == VISIBLE) {
            mOnChatKeyBoardListener.onKeyboardHeightChanged(barHeight);
        }
    }

    public interface OnChatKeyBoardListener {
        void onSendBtnClick(String msg);

        void onRecordingAction(RecordingAction action);

        void onUserDefEmoticonClicked(String tag, String uri);

        // when keyboard popped, get the pixels of the height include input bar
        void onKeyboardHeightChanged(int height);
    }

    public static class SimpleOnChatkeyboardListener implements OnChatKeyBoardListener {
        @Override
        public void onSendBtnClick(final String msg) {
            // This space for rent
        }

        @Override
        public void onRecordingAction(final RecordingAction action) {
            // This space for rent
        }

        @Override
        public void onUserDefEmoticonClicked(final String tag, final String uri) {
            // This space for rent
        }

        @Override
        public void onKeyboardHeightChanged(final int height) {
            // This space for rent
        }
    }

    public enum RecordingAction {
        START,    // start recording
        COMPLETE,  // recording end
        CANCELED,  // recording canceled
        WILLCANCEL,   // state which can be canceled
        RESTORE     // state which is restored from WILLCANCEL
    }
}