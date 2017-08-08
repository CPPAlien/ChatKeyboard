package cn.hadcn.keyboard.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import cn.hadcn.keyboard.R;
import cn.hadcn.keyboard.utils.Utils;

/**
 * handle the child view
 * and control keyboard area show or hide
 * @author chris
 */
public class SoftHandleLayout extends SoftListenLayout {
    public static final int KEYBOARD_STATE_NONE = 100;  // no pop
    public static final int KEYBOARD_STATE_FUNC = 101;  // only media or emoticon pop
    public static final int KEYBOARD_STATE_BOTH = 102;  // keyboard and media or emoticon pop
    // together
    protected int mAutoHeightLayoutId;
    protected int mAutoViewHeight;
    protected View mAutoHeightLayoutView;
    protected int mKeyboardState = KEYBOARD_STATE_NONE;
    private boolean isAutoViewNeedHide = true; //if soft keyboard close by itself, close auto
    // view too. if not, just close keyboard

    public SoftHandleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAutoViewHeight = Utils.getDefKeyboardHeight(mContext);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        int childSum = getChildCount();
        if (childSum > 1) {
            throw new IllegalStateException("can host only one direct child");
        }
        super.addView(child, index, params);
        if (childSum == 0) {
            mAutoHeightLayoutId = child.getId();
            if (mAutoHeightLayoutId < 0) {
                child.setId(R.id.keyboard_layout_id);
                mAutoHeightLayoutId = R.id.keyboard_layout_id;
            }
            LayoutParams paramsChild = (LayoutParams) child.getLayoutParams();
            paramsChild.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            child.setLayoutParams(paramsChild);
        } else if (childSum == 1) {
            LayoutParams paramsChild = (LayoutParams) child.getLayoutParams();
            paramsChild.addRule(RelativeLayout.ABOVE, mAutoHeightLayoutId);
            child.setLayoutParams(paramsChild);
        }
    }

    /**
     * set height media or emoticons parent view
     *
     * @param view view
     */
    protected void setAutoHeightLayoutView(View view) {
        mAutoHeightLayoutView = view;
    }

    private void setAutoViewHeight(final int height) {
        if (height == 0) {
            mAutoHeightLayoutView.setVisibility(GONE);
        } else {
            mAutoHeightLayoutView.setVisibility(VISIBLE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mAutoHeightLayoutView
                    .getLayoutParams();
            params.height = height;
            mAutoHeightLayoutView.setLayoutParams(params);
        }
        // it will take some time for view draw
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                autoViewHeightChanged(height);
            }
        }, 100);
    }

    protected void hideAutoView() {
        post(new Runnable() {
            @Override
            public void run() {
                setAutoViewHeight(0);
            }
        });
        mKeyboardState = KEYBOARD_STATE_NONE;
    }

    protected void showAutoView() {
        // show auto view is after keyboard show will be better
        // there exist time during keyboard popping
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setAutoViewHeight(mAutoViewHeight);
            }
        }, 150);
        isAutoViewNeedHide = true;
        mKeyboardState = KEYBOARD_STATE_FUNC;
    }

    protected void autoViewHeightChanged(final int height) {
        // rent the place for child use
    }

    @Override
    protected void OnSoftKeyboardPop(int height) {
        if (height > 0 && height != mAutoViewHeight) {
            mAutoViewHeight = height;
            Utils.setDefKeyboardHeight(mContext, mAutoViewHeight);
        }
        //if soft keyboard popped, auto view must be visible, soft keyboard covers it
        if (mKeyboardState != KEYBOARD_STATE_BOTH) {
            showAutoView();
        } else {
            // else, just update auto view height
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setAutoViewHeight(mAutoViewHeight);
                }
            }, 150);
        }
        mKeyboardState = KEYBOARD_STATE_BOTH;
    }

    @Override
    protected void OnSoftKeyboardClose() {
        mKeyboardState = mKeyboardState == KEYBOARD_STATE_BOTH ? KEYBOARD_STATE_FUNC :
                KEYBOARD_STATE_NONE;

        // if keyboard closed isn't by calling close, but by pressing back button or keyboard
        // hide, hide auto view
        if (isAutoViewNeedHide) {
            hideAutoView();
        }
        isAutoViewNeedHide = true;
    }

    /**
     * display soft keyboard
     */
    protected void openSoftKeyboard(EditText et) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService
                (Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * close soft keyboard
     */
    protected void closeSoftKeyboard(EditText et) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService
                (Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && ((Activity) mContext).getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), InputMethodManager
                    .HIDE_NOT_ALWAYS);
        }
        isAutoViewNeedHide = false; //only if you close keyboard by calling method, auto view
        // don't need hide
    }
}