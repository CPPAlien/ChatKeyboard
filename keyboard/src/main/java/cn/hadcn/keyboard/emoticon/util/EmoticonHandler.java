package cn.hadcn.keyboard.emoticon.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;

import java.util.ArrayList;

import cn.hadcn.keyboard.emoticon.db.EmoticonDBHelper;
import cn.hadcn.keyboard.emoticon.EmoticonBean;
import cn.hadcn.keyboard.utils.EmoticonLoader;
import cn.hadcn.keyboard.view.VerticalImageSpan;

/**
 * EmoticonHandler
 * Created by 90Chris on 2015/11/25.
 */
public class EmoticonHandler {
    private static ArrayList<EmoticonBean> mEmoticonBeans = null;
    private static EmoticonHandler sEmoticonHandler = null;
    private Context mContext;

    public static EmoticonHandler getInstance() {
        if ( sEmoticonHandler == null ) {
            sEmoticonHandler = new EmoticonHandler();
        }
        return sEmoticonHandler;
    }

    private EmoticonHandler() {

    }

    public ArrayList<EmoticonBean> init(Context context) {
        mContext = context;
        if ( mEmoticonBeans == null ) {
            EmoticonDBHelper emoticonDbHelper = new EmoticonDBHelper(context);
            mEmoticonBeans = emoticonDbHelper.queryAllEmoticonBeans();
            emoticonDbHelper.cleanup();
        }
        return mEmoticonBeans;
    }

    public void setTextFace( String content, Spannable spannable, int size ) {
        if ( content.length() <= 0 ) {
            return;
        }
        int keyIndex = 0;
        for ( EmoticonBean bean : mEmoticonBeans ) {
            int keyLength = bean.getTag().length();
            while ( keyIndex >= 0 ) {
                keyIndex = content.indexOf(bean.getTag(), keyIndex);  //when do not find, get -1
                if ( keyIndex < 0 ) {
                    break;
                }
                Drawable drawable = EmoticonLoader.getInstance(mContext).getDrawable(bean.getIconUri());
                drawable.setBounds(0, 0, size, size);
                VerticalImageSpan imageSpan = new VerticalImageSpan(drawable);
                spannable.setSpan(imageSpan, keyIndex, keyIndex + keyLength, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                keyIndex += keyLength;
            }
            keyIndex = 0;
        }
    }
}
