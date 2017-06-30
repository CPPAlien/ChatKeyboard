# ChatKeyboard 

[![](https://img.shields.io/badge/language-Java-green.svg)](https://github.com/CPPAlien/ChatKeyboard)

A powerful and easy using keyboard lib includes emoticons, audio recording, multi media keyboard, etc.


### Demo
<div class='row'>
    <img src='http://7xq276.com2.z0.glb.qiniucdn.com/keyboard-demo.gif' width="250px"/>
</div>

[Apk Download](http://7xq276.com2.z0.glb.qiniucdn.com/keyboard.apk)

### Import

use maven to import dependency in gradle

```
repositories{
    maven { url "https://jitpack.io" }
}
dependencies {
    compile 'com.github.CPPAlien:ChatKeyboard:1.0.1'
}
```

You Can Use The Latest Version：[![](https://jitpack.io/v/CPPAlien/ChatKeyboard.svg)](https://jitpack.io/#CPPAlien/ChatKeyboard)

dependency
```
com.android.support:support-v4:23.1.0
```

### How to use

1, Use ChatKeyboardLayout make your layout contains keyboard.
```
<cn.hadcn.keyboard.ChatKeyboardLayout
        android:id="@+id/kv_bar"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:sendBtnBg="@drawable/send_button_bg">
        ...your layout
</cn.hadcn.keyboard.ChatKeyboardLayout>
```
**Notice: ChatKeyboardLayout can only include one child.**

2, custom your emoticon and stick keyboard
```java
if (!ChatKeyboardLayout.isEmoticonInitSuccess(this)) {
	List<EmoticonEntity> entities = new ArrayList<>();
	entities.add(new EmoticonEntity("emoticons/xhs", EmoticonBase.Scheme.ASSETS));
	entities.add(new EmoticonEntity("emoticons/tusiji", EmoticonBase.Scheme.ASSETS));
	ChatKeyboardLayout.initEmoticonsDB(this, true, entities);
}
```
**Notice: Add the code above before the ChatKeyboardLayout used, better in onCreate of Application**

3, keyboard listener
```java
public interface OnChatKeyBoardListener {
        /**
         * When send button clicked
         *
         * @param text content in input area
         */
        void onSendButtonClicked(String text);

        /**
         * When user input or delete text in input area
         *
         * @param text changing text
         */
        void onInputTextChanged(String text);

        /**
         * Recording button action {@link RecordingAction}
         *
         * @param action action
         */
        void onRecordingAction(RecordingAction action);

        /**
         * When sticker defined by developer clicked
         *
         * @param tag sticker tag
         * @param uri sticker location uri
         */
        void onUserDefEmoticonClicked(String tag, String uri);

        /**
         * when keyboard popped or back, get the pixels of the height include keyboard bar
         *
         * @param height pixel height
         */
        void onKeyboardHeightChanged(int height);
} 
```
You can use `SimpleOnChatKeyboardListener` instead of the interface above, so you can just listen these you want listen.

### These you should know

1. Make sure The keyboard layout bottom align the screen bottom.
2. Make sure the keyboard layout height at least 2/3 of you screen height.

other usage

please refer to the demo code



Thanks for [xhsEmoticonsKeyboard](https://github.com/w446108264/XhsEmoticonsKeyboard) powered by w446108264.