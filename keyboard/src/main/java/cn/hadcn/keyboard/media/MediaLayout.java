package cn.hadcn.keyboard.media;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import java.util.List;

import cn.hadcn.keyboard.R;
import cn.hadcn.keyboard.utils.Utils;
import cn.hadcn.keyboard.view.IndicatorView;


/**
 * MediaLayout
 * Created by 90Chris on 2015/11/19.
 */
public class MediaLayout extends RelativeLayout implements ViewPager.OnPageChangeListener{
    ViewPager vpContent;
    IndicatorView ivIndicator;
    Context mContext;

    public MediaLayout(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public MediaLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init( context );
    }

    public MediaLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init( context );
    }

    private void init( Context context ) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.keyboard_bottom_media, this);
        vpContent = (ViewPager) findViewById(R.id.popup_media_pager);
        ivIndicator = (IndicatorView)findViewById(R.id.popup_media_indicator);
        vpContent.setOnPageChangeListener(this);  //compatible for android 22
    }

    public void setContents( List<MediaBean> mediaContents ) {
        int size = getResources().getDimensionPixelSize(R.dimen.media_item_size);
        MediaPagerAdapter adapter = new MediaPagerAdapter(mContext, mediaContents, size);
        vpContent.setAdapter(adapter);
        ivIndicator.setIndicatorCount(adapter.getPageNum());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        ivIndicator.moveTo(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
