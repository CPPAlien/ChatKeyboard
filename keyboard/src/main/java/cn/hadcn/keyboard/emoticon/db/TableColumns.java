package cn.hadcn.keyboard.emoticon.db;

import android.provider.BaseColumns;

public final class TableColumns {

    public interface EmoticonColumns extends BaseColumns {
        String EVENT_TYPE = "eventtype"; //1, for send in input area; 2, for sent in chat area directly
        String TAG = "tag";   //for matching, should be unique
        String NAME = "name";  //for displaying
        String ICON_URI = "icon_uri";  //uri for displaying in grid
        String MSG_URI = "msg_uri";  //for being sent in chat
        String EMOTICON_SET_NAME = "emoticon_set_name";  //emoticonset name
    }

    public interface EmoticonSetColumns extends BaseColumns {
        String NAME = "name";
        String LINE = "line";
        String ROW = "row";
        String ICON_URI = "iconuri";
        String IS_SHOW_DEL_BTN = "isshowdelbtn";
        String ITEM_PADDING = "itempadding";
        String HORIZONTAL_SPACING = "horizontalspacing";
        String VERTICAL_SPACING = "verticalspacing";
        String IS_SHOWN_NAME = "isshownname";
    }
}
