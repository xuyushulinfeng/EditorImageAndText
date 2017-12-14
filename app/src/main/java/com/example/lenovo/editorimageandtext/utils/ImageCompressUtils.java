package com.example.lenovo.editorimageandtext.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片压缩工具
 *
 * @author xyl
 */
@SuppressLint("NewApi")
public class ImageCompressUtils {

    /**
     * 缓存Image的类，当存储Image的大小大于LruCache设定的值，系统自动释放内存
     */
    private LruCache<String, Bitmap> mMemoryCache;

    /**
     * 压缩的图片保存路径
     */
    private String filename = "";

    // private static ImageCompressUtils insImageCompressUtils = null;
    // private ImageCompressUtils(){
    //
    // }
    //
    // public static ImageCompressUtils getInstance(){
    // if(insImageCompressUtils == null){
    // insImageCompressUtils = new ImageCompressUtils();
    // }
    // return insImageCompressUtils;
    // }

    public ImageCompressUtils() {
        // 获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;
        // 给LruCache分配1/8 4M
        mMemoryCache = new LruCache<String, Bitmap>(mCacheSize) {

            // 必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

        };
    }

    /**
     * 设置压缩图片的保存路径
     *
     * @param filename 压缩的图片保存路径
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * 压缩图片
     *
     * @param filePath  待压缩的图片的路径
     * @param maxQuilty 设定的最大的图片质量，单位是KB，例如如果希望压缩的图片最终的质量是100KB左右，传入100
     * @return 压缩后的图片的路径
     */
    public String compressImage(String filePath, double maxQuilty, boolean isscaled) {
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        /** 图片实际的高 */
        int actualHeight = options.outHeight;
        /** 图片实际的宽 */
        int actualWidth = options.outWidth;
        float maxHeight = (float) getMaxHeight(maxQuilty, actualWidth, actualHeight,
                FileSizeUtil.getFileOrFilesSize(filePath, 2));
        float maxWidth = (float) getMaxWidth(maxQuilty, actualWidth,
                FileSizeUtil.getFileOrFilesSize(filePath, 2));
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        if (isscaled) {
            scaledBitmap = scaledBitmap(actualWidth, actualHeight, bmp, filePath, options);
        }
        FileOutputStream out = null;
        if (TextUtils.isEmpty(filename)) {
            filename = getFilename();
        }
        try {
            out = new FileOutputStream(filename);
            if (isscaled) {
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            } else {
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bmp != null) {
            bmp.recycle();
        }
        if (isscaled)
            scaledBitmap.recycle();
        return filename;
    }

    /**
     * 将bitmap转化为数组
     *
     * @param bmp
     * @return
     */
    public static byte[] bmpToByteArray(Bitmap bmp) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        recycleBitmap(bmp);
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param bitmap
     */
    private static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    /**
     * 旋转图片
     *
     * @param actualWidth
     * @param actualHeight
     * @param bmp
     * @param filePath
     * @param options
     * @return
     */
    private Bitmap scaledBitmap(int actualWidth, int actualHeight, Bitmap bmp, String filePath,
                                BitmapFactory.Options options) {
        Bitmap scaledBitmap = null;
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2,
                new Paint(Paint.FILTER_BITMAP_FLAG));
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.e("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.e("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.e("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.e("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
                    scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scaledBitmap;
    }

    /**
     * 获取最大的宽
     *
     * @param maxQuilty    设定的最大的图片质量，单位是KB，例如如果希望压缩的图片最终的质量是100KB左右，传入100
     * @param oriWidth     原来的图的宽度
     * @param oriImageSize 原来的图的大小（单位是KB），如果是1MB，那么1024
     * @return
     */
    private double getMaxWidth(double maxQuilty, double oriWidth, double oriImageSize) {
        return Math.sqrt(maxQuilty * Math.pow(oriWidth, 2) / (oriImageSize * 0.9));
    }

    /**
     * 获取最大的高
     *
     * @param maxQuilty    设定的最大的图片质量，单位是KB，例如如果希望压缩的图片最终的质量是100KB左右，传入100
     * @param oriWidth     原来的图的宽度
     * @param oriHeight    原来的图的高度
     * @param oriImageSize 原来的图的大小（单位是KB），如果是1MB，那么1024
     * @return
     */
    private double getMaxHeight(double maxQuilty, double oriWidth, double oriHeight,
                                double oriImageSize) {
        double maxWidth = Math.sqrt(maxQuilty * Math.pow(oriWidth, 2) / oriImageSize);
        return maxWidth * oriHeight / oriWidth;
    }

    /**
     * 计算压缩的大小比例
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 2;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    /**
     * 创建压缩后的图片的路径
     *
     * @return
     */
    private String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    /**
     * 压缩图片
     *
     * @param filePath  待压缩的图片的路径
     * @param maxQuilty 设定的最大的图片质量，单位是KB，例如如果希望压缩的图片最终的质量是100KB左右，传入100
     * @return 压缩后的图片的路径
     */
    public Bitmap compressBitmapImage(String filePath, double maxQuilty) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        /** 图片实际的高 */
        int actualHeight = options.outHeight;
        /** 图片实际的宽 */
        int actualWidth = options.outWidth;
        float maxHeight = (float) getMaxHeight(maxQuilty, actualWidth, actualHeight,
                FileSizeUtil.getFileOrFilesSize(filePath, 2));
        float maxWidth = (float) getMaxWidth(maxQuilty, actualWidth,
                FileSizeUtil.getFileOrFilesSize(filePath, 2));
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        bmp = scaledBitmap(actualWidth, actualHeight, bmp, filePath, options);
        FileOutputStream out = null;
        if (TextUtils.isEmpty(filename)) {
            filename = getFilename();
        }
        try {
            out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    /**
     * 获取Bitmap, 内存中没有就去手机或者sd卡中获取，这一步在getView中会调用，比较关键的一步
     *
     * @param url
     * @return
     */
    public Bitmap showCacheBitmap(String url) {
        if (getBitmapFromMemCache(url) != null) {
            return getBitmapFromMemCache(url);
        } else {
            Bitmap bitmap = compressBitmapImage(url, 100);
            // 将Bitmap 加入内存缓存
            addBitmapToMemoryCache(url, bitmap);
            return bitmap;
        }
    }

    /**
     * 从内存缓存中获取一个Bitmap
     *
     * @param key
     * @return
     */
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 添加Bitmap到内存缓存
     *
     * @param key
     * @param bitmap
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

}
