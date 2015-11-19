package cn.hadcn.keyboard.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.hadcn.keyboard.R;


public class IndicatorView extends LinearLayout {

    private Context mContext;
    private ArrayList<ImageView> mImageViews = new ArrayList<>();
    private Drawable bmpSelect;
    private Drawable bmpNormal;
    private int mMargin = 0;

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.setOrientation(HORIZONTAL);

        bmpSelect= ContextCompat.getDrawable(mContext, R.drawable.indicator_point_select);
        bmpNormal = ContextCompat.getDrawable(mContext, R.drawable.indicator_point_normal);
        mMargin = getResources().getDimensionPixelSize(R.dimen.indicator_margin);
    }

    public void setIndicatorCount(int count){
        mImageViews.clear();
        this.removeAllViews();
        for (int i = 0 ; i < count ; i++) {
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins( mMargin, 0, mMargin, 0 );

            ImageView imageView = new ImageView(mContext);

            if (i == 0){
                imageView.setImageDrawable(bmpSelect);
                this.addView(imageView, layoutParams);
            } else {
                imageView.setImageDrawable(bmpNormal);
                this.addView(imageView, layoutParams);
            }
            mImageViews.add(imageView);
        }
    }

    public void moveTo(int position){
        for ( ImageView iv : mImageViews ) {
            iv.setImageDrawable(bmpNormal);
        }
        mImageViews.get(position).setImageDrawable(bmpSelect);
    }
}
