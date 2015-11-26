package cn.hadcn.keyboard.emoticon.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import cn.hadcn.keyboard.R;
import cn.hadcn.keyboard.emoticon.EmoticonBean;
import cn.hadcn.keyboard.emoticon.util.EmoticonsKeyboardBuilder;
import cn.hadcn.keyboard.view.IndicatorView;

/**
 * EmoticonLayout
 * Created by 90Chris on 2015/11/19.
 */
public class EmoticonLayout extends RelativeLayout implements EmoticonsPageView.OnEmoticonsPageViewListener{
    Context mContext;
    EmoticonsPageView epvContent;
    IndicatorView ivIndicator;
    EmoticonsToolBarView etvToolBar;

    public EmoticonLayout(Context context) {
        super(context);
        mContext = context;
        init(mContext);
    }

    public EmoticonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(mContext);
    }

    public EmoticonLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(mContext);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.keyboard_bottom_emoticons, this);
        ivIndicator = (IndicatorView) findViewById(R.id.emoticon_indicator_view);
        epvContent = (EmoticonsPageView) findViewById(R.id.emoticon_page_view);
        etvToolBar = (EmoticonsToolBarView) findViewById(R.id.emoticon_page_toolbar);
        epvContent.setOnIndicatorListener(this);
        epvContent.setIViewListener(new IView() {
            @Override
            public void onItemClick(EmoticonBean bean) {
                mListener.onEmoticonItemClicked(bean);
            }

            @Override
            public void onPageChangeTo(int position) {
                etvToolBar.setToolBtnSelect(position);
            }
        });
        etvToolBar.setOnToolBarItemClickListener(new EmoticonsToolBarView.OnToolBarItemClickListener() {
            @Override
            public void onToolBarItemClick(int position) {
                epvContent.setPageSelect(position);
            }
        });
    }

    OnEmoticonListener mListener = null;
    public interface OnEmoticonListener {
        void onEmoticonItemClicked(EmoticonBean bean);
    }

    @Override
    public void emoticonsPageViewCountChanged(int count) {
        ivIndicator.setIndicatorCount(count);
    }

    @Override
    public void moveTo(int position) {
        ivIndicator.moveTo(position);
    }

    @Override
    public void moveBy(int oldPosition, int newPosition) {
        ivIndicator.moveTo(newPosition);
    }

    public void addToolView(int icon){
        if(etvToolBar != null && icon > 0){
            etvToolBar.addData(icon);
        }
    }

    public void addFixedView(View view , boolean isRight){
        if(etvToolBar != null){
            etvToolBar.addFixedView(view, isRight);
        }
    }

    public void setContents(EmoticonsKeyboardBuilder builder, OnEmoticonListener listener) {
        epvContent.setEmoticonContents(builder);
        etvToolBar.setEmoticonContents(builder);
        mListener = listener;
    }
}
