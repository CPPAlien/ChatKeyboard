package cn.hadcn.keyboard.emoticon.util;

import android.content.Context;

import java.util.ArrayList;

import cn.hadcn.keyboard.db.EmoticonDBHelper;
import cn.hadcn.keyboard.emoticon.EmoticonBean;

/**
 * EmoticonHandler
 * Created by 90Chris on 2015/11/25.
 */
public class EmoticonHandler {
    private static ArrayList<EmoticonBean> mEmoticonBeans = null;

    public static ArrayList<EmoticonBean> getAllEmoticons(Context context) {
        if ( mEmoticonBeans == null ) {
            EmoticonDBHelper emoticonDbHelper = new EmoticonDBHelper(context);
            mEmoticonBeans = emoticonDbHelper.queryAllEmoticonBeans();
            emoticonDbHelper.cleanup();
        }
        return mEmoticonBeans;
    }
}
