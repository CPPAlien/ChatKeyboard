package cn.hadcn.keyboard.view.I;

import cn.hadcn.keyboard.emoticon.EmoticonBean;

public interface IView {
    void onItemClick(EmoticonBean bean);
    void onItemDisplay(EmoticonBean bean);
    void onPageChangeTo(int position);
}
