package cn.hadcn.keyboard;

import android.content.Context;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hadcn.keyboard.db.EmoticonDBHelper;
import cn.hadcn.keyboard.emoticon.EmoticonBean;
import cn.hadcn.keyboard.emoticon.EmoticonSetBean;
import cn.hadcn.keyboard.emoticon.util.DefEmoticons;
import cn.hadcn.keyboard.emoticon.util.EmoticonsKeyboardBuilder;
import cn.hadcn.keyboard.utils.HadLog;
import cn.hadcn.keyboard.utils.Utils;
import cn.hadcn.keyboard.utils.EmoticonBase;

public class EmoticonsUtils {

    public static void initEmoticonsDB(final Context context, final boolean isShowEmoji, final List<EmoticonEntity> emoticonEntities) {
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

                    List<EmoticonSetBean> emoticonSetBeans = new ArrayList<>();
                    for ( EmoticonEntity entity : emoticonEntities ) {
                        try {
                            EmoticonSetBean bean = Utils.ParseEmoticons(context, entity.getPath(), entity.getScheme());
                            emoticonSetBeans.add(bean);
                        } catch (IOException e) {
                            e.printStackTrace();
                            HadLog.e( String.format("read %s config.xml error", entity.getPath()) );
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                            HadLog.e( String.format("parse %s config.xml error", entity.getPath()) );
                        }
                    }

                    for ( EmoticonSetBean setBean : emoticonSetBeans ) {
                        emoticonDbHelper.insertEmoticonSet(setBean);
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
