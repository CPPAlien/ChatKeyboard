package cn.hadcn.keyboard.emoticon.view;

import android.content.Context;
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
import cn.hadcn.keyboard.utils.Utils;
import cn.hadcn.keyboard.utils.EmoticonLoader;

public class EmoticonsToolBarView extends RelativeLayout {

    private Context mContext;
    private HorizontalScrollView hsv_toolbar;
    private LinearLayout ly_tool;

    private List<EmoticonSetBean> mEmoticonSetBeanList;
    private ArrayList<ImageView> mToolBtnList = new ArrayList<ImageView>();
    private int mBtnWidth = 60;

    public EmoticonsToolBarView(Context context) {
        this(context, null);
    }

    public EmoticonsToolBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.emoticonstoolbar_view, this);
        this.mContext = context;
        findView();
    }

    private void findView() {
        hsv_toolbar = (HorizontalScrollView) findViewById(R.id.hsv_toolbar);
        ly_tool = (LinearLayout) findViewById(R.id.ly_tool);
    }

    private void scrollToBtnPosition(final int position){
        int childCount = ly_tool.getChildCount();
        if(position < childCount){
            hsv_toolbar.post(new Runnable() {
                @Override
                public void run() {
                    int mScrollX = hsv_toolbar.getScrollX();

                    //todo test adding many views
                    int childX = ly_tool.getChildAt(position).getLeft();
                    if ( childX < mScrollX ) {
                        hsv_toolbar.scrollTo( childX, 0 );
                        return;
                    }

                    int childWidth = (int)ly_tool.getChildAt(position).getWidth();
                    int hsvWidth = hsv_toolbar.getWidth();
                    int childRight = childX + childWidth;
                    int scrollRight = mScrollX + hsvWidth;

                    if(childRight > scrollRight){
                        hsv_toolbar.scrollTo(childRight - scrollRight, 0);
                    }
                }
            });
        }
    }

    public void setToolBtnSelect(int select) {
        scrollToBtnPosition(select);
        for (int i = 0; i < mToolBtnList.size(); i++) {
            if (select == i) {
                mToolBtnList.get(i).setBackgroundColor(getResources().getColor(R.color.toolbar_btn_select));
            } else {
                mToolBtnList.get(i).setBackgroundColor(getResources().getColor(R.color.toolbar_btn_nomal));
            }
        }
    }

    public void setBtnWidth(int width){
        mBtnWidth = width;
    }

    public void addData(int rec){
        if(ly_tool != null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View toolBtnView = inflater.inflate(R.layout.emoticonstoolbar_item,null);
            ImageView iv_icon = (ImageView)toolBtnView.findViewById(R.id.iv_icon);
            iv_icon.setImageResource(rec);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(Utils.dip2px(mContext, mBtnWidth), LayoutParams.MATCH_PARENT);
            iv_icon.setLayoutParams(imgParams);
            ly_tool.addView(toolBtnView);
            final int position = mToolBtnList.size();
            mToolBtnList.add(iv_icon);
            iv_icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListeners != null && !mItemClickListeners.isEmpty()) {
                        for (OnToolBarItemClickListener listener : mItemClickListeners) {
                            listener.onToolBarItemClick(position);
                        }
                    }
                }
            });
        }
    }

    private int getIdValue(){
        int childCount = getChildCount();
        int id = 1;
        if(childCount == 0){
            return id;
        }
        boolean isKeep = true;
        while (isKeep){
            isKeep = false;
            Random random = new Random();
            id = random.nextInt(100);
            for(int i = 0 ; i < childCount ; i++){
                if(getChildAt(i).getId() == id){
                    isKeep = true;
                    break;
                }
            }
        }
        return id;
    }

    public void addFixedView(View view , boolean isRight){
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        LayoutParams hsvParams = (LayoutParams) hsv_toolbar.getLayoutParams();
        if(view.getId() <= 0){
            view.setId(getIdValue());
        }
        if(isRight){
            params.addRule(ALIGN_PARENT_RIGHT);
            hsvParams.addRule(LEFT_OF, view.getId());
        }
        else{
            params.addRule(ALIGN_PARENT_LEFT);
            hsvParams.addRule(RIGHT_OF,view.getId());
        }
        addView(view,params);
        hsv_toolbar.setLayoutParams(hsvParams);
    }

    public void setEmoticonContents(EmoticonsKeyboardBuilder builder) {
        mEmoticonSetBeanList = builder.builder == null ? null : builder.builder.getEmoticonSetBeanList();
        if(mEmoticonSetBeanList == null){
            return;
        }

        int i = 0;
        for(EmoticonSetBean bean : mEmoticonSetBeanList){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View toolBtnView = inflater.inflate(R.layout.emoticonstoolbar_item, null);
            ImageView iv_icon = (ImageView)toolBtnView.findViewById(R.id.iv_icon);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(Utils.dip2px(mContext, mBtnWidth), LayoutParams.MATCH_PARENT);
            iv_icon.setLayoutParams(imgParams);
            ly_tool.addView(toolBtnView);

            iv_icon.setImageDrawable(EmoticonLoader.getInstance(mContext).getDrawable(bean.getIconUri()));

            mToolBtnList.add(iv_icon);

            final int finalI = i;
            iv_icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListeners != null && !mItemClickListeners.isEmpty()) {
                        for (OnToolBarItemClickListener listener : mItemClickListeners) {
                            listener.onToolBarItemClick(finalI);
                        }
                    }
                }
            });
            i++;
        }
        setToolBtnSelect(0);
    }

    private List<OnToolBarItemClickListener> mItemClickListeners;
    public interface OnToolBarItemClickListener {
        void onToolBarItemClick(int position);
    }
    public void addOnToolBarItemClickListener(OnToolBarItemClickListener listener) {
        if (mItemClickListeners == null) {
            mItemClickListeners = new ArrayList<OnToolBarItemClickListener>();
        }
        mItemClickListeners.add(listener);
    }
    public void setOnToolBarItemClickListener(OnToolBarItemClickListener listener) { addOnToolBarItemClickListener(listener);}
}
