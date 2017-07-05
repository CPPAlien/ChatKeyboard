package cn.hadcn.keyboard.media;

/**
 * @author chris
 */
public class MediaBean {
    private int id;
    private int drawableId;
    private String text;
    private MediaListener mediaListener;

    public interface MediaListener {
        void onMediaClick(int id);
    }

    public MediaBean(int id, int drawableId, String text, MediaListener mediaListener) {
        this.id = id;
        this.drawableId = drawableId;
        this.text = text;
        this.mediaListener = mediaListener;
    }

    public int getId() {
        return id;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public String getText() {
        return text;
    }

    public MediaListener getMediaListener() {
        return mediaListener;
    }
}
