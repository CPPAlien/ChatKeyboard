package cn.hadcn.keyboard.emoticon.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.hadcn.keyboard.R;
import cn.hadcn.keyboard.emoticon.EmoticonSetBean;
import cn.hadcn.keyboard.emoticon.util.EmoticonsKeyboardBuilder;
import cn.hadcn.keyboard.utils.EmoticonLoader;

/**
 * @author chris
 */
public class EmoticonsTabBarView extends RelativeLayout {
    private Context mContext;
    private HorizontalScrollView mEmoticonsBarScrollView;
    private LinearLayout mEmoticonsBar;
    private List<ImageView> mToolBtnList = new ArrayList<>();
    private int mBtnWidth;

    public EmoticonsTabBarView(Context context) {
        this(context, null);
    }

    public EmoticonsTabBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.emoticonstoolbar_view, this);
        this.mContext = context;
        mEmoticonsBarScrollView = (HorizontalScrollView) findViewById(R.id
                .keyboard_emoticons_bar_scroll);
        mEmoticonsBar = (LinearLayout) findViewById(R.id.keyboard_emoticons_bar_content);
        mBtnWidth = getResources().getDimensionPixelOffset(R.dimen
                .keyboard_emoticons_bar_item_width);
    }

    private void scrollToBtnPosition(final int position) {
        int childCount = mEmoticonsBar.getChildCount();
        if (position < childCount) {
            mEmoticonsBarScrollView.post(new Runnable() {
                @Override
                public void run() {
                    int mScrollX = mEmoticonsBarScrollView.getScrollX();
                    int childX = mEmoticonsBar.getChildAt(position).getLeft();
                    if (childX < mScrollX) {
                        mEmoticonsBarScrollView.scrollTo(childX, 0);
                        return;
                    }

                    int childWidth = mEmoticonsBar.getChildAt(position).getWidth();
                    int hsvWidth = mEmoticonsBarScrollView.getWidth();
                    int childRight = childX + childWidth;
                    int scrollRight = mScrollX + hsvWidth;

                    if (childRight > scrollRight) {
                        mEmoticonsBarScrollView.scrollTo(childRight - scrollRight, 0);
                    }
                }
            });
        }
    }

    public void setToolBtnSelect(int select) {
        scrollToBtnPosition(select);
        for (int i = 0; i < mToolBtnList.size(); i++) {
            if (select == i) {
                mToolBtnList.get(i).setBackgroundColor(ContextCompat.getColor(getContext(), R.color
                        .keyboard_emoticons_bar_item_select));
            } else {
                mToolBtnList.get(i).setBackgroundColor(ContextCompat.getColor(getContext(), R.color
                        .keyboard_emoticons_bar_item_normal));
            }
        }
    }

    /**
     * add additional emoticon bar item
     *
     * @param drawable icon drawable
     */
    public void addItem(@NonNull Drawable drawable) {
        if (mEmoticonsBar != null) {
            View toolBtnView = inflate(mContext, R.layout.emoticonstoolbar_item, null);
            ImageView iv_icon = (ImageView) toolBtnView.findViewById(R.id.iv_icon);
            iv_icon.setImageDrawable(drawable);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(mBtnWidth,
                    LayoutParams.MATCH_PARENT);
            iv_icon.setLayoutParams(imgParams);
            mEmoticonsBar.addView(toolBtnView);
            final int position = mToolBtnList.size();
            mToolBtnList.add(iv_icon);
            iv_icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onTabClicked(position);
                    }
                }
            });
        }
    }

    private int getIdValue() {
        int childCount = getChildCount();
        int id = 1;
        if (childCount == 0) {
            return id;
        }
        boolean isKeep = true;
        while (isKeep) {
            isKeep = false;
            Random random = new Random();
            id = random.nextInt(100);
            for (int i = 0; i < childCount; i++) {
                if (getChildAt(i).getId() == id) {
                    isKeep = true;
                    break;
                }
            }
        }
        return id;
    }

    public void addFixedView(View view, boolean isRight) {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                .MATCH_PARENT);
        LayoutParams hsvParams = (LayoutParams) mEmoticonsBarScrollView.getLayoutParams();
        if (view.getId() <= 0) {
            view.setId(getIdValue());
        }
        if (isRight) {
            params.addRule(ALIGN_PARENT_RIGHT);
            hsvParams.addRule(LEFT_OF, view.getId());
        } else {
            params.addRule(ALIGN_PARENT_LEFT);
            hsvParams.addRule(RIGHT_OF, view.getId());
        }
        addView(view, params);
        mEmoticonsBarScrollView.setLayoutParams(hsvParams);
    }

    public void setEmoticonContents(EmoticonsKeyboardBuilder builder) {
        final List<EmoticonSetBean> emoticonSetBeanList = builder.builder == null ? null :
                builder.builder
                        .getEmoticonSetBeanList();
        if (emoticonSetBeanList == null) {
            return;
        }

        int i = 0;
        for (EmoticonSetBean bean : emoticonSetBeanList) {
            View toolBtnView = inflate(mContext, R.layout.emoticonstoolbar_item, null);
            ImageView iv_icon = (ImageView) toolBtnView.findViewById(R.id.iv_icon);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(mBtnWidth,
                    LayoutParams.MATCH_PARENT);
            iv_icon.setLayoutParams(imgParams);
            mEmoticonsBar.addView(toolBtnView);

            iv_icon.setImageDrawable(EmoticonLoader.getInstance(mContext).getDrawable(bean
                    .getIconUri()));

            mToolBtnList.add(iv_icon);

            final int finalI = i;
            iv_icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onTabClicked(finalI);
                    }
                }
            });
            i++;
        }
        setToolBtnSelect(0);
    }

    private OnEmoticonsTabChangeListener mListener;

    public interface OnEmoticonsTabChangeListener {
        void onTabClicked(int position);
    }

    public void setTabChangeListener(OnEmoticonsTabChangeListener listener) {
        mListener = listener;
    }
}
