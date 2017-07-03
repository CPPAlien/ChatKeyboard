package cn.hadcn.keyboard.emoticon.util;

import java.util.ArrayList;
import java.util.List;

import cn.hadcn.keyboard.emoticon.EmoticonSetBean;

public class EmoticonsKeyboardBuilder {

    public Builder builder;

    public EmoticonsKeyboardBuilder(Builder builder){
        this.builder = builder;
    }

    public static class Builder {

        List<EmoticonSetBean> mEmoticonSetBeanList = new ArrayList<EmoticonSetBean>();

        public Builder(){ }

        public List<EmoticonSetBean> getEmoticonSetBeanList() { return mEmoticonSetBeanList; }

        public Builder setEmoticonSetBeanList(List<EmoticonSetBean> mEmoticonSetBeanList) { this.mEmoticonSetBeanList = mEmoticonSetBeanList;  return this;}

        public EmoticonsKeyboardBuilder build() { return new EmoticonsKeyboardBuilder(this); }
    }
}
