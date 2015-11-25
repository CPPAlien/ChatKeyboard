package cn.hadcn.keyboard_example;

import android.app.Application;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        try {
            EmoticonSetBean bean = Utils.ParseEmoticons(this, "emoticons/xhs", EmoticonBase.Scheme.ASSETS);
            emoticonSetBeans.add(bean);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("keyboard-example", "read tusiji config.xml error");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e("keyboard-example", "parse tusiji config.xml error");
        }

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


}
