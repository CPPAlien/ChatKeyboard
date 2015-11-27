package cn.hadcn.keyboard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;

import cn.hadcn.keyboard.emoticon.EmoticonBean;
import cn.hadcn.keyboard.emoticon.util.EmoticonHandler;
import cn.hadcn.keyboard.utils.EmoticonLoader;
import cn.hadcn.keyboard.utils.Utils;
import cn.hadcn.keyboard.view.VerticalImageSpan;

/**
 * HadTextView
 * Created by 90Chris on 2015/11/24.
 */
public class ChatTextView extends TextView{
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
        emoticonBeanList = EmoticonHandler.getAllEmoticons(context);
        setText(getText());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!TextUtils.isEmpty(text)) {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            setTextFace(getContext(), text.toString(), builder);
            text = builder;
        }
        super.setText(text, type);
    }

    public static ArrayList<EmoticonBean> emoticonBeanList = null;
    public void setTextFace( Context context, String content, Spannable spannable ) {
        int fontHeight = (int)getTextSize();
        int keyIndex = 0;
        for (EmoticonBean bean : emoticonBeanList) {
            int keyLength = bean.getTag().length();
            while ( keyIndex >= 0 ) {
                keyIndex = content.indexOf(bean.getTag(), keyIndex);  //when do not find, get -1
                if ( keyIndex < 0 ) {
                    break;
                }
                Drawable drawable = EmoticonLoader.getInstance(context).getDrawable(bean.getIconUri());
                int extendsPix = Utils.dip2px(context, 10);
                drawable.setBounds(0, 0, fontHeight + extendsPix, fontHeight + extendsPix);
                VerticalImageSpan imageSpan = new VerticalImageSpan(drawable);
                spannable.setSpan(imageSpan, keyIndex, keyIndex + keyLength, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                keyIndex += keyLength;
            }
            keyIndex = 0;
        }
    }
}
