package cn.hadcn.keyboard_example;

import android.app.Application;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hadcn.keyboard.emoticon.EmoticonBean;
import cn.hadcn.keyboard.emoticon.EmoticonSetBean;
import cn.hadcn.keyboard.emoticon.EmoticonsUtils;
import cn.hadcn.keyboard.utils.Utils;
import cn.hadcn.keyboard.utils.EmoticonBase;

/**
 * MainApplication
 * Created by 90Chris on 2015/10/8.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        List<EmoticonSetBean> emoticonSetBeans = new ArrayList<>();

        ArrayList<EmoticonBean> xhsfaceArray = Utils.ParseData(xhsemojiArray, EmoticonBean.FACE_TYPE_NORMAL, EmoticonBase.Scheme.ASSETS);
        EmoticonSetBean xhsEmoticonSetBean = new EmoticonSetBean("xhs", 3, 7);
        xhsEmoticonSetBean.setIconUri("assets://xhsemoji_19.png");
        xhsEmoticonSetBean.setItemPadding(20);
        xhsEmoticonSetBean.setVerticalSpacing(10);
        xhsEmoticonSetBean.setShowDelBtn(true);
        xhsEmoticonSetBean.setEmoticonList(xhsfaceArray);
        emoticonSetBeans.add(xhsEmoticonSetBean);

        try {
            EmoticonSetBean bean = Utils.ParseEmoticons(this, "emoticons/tusiji", EmoticonBase.Scheme.ASSETS);
            emoticonSetBeans.add(bean);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("keyboard-example", "read tusiji config.xml error");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e("keyboard-example", "parse tusiji config.xml error");
        }

        try {
            EmoticonSetBean bean = Utils.ParseEmoticons(this, "emoticons/qbi", EmoticonBase.Scheme.ASSETS);
            bean.setIsShownName(true);
            emoticonSetBeans.add(bean);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("keyboard-example", "read qbi config.xml error");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e("keyboard-example", "parse qbi config.xml error");
        }

        EmoticonsUtils.initEmoticonsDB(getApplicationContext(), true, emoticonSetBeans);
    }

    /*
    小红书表情
     */
    public static String[] xhsemojiArray = {
            "xhsemoji_1.png,[无语]",
            "xhsemoji_2.png,[汗]",
            "xhsemoji_3.png,[瞎]",
            "xhsemoji_4.png,[口水]",
            "xhsemoji_5.png,[酷]",
            "xhsemoji_6.png,[哭] ",
            "xhsemoji_7.png,[萌]",
            "xhsemoji_8.png,[挖鼻孔]",
            "xhsemoji_9.png,[好冷]",
            "xhsemoji_10.png,[白眼]",
            "xhsemoji_11.png,[晕]",
            "xhsemoji_12.png,[么么哒]",
            "xhsemoji_13.png,[哈哈]",
            "xhsemoji_14.png,[好雷]",
            "xhsemoji_15.png,[啊]",
            "xhsemoji_16.png,[嘘]",
            "xhsemoji_17.png,[震惊]",
            "xhsemoji_18.png,[刺瞎]",
            "xhsemoji_19.png,[害羞]",
            "xhsemoji_20.png,[嘿嘿]",
            "xhsemoji_21.png,[嘻嘻]"};
}
