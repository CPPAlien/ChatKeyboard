package cn.hadcn.keyboard.emoticon;

import java.util.List;

/**
 * @author chris
 */
public class EmoticonSetBean {
    /**
     * name of set
     */
    private String name;
    /**
     * line number
     */
    private int line;
    /**
     * row number
     */
    private int row;
    /**
     * icon
     */
    private String iconUri;
    /**
     * is show delete button
     */
    private boolean isShowDelBtn;
    /**
     * item padding
     */
    private int itemPadding;
    private int horizontalSpacing;
    private int verticalSpacing;
    private List<EmoticonBean> emoticonList;
    private boolean isShownName;

    public EmoticonSetBean() {
    }

    public EmoticonSetBean(String name, int line, int row) {
        this.name = name;
        this.line = line;
        this.row = row;
    }

    public EmoticonSetBean(String name, int line, int row, String iconUri, boolean isShowDelBtn,
                           boolean isShownName,
                           int itemPadding, int horizontalSpacing, int verticalSpacing,
                           List<EmoticonBean> emoticonList) {
        this.name = name;
        this.line = line;
        this.row = row;
        this.iconUri = iconUri;
        this.isShowDelBtn = isShowDelBtn;
        this.itemPadding = itemPadding;
        this.horizontalSpacing = horizontalSpacing;
        this.verticalSpacing = verticalSpacing;
        this.emoticonList = emoticonList;
        this.isShownName = isShownName;
    }

    public boolean isShownName() {
        return isShownName;
    }

    public void setIsShownName(boolean isShownName) {
        this.isShownName = isShownName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getIconUri() {
        return iconUri;
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }

    public boolean isShowDelBtn() {
        return isShowDelBtn;
    }

    public void setShowDelBtn(boolean isShowDelBtn) {
        this.isShowDelBtn = isShowDelBtn;
    }

    public int getItemPadding() {
        return itemPadding;
    }

    public void setItemPadding(int itemPadding) {
        this.itemPadding = itemPadding;
    }

    public int getHorizontalSpacing() {
        return horizontalSpacing;
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
    }

    public int getVerticalSpacing() {
        return verticalSpacing;
    }

    public void setVerticalSpacing(int verticalSpacing) {
        this.verticalSpacing = verticalSpacing;
    }

    public List<EmoticonBean> getEmoticonList() {
        return emoticonList;
    }

    public void setEmoticonList(List<EmoticonBean> emoticonList) {
        this.emoticonList = emoticonList;
    }
}
