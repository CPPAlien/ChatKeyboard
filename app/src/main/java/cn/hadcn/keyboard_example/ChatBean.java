package cn.hadcn.keyboard_example;

/**
 * ChatBean
 * Created by 90Chris on 2015/11/24.
 */
public class ChatBean {
    private String emoticonUri;
    private String textMsg;

    public ChatBean(String emoticonUri, String textMsg) {
        this.emoticonUri = emoticonUri;
        this.textMsg = textMsg;
    }

    public String getEmoticonUri() {
        return emoticonUri;
    }

    public String getTextMsg() {
        return textMsg;
    }
}
