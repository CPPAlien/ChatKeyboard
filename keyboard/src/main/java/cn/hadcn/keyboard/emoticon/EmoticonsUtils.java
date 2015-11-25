package cn.hadcn.keyboard.emoticon;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cn.hadcn.keyboard.db.EmoticonDBHelper;
import cn.hadcn.keyboard.emoticon.util.DefEmoticons;
import cn.hadcn.keyboard.emoticon.util.EmoticonsKeyboardBuilder;
import cn.hadcn.keyboard.utils.Utils;
import cn.hadcn.keyboard.utils.EmoticonBase;

public class EmoticonsUtils {

    public static void initEmoticonsDB(final Context context, final boolean isShowEmoji, final List<EmoticonSetBean> emoticonSetList) {
        if (!Utils.isInitDb(context)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    EmoticonDBHelper emoticonDbHelper = new EmoticonDBHelper(context);

                    /**
                     * FROM DRAWABLE
                     */
                    if ( isShowEmoji ) {
                        ArrayList<EmoticonBean> emojiArray = Utils.ParseData(DefEmoticons.emojiArray, EmoticonBean.FACE_TYPE_NORMAL, EmoticonBase.Scheme.DRAWABLE);
                        EmoticonSetBean emojiEmoticonSetBean = new EmoticonSetBean("emoji", 3, 7);
                        emojiEmoticonSetBean.setIconUri("drawable://icon_emoji");
                        emojiEmoticonSetBean.setItemPadding(20);
                        emojiEmoticonSetBean.setVerticalSpacing(10);
                        emojiEmoticonSetBean.setShowDelBtn(true);
                        emojiEmoticonSetBean.setEmoticonList(emojiArray);
                        emoticonDbHelper.insertEmoticonSet(emojiEmoticonSetBean);
                    }


                    if ( emoticonSetList != null && emoticonSetList.size() > 0 ) {
                        for ( EmoticonSetBean setBean : emoticonSetList ) {
                            emoticonDbHelper.insertEmoticonSet(setBean);
                        }
                    }

                    emoticonDbHelper.cleanup();
                    Utils.setIsInitDb(context, true);
                }
            }).start();
        }
    }

    public static EmoticonsKeyboardBuilder getBuilder(Context context) {

        EmoticonDBHelper emoticonDbHelper = new EmoticonDBHelper(context);
        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = emoticonDbHelper.queryAllEmoticonSet();
        emoticonDbHelper.cleanup();

        return new EmoticonsKeyboardBuilder.Builder()
                .setEmoticonSetBeanList(mEmoticonSetBeanList)
                .build();
    }
}
