package cn.hadcn.keyboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * HadTextView
 * Created by 90Chris on 2015/11/24.
 */
public class HadTextView extends TextView{
    public HadTextView(Context context) {
        super(context);
    }

    public HadTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HadTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {

        super.setText(text, type);
    }
}
