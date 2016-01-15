# ChatKeyboard
A powerful and easy using keyboard lib includes emoticons, audio recording, multi media keyboard, etc.


###Demo
<div class='row'>
    <img src='http://7xq276.com2.z0.glb.qiniucdn.com/keyboard-demo.gif' width="250px"/>
</div>

[Apk Download](http://7xq276.com2.z0.glb.qiniucdn.com/keyboard.apk)

###Import

use maven to import dependency in gradle

```
repositories{
    maven { url "https://jitpack.io" }
}
dependencies {
    compile 'com.github.CPPAlien:ChatKeyboard:1.0.0'
}
```
download [keyboard-1.0.0.aar](http://7xq276.com2.z0.glb.qiniucdn.com/keyboard-1.0.0.aar)

dependency
```
com.android.support:support-v4:23.1.0
```

###how to use
Use ChatKeyboardLayout make your layout contains keyboard.


```
<cn.hadcn.keyboard.ChatKeyboardLayout
        android:id="@+id/kv_bar"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:sendBtnBg="@drawable/send_button_bg">
        ...your layout
</cn.hadcn.keyboard.ChatKeyboardLayout>
```
**Caution: ChatKeyboardLayout can only include one child.**

other usage
please refer to the demo code

Thanks for [xhsEmoticonsKeyboard](https://github.com/w446108264/XhsEmoticonsKeyboard) powered by w446108264.