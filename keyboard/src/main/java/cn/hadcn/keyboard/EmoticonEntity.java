package cn.hadcn.keyboard;

import cn.hadcn.keyboard.utils.EmoticonBase;

/**
 * EmoticonEntity
 * @author chris
 */
public class EmoticonEntity {
    private String path;
    private EmoticonBase.Scheme scheme;

    public EmoticonEntity(String path, EmoticonBase.Scheme scheme) {
        this.path = path;
        this.scheme = scheme;
    }

    public String getPath() {
        return path;
    }

    public EmoticonBase.Scheme getScheme() {
        return scheme;
    }
}
