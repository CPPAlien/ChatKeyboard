package cn.hadcn.keyboard_example;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import cn.hadcn.keyboard.ChatKeyboardLayout;
import cn.hadcn.keyboard.EmoticonEntity;
import cn.hadcn.keyboard.utils.EmoticonBase;

/**
 * MainApplication
 * Created by 90Chris on 2015/10/8.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (!ChatKeyboardLayout.isEmoticonsDBInitSuccess(this)) {
            List<EmoticonEntity> entities = new ArrayList<>();
            entities.add(new EmoticonEntity("emoticons/xhs", EmoticonBase.Scheme.ASSETS));
            entities.add(new EmoticonEntity("emoticons/tusiji", EmoticonBase.Scheme.ASSETS));
            entities.add(new EmoticonEntity("emoticons/emoji", EmoticonBase.Scheme.ASSETS));
            ChatKeyboardLayout.initEmoticonsDB(this, true, entities);
        }
    }
}