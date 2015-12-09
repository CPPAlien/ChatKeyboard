package cn.hadcn.keyboard.emoticon.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
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
    private static ArrayList<EmoticonBean> mEmoticonBeans = new ArrayList<>();
    private static EmoticonHandler sEmoticonHandler = null;
    private Context mContext;
    EmoticonDBHelper emoticonDbHelper = null;

    public static EmoticonHandler getInstance( @NonNull Context context ) {
        if ( sEmoticonHandler == null ) {
            sEmoticonHandler = new EmoticonHandler( context );
        }
        return sEmoticonHandler;
    }

    private EmoticonHandler( Context context ) {
        mContext = context;
        emoticonDbHelper = new EmoticonDBHelper(context);
    }

    public EmoticonDBHelper getEmoticonDbHelper() {
        if ( emoticonDbHelper == null ) {
            emoticonDbHelper = new EmoticonDBHelper(mContext);
        }
        return emoticonDbHelper;
    }

    public ArrayList<EmoticonBean> loadEmoticonsToMemory() {
        mEmoticonBeans = emoticonDbHelper.queryAllEmoticonBeans();
        emoticonDbHelper.cleanup();

        return mEmoticonBeans;
    }

    public String getEmoticonUriByTag( String tag ) {
        return emoticonDbHelper.getUriByTag(tag);
    }

    public void setTextFace( String content, Spannable spannable, int size ) {
        if ( mEmoticonBeans == null ) {
            mEmoticonBeans = emoticonDbHelper.queryAllEmoticonBeans();
            emoticonDbHelper.cleanup();
        }
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
