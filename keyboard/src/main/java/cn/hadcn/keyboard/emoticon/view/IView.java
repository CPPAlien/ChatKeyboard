package cn.hadcn.keyboard.emoticon.view;

import cn.hadcn.keyboard.emoticon.EmoticonBean;

public interface IView {
    void onItemClick(EmoticonBean bean);
    void onPageChangeTo(int position);
}
