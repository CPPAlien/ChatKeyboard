package cn.hadcn.keyboard_example;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hadcn.keyboard.ChatKeyboardLayout;
import cn.hadcn.keyboard.db.EmoticonDBHelper;
import cn.hadcn.keyboard.emoticon.EmoticonSetBean;
import cn.hadcn.keyboard.media.MediaBean;
import cn.hadcn.keyboard.emoticon.util.EmoticonsKeyboardBuilder;
import cn.hadcn.keyboard.utils.imageloader.EmoticonBase;
import cn.hadcn.keyboard.utils.imageloader.EmoticonLoader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaBean.MediaListener, ChatKeyboardLayout.OnChatKeyBoardListener {
    ChatKeyboardLayout keyboardLayout = null;
    SimpleAdapter mAdapter;
    List<Map<String, Drawable>> mMapList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keyboardLayout = (ChatKeyboardLayout)findViewById(R.id.kv_bar);
        keyboardLayout.setEmoticonContents(this);


        ArrayList<MediaBean> popupModels = new ArrayList<>();
        popupModels.add(new MediaBean(0, R.drawable.icon_camera, "拍照", this));
        popupModels.add(new MediaBean(1, R.drawable.icon_photo, "照片", this));
        popupModels.add(new MediaBean(2, R.drawable.icon_camera, "拍照", this));
        popupModels.add(new MediaBean(3, R.drawable.icon_photo, "照片", this));
        popupModels.add(new MediaBean(4, R.drawable.icon_camera, "拍照", this));
        popupModels.add(new MediaBean(5, R.drawable.icon_photo, "照片", this));
        popupModels.add(new MediaBean(6, R.drawable.icon_camera, "拍照", this));
        popupModels.add(new MediaBean(7, R.drawable.icon_photo, "照片", this));
        popupModels.add(new MediaBean(8, R.drawable.icon_camera, "拍照", this));
        popupModels.add(new MediaBean(9, R.drawable.icon_photo, "照片", this));
        keyboardLayout.setMediaContents(popupModels);
        keyboardLayout.setOnKeyBoardBarListener(this);

        ListView listView = (ListView)findViewById(R.id.list_view);
        mMapList = new ArrayList<>();
        Map<String, Drawable> map = new HashMap<>();
        map.put("image", ContextCompat.getDrawable(this, R.drawable.ic_launcher));
        mMapList.add(map);

        mAdapter = new SimpleAdapter(this, mMapList, R.layout.item_list, new String[]{"image"}, new int[]{R.id.item_image});
        mAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if ( view instanceof ImageView && o instanceof Drawable ) {
                    ((ImageView) view).setImageDrawable((Drawable)o);
                    return true;
                }
                return false;
            }
        });
        listView.setAdapter(mAdapter);
    }

    protected void displayImageFromAssets(String imageUri, ImageView imageView) throws IOException {
        String filePath = EmoticonBase.Scheme.ASSETS.crop(imageUri);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);
        return ;
    }

    public static EmoticonsKeyboardBuilder getBuilder(Context context) {

        EmoticonDBHelper emoticonDbHelper = new EmoticonDBHelper(context);
        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = emoticonDbHelper.queryAllEmoticonSet();
        emoticonDbHelper.cleanup();

        return new EmoticonsKeyboardBuilder.Builder()
                .setEmoticonSetBeanList(mEmoticonSetBeanList)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onMediaClick(int id) {

    }

    @Override
    public void onSendBtnClick(String msg) {

    }

    @Override
    public void onRecordingAction(ChatKeyboardLayout.RecordingAction action) {

    }

    @Override
    public void onUserDefEmoticonClicked(String name, String uri) {
        Map<String, Drawable> map = new HashMap<>();
        map.put("image", EmoticonLoader.getInstance(this).getDrawable(uri));
        mMapList.add(map);
        mAdapter.notifyDataSetChanged();
    }
}
