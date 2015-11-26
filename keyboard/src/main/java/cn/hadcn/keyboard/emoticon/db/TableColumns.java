package cn.hadcn.keyboard.emoticon.db;

import android.provider.BaseColumns;

public final class TableColumns {

    public interface EmoticonColumns extends BaseColumns {
        String EVENTTYPE = "eventtype";
        String TAG = "tag";   //for matching, should be unique
        String NAME = "name";  //for displaying
        String ICONURI = "iconuri";
        String EMOTICONSET_NAME = "emoticonset_name";  //unique
    }

    public interface EmoticonSetColumns extends BaseColumns {
        String NAME = "name";
        String LINE = "line";
        String ROW = "row";
        String ICONURI = "iconuri";
        String ISSHOWDELBTN = "isshowdelbtn";
        String ITEMPADDING = "itempadding";
        String HORIZONTALSPACING = "horizontalspacing";
        String VERTICALSPACING = "verticalspacing";
        String ISSHOWNNAME = "isshownname";
    }

}
