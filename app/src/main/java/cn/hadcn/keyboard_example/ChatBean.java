package cn.hadcn.keyboard_example;

/**
 * ChatBean
 * Created by 90Chris on 2015/11/24.
 */
public class ChatBean {
    private String tag;
    private String textMsg;

    public ChatBean(String tag, String textMsg) {
        this.tag = tag;
        this.textMsg = textMsg;
    }

    public String getTag() {
        return tag;
    }

    public String getTextMsg() {
        return textMsg;
    }
}
