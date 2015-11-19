package cn.hadcn.keyboard_example;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;

import cn.hadcn.keyboard.ChatKeyboardLayout;
import cn.hadcn.keyboard.db.DBHelper;
import cn.hadcn.keyboard.emoticon.EmoticonSetBean;
import cn.hadcn.keyboard.media.MediaBean;
import cn.hadcn.keyboard.utils.EmoticonsKeyboardBuilder;
import cn.hadcn.keyboard.utils.imageloader.ImageBase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaBean.MediaListener {
    ChatKeyboardLayout keyboardLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keyboardLayout = (ChatKeyboardLayout)findViewById(R.id.kv_bar);
        keyboardLayout.setEmoticonContents(EmoticonsUtils.getBuilder(this));


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
    }

    protected void displayImageFromAssets(String imageUri, ImageView imageView) throws IOException {
        String filePath = ImageBase.Scheme.ASSETS.crop(imageUri);
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

        DBHelper dbHelper = new DBHelper(context);
        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = dbHelper.queryAllEmoticonSet();
        dbHelper.cleanup();

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
}
