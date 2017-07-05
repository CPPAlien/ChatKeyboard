package cn.hadcn.keyboard;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v4.app.ActivityCompat;
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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import cn.hadcn.keyboard.emoticon.EmoticonBean;
import cn.hadcn.keyboard.emoticon.EmoticonSetBean;
import cn.hadcn.keyboard.emoticon.db.EmoticonDBHelper;
import cn.hadcn.keyboard.emoticon.util.DefEmoticons;
import cn.hadcn.keyboard.emoticon.util.EmoticonHandler;
import cn.hadcn.keyboard.emoticon.util.EmoticonsKeyboardBuilder;
import cn.hadcn.keyboard.emoticon.view.EmoticonLayout;
import cn.hadcn.keyboard.emoticon.view.EmoticonsTabBarView;
import cn.hadcn.keyboard.media.MediaBean;
import cn.hadcn.keyboard.media.MediaLayout;
import cn.hadcn.keyboard.utils.EmoticonBase;
import cn.hadcn.keyboard.utils.HadLog;
import cn.hadcn.keyboard.utils.Utils;
import cn.hadcn.keyboard.view.HadEditText;
import cn.hadcn.keyboard.view.SoftHandleLayout;

/**
 * @author chris
 */
public class ChatKeyboardLayout extends SoftHandleLayout implements EmoticonsTabBarView
        .OnEmoticonsTabChangeListener {
    public static class Style {
        private Style() {
        }

        public static final int NONE = 0;
        public static final int TEXT_ONLY = 1;
        public static final int TEXT_EMOTICON = 2;
        public static final int CHAT_STYLE = 3;
    }

    @IntDef({Style.TEXT_ONLY, Style.TEXT_EMOTICON, Style.CHAT_STYLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface KeyboardStyle {
    }

    private int mEmoticonLayoutPos = 0; //display emoticons area
    private int mMediaLayoutPos = 0;    //display medias area
    private int mLayoutOrderCount = 0;
    private int mChildViewPosition = -1;
    private HadEditText etInputArea;
    private RelativeLayout rlInput;
    private LinearLayout lyBottomLayout;
    private ImageView btnEmoticon;
    private Button btnSend;
    private Button btnRecording;
    private ImageView leftIconView;
    private ImageView rightIconView;
    private boolean isShowRightIcon = false;
    private boolean isAddedEmoticonLayout = false;
    @KeyboardStyle
    int mCurrentStyle = Style.NONE;

    public ChatKeyboardLayout(Context context) {
        super(context, null);
        initView(context);
    }

    public ChatKeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable
                .ChatKeyboardLayout);
        Drawable btnSendBg = typedArray.getDrawable(R.styleable
                .ChatKeyboardLayout_sendButtonBackground);
        String btnSendText = typedArray.getString(R.styleable.ChatKeyboardLayout_sendButtonText);
        ColorStateList btnSendTextColor = typedArray.getColorStateList(R.styleable
                .ChatKeyboardLayout_sendButtonTextColor);
        isShowRightIcon = typedArray.getBoolean(R.styleable
                .ChatKeyboardLayout_chatStyleShowRightIcon, false);
        @KeyboardStyle int keyboardStyle = typedArray.getInt(R.styleable
                .ChatKeyboardLayout_keyboardStyle, Style.TEXT_ONLY);
        setKeyboardStyle(keyboardStyle);
        Drawable leftIcon = typedArray.getDrawable(R.styleable
                .ChatKeyboardLayout_chatStyleLeftIcon);
        Drawable rightIcon = typedArray.getDrawable(R.styleable
                .ChatKeyboardLayout_chatStyleRightIcon);

        typedArray.recycle();

        if (btnSendBg != null) {
            btnSend.setBackgroundDrawable(btnSendBg);
        }
        if (btnSendText != null) {
            btnSend.setText(btnSendText);
        }
        if (btnSendTextColor != null) {
            btnSend.setTextColor(btnSendTextColor);
        }
        if (leftIcon != null) {
            leftIconView.setImageDrawable(leftIcon);
        }
        if (rightIcon != null) {
            rightIconView.setImageDrawable(rightIcon);
        }
    }

    private void initView(Context context) {
        // must be in front of inflate
        EmoticonHandler.getInstance(context).loadEmoticonsToMemory();
        LayoutInflater.from(context).inflate(R.layout.view_keyboard_layout, this);

        rlInput = (RelativeLayout) findViewById(R.id.view_keyboard_input_layout);
        lyBottomLayout = (LinearLayout) findViewById(R.id.view_keyboard_bottom);
        btnEmoticon = (ImageView) findViewById(R.id.view_keyboard_face_icon);
        leftIconView = (ImageView) findViewById(R.id.view_keyboard_left_icon);
        btnRecording = (Button) findViewById(R.id.view_keyboard_recording_bar);
        rightIconView = (ImageView) findViewById(R.id.view_keyboard_right_icon);
        btnSend = (Button) findViewById(R.id.view_keyboard_send_button);

        etInputArea = (HadEditText) findViewById(R.id.et_chat);

        setAutoHeightLayoutView(lyBottomLayout);
        leftIconView.setOnClickListener(new LeftIconClickListener());
        rightIconView.setOnClickListener(new RightIconClickListener());
        rightIconView.setVisibility(GONE);
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
                String str = arg0.toString();
                if (mOnChatKeyBoardListener != null) {
                    mOnChatKeyBoardListener.onInputTextChanged(str);
                }
                if (!isShowRightIcon) {
                    return;
                }
                if (TextUtils.isEmpty(str)) {
                    rightIconView.setVisibility(VISIBLE);
                    btnSend.setVisibility(GONE);
                } else {
                    rightIconView.setVisibility(GONE);
                    btnSend.setVisibility(VISIBLE);
                }
            }
        });
    }

    private void setEditableState(boolean editableState) {
        if (editableState) {
            etInputArea.setFocusable(true);
            etInputArea.setFocusableInTouchMode(true);
            etInputArea.requestFocus();
            // rlInput.setBackgroundResource(R.drawable.input_bg_green);
        } else {
            etInputArea.setFocusable(false);
            etInputArea.setFocusableInTouchMode(false);
            // rlInput.setBackgroundResource(R.drawable.input_bg_gray);
        }
    }

    public Button getSendButton() {
        return btnSend;
    }

    public HadEditText getInputEditText() {
        return etInputArea;
    }

    public void clearInputContent() {
        etInputArea.setText("");
    }

    public void del() {
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        etInputArea.onKeyDown(KeyEvent.KEYCODE_DEL, event);
    }

    /**
     * Set the keyboard style, {@link Style}
     * when this method is called, the keyboard will change it's style immediately
     * if the style is same as current style, it won't change
     *
     * @param style style of keyboard
     */
    public void setKeyboardStyle(@KeyboardStyle int style) {
        // when style changed, restore the keyboard to init state
        if (mCurrentStyle == style) {
            return;
        }
        clearInputContent();
        etInputArea.setVisibility(VISIBLE);
        btnRecording.setVisibility(GONE);
        hideKeyboard();
        switch (style) {
            case Style.TEXT_ONLY:
                btnEmoticon.setVisibility(GONE);
                rightIconView.setVisibility(GONE);
                btnSend.setVisibility(VISIBLE);
                leftIconView.setVisibility(GONE);
                isShowRightIcon = false;
                break;
            case Style.TEXT_EMOTICON:
                showEmoticons();
                rightIconView.setVisibility(GONE);
                btnSend.setVisibility(VISIBLE);
                leftIconView.setVisibility(GONE);
                isShowRightIcon = false;
                break;
            case Style.CHAT_STYLE:
                showEmoticons();
                if (isShowRightIcon) {
                    rightIconView.setVisibility(VISIBLE);
                    btnSend.setVisibility(GONE);
                } else {
                    rightIconView.setVisibility(GONE);
                    btnSend.setVisibility(VISIBLE);
                }

                leftIconView.setVisibility(VISIBLE);
                break;
        }
        mCurrentStyle = style;
    }

    /**
     * when keyboard style was set as Style.CHAT_STYLE
     * you can set right icon behavior, this method will clear the input content
     *
     * @param isShow true, right icon will show, and when user input text, it will be hidden;
     *               false, right icon won't show in any situation
     */
    public void setShowRightIcon(boolean isShow) {
        clearInputContent();
        isShowRightIcon = isShow;
        if (isShowRightIcon) {
            rightIconView.setVisibility(VISIBLE);
            btnSend.setVisibility(GONE);
        } else {
            rightIconView.setVisibility(GONE);
            btnSend.setVisibility(VISIBLE);
        }
    }

    /**
     * hide whole layout of keyboard
     */
    public void hideLayout() {
        hideKeyboard();
        findViewById(R.id.keyboard_layout_id).setVisibility(GONE);
    }

    /**
     * show keyboard layout
     */
    public void showLayout() {
        findViewById(R.id.keyboard_layout_id).setVisibility(VISIBLE);
        int barHeight = findViewById(R.id.keyboard_layout_id).getHeight();
        if (mOnChatKeyBoardListener != null) {
            mOnChatKeyBoardListener.onKeyboardHeightChanged(barHeight);
        }
    }

    /**
     * judge whether keyboard layout is show
     *
     * @return true or false
     */
    public boolean isLayoutVisible() {
        return VISIBLE == findViewById(R.id.keyboard_layout_id).getVisibility();
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
     * pop soft keyboard, if the layout is hidden, it will show layout first
     */
    public void popKeyboard() {
        showLayout();
        openSoftKeyboard(etInputArea);
        showAutoView();
    }

    /**
     * judge whether keyboard was popped
     *
     * @return true or false
     */
    public boolean isKeyboardPopped() {
        return mKeyboardState != KEYBOARD_STATE_NONE;
    }

    @Override
    protected void autoViewHeightChanged(final int height) {
        super.autoViewHeightChanged(height);
        if (findViewById(R.id.keyboard_layout_id).getVisibility() != VISIBLE) {
            if (mOnChatKeyBoardListener != null) {
                mOnChatKeyBoardListener.onKeyboardHeightChanged(0);
            }
        } else {
            int barHeight = findViewById(R.id.keyboard_layout_id).getHeight();
            if (mOnChatKeyBoardListener != null) {
                mOnChatKeyBoardListener.onKeyboardHeightChanged(barHeight);
            }
        }
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

    /**
     * The action after left icon clicked
     * default action is to show or hide input area and recording bar
     *
     * @param view view
     */
    protected void leftIconClicked(View view) {
        if (rlInput.isShown()) {
            // switch to voice recording bar
            hideAutoView();
            closeSoftKeyboard(etInputArea);
            rlInput.setVisibility(INVISIBLE);
            btnRecording.setVisibility(VISIBLE);
            leftIconView.setImageResource(R.drawable.keyboard_icon);
            btnSend.setVisibility(GONE);
            if (isShowRightIcon) {
                rightIconView.setVisibility(VISIBLE);
            }
        } else {
            // switch to text input bar
            rlInput.setVisibility(VISIBLE);
            btnRecording.setVisibility(GONE);
            setEditableState(true);
            openSoftKeyboard(etInputArea);
            leftIconView.setImageResource(R.drawable.recording_icon);
            if (!TextUtils.isEmpty(etInputArea.getText().toString())) {
                rightIconView.setVisibility(GONE);
                btnSend.setVisibility(VISIBLE);
            }
            if (!isShowRightIcon) {    //if media button not be shown, show button send
                // every time
                btnSend.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * The action after right icon clicked
     * default action is show media layout, you can implement it by yourself
     *
     * @param view view
     */
    protected void rightIconClicked(View view) {
        switch (mKeyboardState) {
            case KEYBOARD_STATE_BOTH:
                closeSoftKeyboard(etInputArea);
                show(mMediaLayoutPos);
                break;
            case KEYBOARD_STATE_NONE:
                btnEmoticon.setImageResource(R.drawable.icon_face_nomal);
                rlInput.setVisibility(VISIBLE);
                btnRecording.setVisibility(GONE);
                leftIconView.setImageResource(R.drawable.recording_icon);
                setEditableState(true);
                showAutoView();
                show(mMediaLayoutPos);
                break;
            case KEYBOARD_STATE_FUNC:
                btnEmoticon.setImageResource(R.drawable.icon_face_nomal);
                if (mChildViewPosition == mMediaLayoutPos) {
                    openSoftKeyboard(etInputArea);
                } else {
                    show(mMediaLayoutPos);
                }
                break;
        }
    }

    private class SendClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (mOnChatKeyBoardListener != null) {
                mOnChatKeyBoardListener.onSendButtonClicked(etInputArea.getText().toString());
            }
        }
    }

    private class LeftIconClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (mOnChatKeyBoardListener != null && !mOnChatKeyBoardListener.onLeftIconClicked
                    (view)) {
                leftIconClicked(view);
            }
        }
    }

    private class FaceClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            switch (mKeyboardState) {
                case KEYBOARD_STATE_BOTH:
                    closeSoftKeyboard(etInputArea);
                    show(mEmoticonLayoutPos);
                    btnEmoticon.setImageResource(R.drawable.icon_face_pop);
                    break;
                case KEYBOARD_STATE_NONE:
                    btnEmoticon.setImageResource(R.drawable.icon_face_pop);
                    setEditableState(true);
                    showAutoView();
                    show(mEmoticonLayoutPos);
                    break;
                case KEYBOARD_STATE_FUNC:
                    if (mChildViewPosition == mEmoticonLayoutPos) {
                        btnEmoticon.setImageResource(R.drawable.icon_face_nomal);
                        openSoftKeyboard(etInputArea);
                    } else {
                        show(mEmoticonLayoutPos);
                        btnEmoticon.setImageResource(R.drawable.icon_face_pop);
                    }
                    break;
            }
        }
    }

    private class RightIconClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (mOnChatKeyBoardListener != null && !mOnChatKeyBoardListener.onRightIconClicked
                    (view)) {
                rightIconClicked(view);
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
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission
                        .RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    if (mOnChatKeyBoardListener != null) {
                        mOnChatKeyBoardListener.onRecordingAction(RecordingAction
                                .PERMISSION_NOT_GRANTED);
                    }
                    return true;
                }
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
                        mOnChatKeyBoardListener.onRecordingAction(RecordingAction.READY_CANCEL);
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

    /**
     * init media contents, if you need media area
     *
     * @param mediaContents media contents
     */
    public void initMediaContents(List<MediaBean> mediaContents) {
        MediaLayout mediaLayout = new MediaLayout(mContext);
        mediaLayout.setContents(mediaContents);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lyBottomLayout.addView(mediaLayout, params);
        mMediaLayoutPos = mLayoutOrderCount;
        mLayoutOrderCount = 1;
    }

    private void addEmoticonLayout() {
        if (isAddedEmoticonLayout) {
            return;
        }
        EmoticonsKeyboardBuilder builder = getBuilder(mContext);
        EmoticonLayout layout = new EmoticonLayout(mContext);
        layout.setContents(builder, new EmoticonLayout.OnEmoticonListener() {
            @Override
            public void onEmoticonItemClicked(EmoticonBean bean) {
                if (etInputArea != null) {
                    setEditableState(true);

                    if (bean.getEventType() == EmoticonBean.FACE_TYPE_DEL) {
                        int action = KeyEvent.ACTION_DOWN;
                        int code = KeyEvent.KEYCODE_DEL;
                        KeyEvent event = new KeyEvent(action, code);
                        etInputArea.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                        return;
                    } else if (bean.getEventType() == EmoticonBean.FACE_TYPE_STICKER) {
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
        mEmoticonLayoutPos = mLayoutOrderCount;
        mLayoutOrderCount = 1;
        isAddedEmoticonLayout = true;
    }

    private void showEmoticons() {
        btnEmoticon.setVisibility(VISIBLE);
        addEmoticonLayout();
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

    /**
     * judge whether emoticons db init success, you can just just init db, when it's not success
     *
     * @param context context
     * @return true or false
     */
    public static boolean isEmoticonsDBInitSuccess(Context context) {
        return Utils.isInitDb(context);
    }

    /**
     * init emoticons db
     *
     * @param context          context
     * @param isShowEmoji      is show default emoji
     * @param emoticonEntities user defined emoticons or stickers
     */
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

                if (emoticonEntities == null || emoticonEntities.isEmpty()) {
                    Utils.setIsInitDb(context, true);
                    return;
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
            throw new NullPointerException("Context is null, cannot create db helper");
        }
        EmoticonDBHelper emoticonDbHelper = new EmoticonDBHelper(context);
        List<EmoticonSetBean> mEmoticonSetBeanList = emoticonDbHelper.queryAllEmoticonSet();
        emoticonDbHelper.cleanup();

        return new EmoticonsKeyboardBuilder.Builder()
                .setEmoticonSetBeanList(mEmoticonSetBeanList)
                .build();
    }

    @Override
    protected void OnSoftKeyboardPop(int height) {
        super.OnSoftKeyboardPop(height);
        btnEmoticon.setImageResource(R.drawable.icon_face_nomal);
    }

    public interface OnChatKeyBoardListener {
        /**
         * When send button clicked
         *
         * @param text content in input area
         */
        void onSendButtonClicked(String text);

        /**
         * When user input or delete text in input area
         *
         * @param text changing text
         */
        void onInputTextChanged(String text);

        /**
         * Recording button action {@link RecordingAction}
         *
         * @param action action
         */
        void onRecordingAction(RecordingAction action);

        /**
         * When sticker defined by developer clicked
         *
         * @param tag sticker tag
         * @param uri sticker location uri
         */
        void onUserDefEmoticonClicked(String tag, String uri);

        /**
         * when keyboard popped or back, get the pixels of the height include keyboard bar
         *
         * @param height pixel height
         */
        void onKeyboardHeightChanged(int height);

        /**
         * when left icon clicked, it will be called
         *
         * @param view view of clicked
         * @return true, don't execute default actions; false execute default actions
         */
        boolean onLeftIconClicked(View view);

        /**
         * when right icon clicked, it will be called
         *
         * @param view view of clicked
         * @return true, don't execute default actions; false execute default actions
         */
        boolean onRightIconClicked(View view);
    }

    public static class SimpleOnChatKeyboardListener implements OnChatKeyBoardListener {
        @Override
        public void onSendButtonClicked(final String text) {
            // This space for rent
        }

        @Override
        public void onInputTextChanged(final String text) {
            //This space for rent
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

        @Override
        public boolean onLeftIconClicked(final View view) {
            return false;
        }

        @Override
        public boolean onRightIconClicked(final View view) {
            return false;
        }
    }

    public enum RecordingAction {
        PERMISSION_NOT_GRANTED, // audio permission not granted
        START,    // start recording
        COMPLETE,  // recording end
        CANCELED,  // recording canceled
        READY_CANCEL,   // state which ready to be canceled
        RESTORE     // state which is restored from WILLCANCEL
    }

    @Override
    public void onTabClicked(final int position) {

    }
}