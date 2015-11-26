package cn.hadcn.keyboard_example;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import cn.hadcn.keyboard.ChatKeyboardLayout;
import cn.hadcn.keyboard.media.MediaBean;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaBean.MediaListener, ChatKeyboardLayout.OnChatKeyBoardListener {
    ChatKeyboardLayout keyboardLayout = null;
    SimpleChatAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keyboardLayout = (ChatKeyboardLayout)findViewById(R.id.kv_bar);
        keyboardLayout.showEmoticons();
/*

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
        keyboardLayout.showMedias(popupModels);
        keyboardLayout.setOnKeyBoardBarListener(this);
        ListView listView = (ListView)findViewById(R.id.list_view);
        mAdapter = new SimpleChatAdapter(this);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                keyboardLayout.hideBottomPop();
                return false;
            }
        });
        listView.setAdapter(mAdapter);*/
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
        mAdapter.addItem(new ChatBean(null, msg));
        keyboardLayout.clearInputArea();
    }

    @Override
    public void onRecordingAction(ChatKeyboardLayout.RecordingAction action) {

    }

    @Override
    public void onUserDefEmoticonClicked(String name, String uri) {
        mAdapter.addItem(new ChatBean(uri, null));
    }
}
