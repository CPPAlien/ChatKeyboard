package cn.hadcn.keyboard.emoticon;

public class EmoticonBean {

    public final static int FACE_TYPE_NORMAL = 0;
    public final static int FACE_TYPE_DEL = 1;
    public final static int FACE_TYPE_USERDEF = 2; // user downloaded emoticons

    private long eventType;
    private String iconUri;
    private String tag;
    private String name;

    public long getEventType() { return eventType; }

    public void setEventType(long eventType) { this.eventType = eventType; }

    public String getIconUri() {
        return iconUri;
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String fromChars(String chars) { return chars; }

    public static String fromChar(char ch) { return Character.toString(ch); }

    public static String fromCodePoint(int codePoint) { return newString(codePoint); }

    public static String newString(int codePoint) {
        if (Character.charCount(codePoint) == 1) {
            return String.valueOf(codePoint);
        } else {
            return new String(Character.toChars(codePoint));
        }
    }

    public EmoticonBean(long eventType , String iconUri , String tag, String name){
        this.eventType = eventType;
        this.iconUri = iconUri;
        this.tag = tag;
        this.name = name;
    }

    public EmoticonBean() {
    }
}
