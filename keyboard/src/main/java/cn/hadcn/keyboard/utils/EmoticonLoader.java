package cn.hadcn.keyboard.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.hadcn.keyboard.emoticon.util.EmoticonHandler;

/**
 * @author chris
 */
public class EmoticonLoader implements EmoticonBase {
    protected final Context mContext;
    private volatile static EmoticonLoader instance;
    private final static String emoticonConfigFileName = "config.xml";

    public static EmoticonLoader getInstance(Context context) {
        if (instance == null) {
            synchronized (EmoticonLoader.class) {
                if (instance == null) {
                    instance = new EmoticonLoader(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public EmoticonLoader(Context context) {
        this.mContext = context.getApplicationContext();
    }

    /**
     * get the config file stream in emoticon directory, name of config
     * is config.xml
     *
     * @param path   path of directory
     * @param scheme scheme
     * @return file input stream
     */
    public InputStream getConfigStream(String path, Scheme scheme) {
        switch (scheme) {
            case FILE:
                try {
                    File file = new File(path + "/" + emoticonConfigFileName);
                    if (file.exists()) {
                        return new FileInputStream(file);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            case ASSETS:
                try {
                    return mContext.getAssets().open(path + "/" + emoticonConfigFileName);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
        }
        return null;
    }

    /**
     * get input stream of emoticon
     *
     * @param imageUri emoticon uri
     * @return input stream
     */
    private InputStream getInputStream(String imageUri) {
        switch (Scheme.ofUri(imageUri)) {
            case FILE:
                String filePath = Scheme.FILE.crop(imageUri);
                try {
                    File file = new File(filePath);
                    if (file.exists()) {
                        return new FileInputStream(file);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            case ASSETS:
                String assetsPath = Scheme.ASSETS.crop(imageUri);
                try {
                    return mContext.getAssets().open(assetsPath);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
        }
        return null;
    }

    public InputStream getInputStreamByTag(String tag) {
        String uri = EmoticonHandler.getInstance(mContext).getEmoticonUriByTag(tag);
        return getInputStream(uri);
    }

    public Drawable getDrawableByTag(String tag) {
        String uri = EmoticonHandler.getInstance(mContext).getEmoticonUriByTag(tag);
        return getDrawable(uri);
    }

    public Drawable getDrawable(String imageUri) {
        switch (Scheme.ofUri(imageUri)) {
            case FILE:
                String filePath = Scheme.FILE.crop(imageUri);
                Bitmap fileBitmap;
                try {
                    fileBitmap = BitmapFactory.decodeFile(filePath);
                    return new BitmapDrawable(mContext.getResources(), fileBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            case CONTENT:
                return null;
            case ASSETS:
                String assetsPath = Scheme.ASSETS.crop(imageUri);
                Bitmap assetsBitmap;
                try {
                    assetsBitmap = BitmapFactory.decodeStream(mContext.getAssets().open
                            (assetsPath));
                    return new BitmapDrawable(mContext.getResources(), assetsBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            case DRAWABLE:
                String drawableIdString = Scheme.DRAWABLE.crop(imageUri);
                int resID = mContext.getResources().getIdentifier(drawableIdString, "drawable",
                        mContext.getPackageName());
                return mContext.getResources().getDrawable((int) resID);
            case UNKNOWN:
            default:
                return null;
        }
    }
}
