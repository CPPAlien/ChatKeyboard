package cn.hadcn.keyboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;


import cn.hadcn.keyboard.emoticon.util.EmoticonHandler;
import cn.hadcn.keyboard.utils.Utils;

public class HadEditText extends EditText {
    private Context mContext;

    public HadEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HadEditText(Context context) {
        super(context);
    }

    public HadEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onTextChanged(CharSequence arg0, int start, int lengthBefore, int after) {
        super.onTextChanged(arg0, start, lengthBefore, after);
        if(onTextChangedInterface != null){
            onTextChangedInterface.onTextChanged(arg0);
        }

        EmoticonHandler.getInstance().setTextFace( arg0.toString(), getText(), Utils.getFontSize(getTextSize()));

        /*if (after <= 0) {
            return;
        }

        int end = start + after;
        String keyStr = arg0.toString().substring(start, end);
        boolean isEmoticonMatcher = false;
        for ( EmoticonBean bean : emoticonBeanList ) {
            if ( !TextUtils.isEmpty(bean.getTag()) && bean.getTag().equals(keyStr) ) {
                Drawable drawable = EmoticonLoader.getInstance(mContext).getDrawable(bean.getIconUri());
                if (drawable != null) {
                    int itemHeight;
                    if (mItemHeight == WRAP_DRAWABLE) {
                        itemHeight = drawable.getIntrinsicHeight();
                    } else if (mItemHeight == WRAP_FONT) {
                        itemHeight = mFontSize;
                    } else {
                        itemHeight = mItemHeight;
                    }

                    int itemWidth;
                    if (mItemWidth == WRAP_DRAWABLE) {
                        itemWidth = drawable.getIntrinsicWidth();
                    } else if (mItemWidth == WRAP_FONT) {
                        itemWidth = mFontSize;
                    } else {
                        itemWidth = mItemWidth;
                    }

                    drawable.setBounds(0, 0, itemWidth, itemHeight);
                    VerticalImageSpan imageSpan = new VerticalImageSpan(drawable);
                    getText().setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    isEmoticonMatcher = true;
                }
            }
        }

        if (!isEmoticonMatcher) {
            ImageSpan[] oldSpans = getText().getSpans(start, end, ImageSpan.class);
            if ( oldSpans != null ) {
                for ( ImageSpan span : oldSpans ) {
                    int endOld = after + getText().getSpanEnd(span) - 1;
                    if ( end >= 0 && endOld > end ) {
                        ImageSpan imageSpan = new ImageSpan(span.getDrawable(), ImageSpan.ALIGN_BASELINE);
                        getText().removeSpan(span);
                        getText().setSpan(imageSpan, end, endOld, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }*/

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (ArrayIndexOutOfBoundsException e) {
            setText(getText().toString());
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void setGravity(int gravity) {
        try {
            super.setGravity(gravity);
        } catch (ArrayIndexOutOfBoundsException e) {
            setText(getText().toString());
            super.setGravity(gravity);
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        try {
            super.setText(text, type);
        } catch (ArrayIndexOutOfBoundsException e) {
            setText(text.toString());
        }
    }

    public interface OnTextChangedInterface {
        void onTextChanged(CharSequence argo);
    }

    OnTextChangedInterface onTextChangedInterface;

    public void setOnTextChangedInterface(OnTextChangedInterface i) {
        onTextChangedInterface = i;
    }
}
