package cn.hadcn.keyboard;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import cn.hadcn.keyboard.emoticon.util.EmoticonHandler;
import cn.hadcn.keyboard.utils.Utils;

/**
 * ChatTextView
 * which replace the special unicode to emoticon
 * @author chris
 */
public class ChatTextView extends TextView {
    private Context mContext;

    public ChatTextView(Context context) {
        super(context);
        init(context);
    }

    public ChatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setText(getText());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!TextUtils.isEmpty(text)) {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            EmoticonHandler.getInstance(mContext).setTextFace(text.toString(), builder, 0, Utils
                    .getFontSize(getTextSize()));
            super.setText(builder, type);
        } else {
            super.setText(text, type);
        }
    }
}
