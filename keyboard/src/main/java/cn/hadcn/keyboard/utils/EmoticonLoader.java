package cn.hadcn.keyboard.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class EmoticonLoader implements EmoticonBase {
    protected final Context mContext;
    private volatile static EmoticonLoader instance;
    private final static String emoticonConfigFileName = "config.xml";

    public static EmoticonLoader getInstance(Context context) {
        if (instance == null) {
            synchronized (EmoticonLoader.class) {
                if (instance == null) {
                    instance = new EmoticonLoader(context);
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
     * @param path path of directory
     * @param scheme scheme
     * @return file input stream
     */
    public InputStream getConfigStream( String path, Scheme scheme ) {
        switch ( scheme ) {
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

    public Drawable getDrawable(String imageUri){
        switch (Scheme.ofUri(imageUri)) {
            case HTTP:
            case HTTPS:
                return null;
            case FILE:
                String filePath = Scheme.FILE.crop(imageUri);
                Bitmap fileBitmap = null;
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
                    assetsBitmap = BitmapFactory.decodeStream(mContext.getAssets().open(assetsPath));
                    return new BitmapDrawable(mContext.getResources(), assetsBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            case DRAWABLE:
                String drawableIdString = Scheme.DRAWABLE.crop(imageUri);
                int resID = mContext.getResources().getIdentifier(drawableIdString, "drawable", mContext.getPackageName());
                return mContext.getResources().getDrawable((int) resID);
            case UNKNOWN:
            default:
                return null;
        }
    }

    /**
     *
     * @param uriStr
     * @param imageView
     * @throws IOException
     */
    @Override
    public void displayImage(String uriStr, ImageView imageView) throws IOException {
        switch (Scheme.ofUri(uriStr)) {
            case HTTP:
            case HTTPS:
                displayImageFromNetwork(uriStr, imageView);
                return ;
            case FILE:
                displayImageFromFile(uriStr, imageView);
                return ;
            case CONTENT:
                displayImageFromContent(uriStr, imageView);
                return ;
            case ASSETS:
                displayImageFromAssets(uriStr, imageView);
                return ;
            case DRAWABLE:
                displayImageFromDrawable(uriStr, imageView);
                return ;
            case UNKNOWN:
            default:
                displayImageFromOtherSource(uriStr, imageView);
                return ;
        }
    }

    /**
     * From Net
     * @param imageUri
     * @param extra
     * @throws IOException
     */
    protected void displayImageFromNetwork(String imageUri, Object extra) throws IOException {
        return ;
    }

    /**
     * From File
     * @param imageUri
     * @param imageView
     * @throws IOException
     */
    protected void displayImageFromFile(String imageUri, ImageView imageView) throws IOException {
        String filePath = Scheme.FILE.crop(imageUri);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);
        return ;
    }

    /**
     * From Content
     * @param imageUri
     * @param imageView
     * @throws IOException
     */
    protected void displayImageFromContent(String imageUri, ImageView imageView) throws FileNotFoundException {
        ContentResolver res = mContext.getContentResolver();
        Uri uri = Uri.parse(imageUri);
        InputStream inputStream = res.openInputStream(uri);
    }

    /**
     * From Assets
     * @param imageUri
     * @param imageView
     * @throws IOException
     */
    protected void displayImageFromAssets(String imageUri, ImageView imageView) throws IOException {
        String filePath = Scheme.ASSETS.crop(imageUri);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(mContext.getAssets().open(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);
    }

    /**
     * From Drawable
     * @param imageUri
     * @param imageView
     * @throws IOException
     */
    protected void displayImageFromDrawable(String imageUri, ImageView imageView) {
        String drawableIdString = Scheme.DRAWABLE.crop(imageUri);
        int resID = mContext.getResources().getIdentifier(drawableIdString, "drawable", mContext.getPackageName());

        if (imageView != null) {
            imageView.setImageResource(resID);
        }
    }

    /**
     * From OtherSource
     * @param imageUri
     * @param imageView
     * @throws IOException
     */
    protected void displayImageFromOtherSource(String imageUri, ImageView imageView) throws IOException {
    }
}
