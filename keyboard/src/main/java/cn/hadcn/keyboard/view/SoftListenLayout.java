package cn.hadcn.keyboard.view;

import static cn.hadcn.keyboard.utils.Utils.getDisplayHeightPixels;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;

import cn.hadcn.keyboard.utils.Utils;

/**
 * listen keyboard show or hide
 * when keyboard show, keep parent layout height, make it not be shrank by keyboard
 * @author chris
 */
public abstract class SoftListenLayout extends RelativeLayout {
    private int mMinLayoutHeight = 0;
    private int mMinKeyboardHeight = 0;
    private int mGlobalBottom = 0;
    private int mKeyboardHeight = 0;
    protected Context mContext;
    // 状态栏的高度
    private int statusBarHeight;
    // 底部虚拟按键的高度
    private int softButtonsBarHeight;
    // 软键盘的显示状态
    private boolean isShowKeyboard;

    public SoftListenLayout(final Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        int displayHeight = getDisplayHeightPixels(context);
        //the height of layout is at least 2/3 of screen height
        mMinLayoutHeight = displayHeight * 2 / 3;

        // min keyboard height, for ignoring navigation bar hide or show effects
        mMinKeyboardHeight = displayHeight / 3;

        mKeyboardHeight = Utils.getDefKeyboardHeight(mContext);

        statusBarHeight = getStatusBarHeight(mContext);
        softButtonsBarHeight = getSoftButtonsBarHeight((Activity) mContext);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                ((Activity) getContext()).getWindow().getDecorView()
                        .getWindowVisibleDisplayFrame(r);

                int screenHeight = ((Activity) getContext()).getWindow().getDecorView().getRootView().getHeight();
                int heightDiff = screenHeight - (r.bottom -r.top);
                if(mKeyboardHeight == 0 && heightDiff > statusBarHeight + softButtonsBarHeight){
                    mKeyboardHeight = heightDiff -statusBarHeight -softButtonsBarHeight;
                }

                if (isShowKeyboard) {
                    // 如果软键盘是弹出的状态，并且heightDiff小于等于 状态栏 + 虚拟按键 高度，
                    // 说明这时软键盘已经收起
                    if (heightDiff <= statusBarHeight + softButtonsBarHeight) {
                        isShowKeyboard = false;
                        OnSoftKeyboardClose();
                    }
                } else {
                    // 如果软键盘是收起的状态，并且heightDiff大于 状态栏 + 虚拟按键 高度，
                    // 说明这时软键盘已经弹出
                    if (heightDiff > statusBarHeight + softButtonsBarHeight) {
                        isShowKeyboard = true;
                        OnSoftKeyboardPop(mKeyboardHeight);
                    }
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
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

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = Resources.getSystem().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取底部虚拟按键的高度
     */
    public static int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight)
            return realHeight - usableHeight;
        else
            return 0;
    }

    protected abstract void OnSoftKeyboardPop(int height);

    protected abstract void OnSoftKeyboardClose();
}
