package cn.hadcn.keyboard;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import cn.hadcn.keyboard.emoticon.util.EmoticonHandler;
import cn.hadcn.keyboard.utils.Utils;

/**
 * HadTextView
 * Created by 90Chris on 2015/11/24.
 */
public class ChatTextView extends TextView{
    Context mContext;

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
            EmoticonHandler.getInstance().setTextFace(mContext, text.toString(), builder, Utils.getFontSize(getTextSize()) );
            text = builder;
        }
        super.setText(text, type);
    }

}
