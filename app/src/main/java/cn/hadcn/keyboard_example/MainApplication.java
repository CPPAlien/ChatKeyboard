package cn.hadcn.keyboard_example;

import android.app.Application;

/**
 * Created by 90Chris on 2015/10/8.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EmoticonsUtils.initEmoticonsDB(getApplicationContext());
    }
}
