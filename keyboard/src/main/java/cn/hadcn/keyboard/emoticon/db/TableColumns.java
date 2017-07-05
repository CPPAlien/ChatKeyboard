package cn.hadcn.keyboard.emoticon.db;

import android.provider.BaseColumns;

/**
 * @author chris
 */
final class TableColumns {
    /**
     * the columns definition for emoticon item
     */
    class EmoticonItem implements BaseColumns {
        private EmoticonItem() {
        }
        //1, for send in input area; 2, for sent in chat area directly
        static final String EVENT_TYPE = "event_type";
        static final String TAG = "tag";   //for matching, should be unique
        static final String NAME = "name";  //for displaying
        static final String ICON_URI = "icon_uri";  //uri for displaying in grid
        static final String MSG_URI = "msg_uri";  //for being sent in chat
        static final String EMOTICON_SET_NAME = "emoticon_set_name";  //emoticon set name
    }

    /**
     * the columns definition for emoticons set
     */
    class EmoticonSet implements BaseColumns {
        private EmoticonSet() {
        }

        static final String NAME = "name";
        static final String LINE = "line";
        static final String ROW = "row";
        static final String ICON_URI = "icon_uri";
        static final String IS_SHOW_DEL_BTN = "is_show_del_btn";
        static final String ITEM_PADDING = "item_padding";
        static final String HORIZONTAL_SPACING = "horizontal_spacing";
        static final String VERTICAL_SPACING = "vertical_spacing";
        static final String IS_SHOW_NAME = "is_show_name";
    }
}
