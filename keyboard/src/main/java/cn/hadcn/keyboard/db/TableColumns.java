package cn.hadcn.keyboard.db;

import android.provider.BaseColumns;

public final class TableColumns {

    private TableColumns() {}

    public interface EmoticonColumns extends BaseColumns {

        String EVENTTYPE = "eventtype";
        String CONTENT = "content";

        String ICONURI = "iconuri";

        String EMOTICONSET_NAME = "emoticonset_name";

    }

    public interface EmoticonSetColumns extends BaseColumns {
        String NAME = "name";
        String LINE = "line";
        String ROW = "row";
        String ICONURI = "iconuri";
        String ICONNAME = "iconname";
        String ISSHOWDELBTN = "isshowdelbtn";
        String ITEMPADDING = "itempadding";
        String HORIZONTALSPACING = "horizontalspacing";
        String VERTICALSPACING = "verticalspacing";
        String ISSHOWNNAME = "isshownname";
    }

}
