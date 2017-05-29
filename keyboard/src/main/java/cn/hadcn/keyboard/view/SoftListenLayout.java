package cn.hadcn.keyboard.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * listen keyboard show or hide
 * when keyboard show, keep parent layout height, make it not be shrank by keyboard
 */
public abstract class SoftListenLayout extends RelativeLayout {
    private int mMinLayoutHeight = 0;
    private int mMinKeyboardHeight = 0;
    private int mGlobalBottom = 0;
    private int mKeyboardHeight = 0;

    public SoftListenLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        //the height of layout is at least 2/3 of screen height
        mMinLayoutHeight = metrics.heightPixels * 2 / 3;

        // min keyboard height, for ignoring navigation bar hide or show effects
        mMinKeyboardHeight = metrics.heightPixels / 3;

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                ((Activity) getContext()).getWindow().getDecorView()
                        .getWindowVisibleDisplayFrame(r);
                if (mGlobalBottom != 0 && mGlobalBottom - r.bottom > mMinKeyboardHeight) {
                    // keyboard pop
                    mKeyboardHeight = mGlobalBottom - r.bottom;
                    OnSoftKeyboardPop(mKeyboardHeight);
                } else if (mGlobalBottom != 0 && r.bottom - mGlobalBottom > mMinKeyboardHeight) {
                    OnSoftKeyboardClose();
                }

                mGlobalBottom = r.bottom;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureHeight = measureHeight(heightMeasureSpec);
        if (measureHeight < mMinLayoutHeight) {
            // if keyboard show, this layout height will be shrank, we should extend it
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int expandSpec = MeasureSpec.makeMeasureSpec(measureHeight + mKeyboardHeight,
                    heightMode);
            super.onMeasure(widthMeasureSpec, expandSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private int measureHeight(int pHeightMeasureSpec) {
        int result = 0;
        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.EXACTLY) {
            result = heightSize;
        }
        return result;
    }

    protected abstract void OnSoftKeyboardPop(int height);

    protected abstract void OnSoftKeyboardClose();
}