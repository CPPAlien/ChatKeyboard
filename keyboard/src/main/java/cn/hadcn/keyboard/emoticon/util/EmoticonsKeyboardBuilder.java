package cn.hadcn.keyboard.emoticon.util;


import java.util.ArrayList;

import cn.hadcn.keyboard.emoticon.EmoticonSetBean;

public class EmoticonsKeyboardBuilder {

    public Builder builder;

    public EmoticonsKeyboardBuilder(Builder builder){
        this.builder = builder;
    }

    public static class Builder {

        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = new ArrayList<EmoticonSetBean>();

        public Builder(){ }

        public ArrayList<EmoticonSetBean> getEmoticonSetBeanList() { return mEmoticonSetBeanList; }

        public Builder setEmoticonSetBeanList(ArrayList<EmoticonSetBean> mEmoticonSetBeanList) { this.mEmoticonSetBeanList = mEmoticonSetBeanList;  return this;}

        public EmoticonsKeyboardBuilder build() { return new EmoticonsKeyboardBuilder(this); }
    }
}
