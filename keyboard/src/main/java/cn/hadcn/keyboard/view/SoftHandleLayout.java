package cn.hadcn.keyboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import cn.hadcn.keyboard.R;
import cn.hadcn.keyboard.utils.Utils;


public class SoftHandleLayout extends SoftListenLayout {
    public static final int KEYBOARD_STATE_NONE = 100;  // no pop
    public static final int KEYBOARD_STATE_FUNC = 102;  // other pop
    public static final int KEYBOARD_STATE_BOTH = 103;  // keyboard pop

    protected Context mContext;
    protected int mAutoHeightLayoutId;
    protected int mAutoViewHeight;
    protected View mAutoHeightLayoutView;
    protected int mKeyboardState = KEYBOARD_STATE_NONE;
    private boolean isAutoViewNeedHide = true; //if soft keyboard close by itself, close auto view too. if not, just close keyboard

    public SoftHandleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
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
                child.setId(R.id.main_view_id);
                mAutoHeightLayoutId = R.id.main_view_id;
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

    public void setAutoHeightLayoutView(View view) {
        mAutoHeightLayoutView = view;
    }

    public void setAutoViewHeight(final int height) {

        if ( height == 0 ) {
            mAutoHeightLayoutView.setVisibility(GONE);
        } else {
            mAutoHeightLayoutView.setVisibility(VISIBLE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mAutoHeightLayoutView.getLayoutParams();
            params.height = height;
            mAutoHeightLayoutView.setLayoutParams(params);
        }
    }

    public void hideAutoView(){
        this.post(new Runnable() {
            @Override
            public void run() {
                Utils.closeSoftKeyboard(mContext);
                setAutoViewHeight(0);
            }
        });
        mKeyboardState = KEYBOARD_STATE_NONE ;
    }

    public void showAutoView(){
        isAutoViewNeedHide = false;
        setAutoViewHeight(Utils.dip2px(mContext, mAutoViewHeight));
        mKeyboardState = mKeyboardState == KEYBOARD_STATE_NONE ? KEYBOARD_STATE_FUNC : KEYBOARD_STATE_BOTH ;
    }

    @Override
    public void OnSoftKeyboardPop(final int height) {
        mKeyboardState = KEYBOARD_STATE_BOTH;
        int heightDp = Utils.px2dip(mContext, height);
        if (heightDp > 0 && heightDp != mAutoViewHeight) {
            mAutoViewHeight = heightDp;
            Utils.setDefKeyboardHeight(mContext, mAutoViewHeight);
        }
        post(new Runnable() {
            @Override
            public void run() {
                setAutoViewHeight(height);
            }
        });
    }

    @Override
    public void OnSoftKeyboardClose() {
        if ( isAutoViewNeedHide ) {
            post(new Runnable() {
                @Override
                public void run() {
                    setAutoViewHeight(0);
                }
            });
        }
        isAutoViewNeedHide = true;
        mKeyboardState = mKeyboardState == KEYBOARD_STATE_BOTH ? KEYBOARD_STATE_FUNC : KEYBOARD_STATE_NONE;
    }
}