package com.example.lenovo.editorimageandtext.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;

import com.example.lenovo.editorimageandtext.bean.ImageBucket;
import com.example.lenovo.editorimageandtext.bean.ImageItem;
import com.example.lenovo.editorimageandtext.bean.VideoItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 获得图片的路径的工具类
 *
 * @author xyl
 */
public class ImagePathUtil {


    /**
     * 得到缩略图
     */
    private static HashMap<String, String> getThumbnail(ContentResolver cr) {
        String[] projection = {Thumbnails._ID, Thumbnails.IMAGE_ID,
                Thumbnails.DATA};
        Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection,
                null, null, null);
        HashMap<String, String> thumbnailList = new HashMap<String, String>();
        if (cursor.moveToFirst()) {
            int image_id;
            String image_path;
            int image_idColumn = cursor.getColumnIndex(Thumbnails.IMAGE_ID);
            int dataColumn = cursor.getColumnIndex(Thumbnails.DATA);

            do {
                image_id = cursor.getInt(image_idColumn);
                image_path = cursor.getString(dataColumn);
                thumbnailList.put("" + image_id, image_path);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return thumbnailList;
    }

    /**
     * 得到原始图像路径
     *
     * @param image_id
     * @return 原始图片路径
     */
    public static String getOriginalImagePath(Context context, String image_id) {
        String path = null;
        String[] projection = {Media._ID, Media.DATA};
        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection,
                Media._ID + "=" + image_id, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(Media.DATA));
        }

        cursor.close();
        return path;
    }

    /**
     * 得到图片专辑列表
     *
     * @param context
     * @return
     */
    public static ArrayList<ImageBucket> getImageAlbumList(Context context) {
        ArrayList<ImageBucket> lists = new ArrayList<ImageBucket>();
        HashMap<String, ImageBucket> maps = buildImagesBucketList(context);
        for (Map.Entry<String, ImageBucket> entry : maps.entrySet()) {
            lists.add(entry.getValue());
        }
        return lists;
    }

    public static HashMap<String, ImageBucket> buildImagesBucketList(Context context) {
        ContentResolver cr = context.getContentResolver();
        HashMap<String, ImageBucket> bucketList = new HashMap<String, ImageBucket>();

        // 构造缩略图索引
        HashMap<String, String> thumbnailList = getThumbnail(cr);

        // 构造相册索引
        String columns[] = new String[]{Media._ID, Media.BUCKET_ID,
                Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE,
                Media.SIZE, Media.BUCKET_DISPLAY_NAME};
        // 得到一个游标
        Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null,
                null);
        if (cur.moveToFirst()) {
            // 获取指定列的索引
            int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
            int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
            int bucketDisplayNameIndex = cur
                    .getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
            int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);

            do {
                String _id = cur.getString(photoIDIndex);
                String path = cur.getString(photoPathIndex);
                String bucketName = cur.getString(bucketDisplayNameIndex);
                String bucketId = cur.getString(bucketIdIndex);

                ImageBucket bucket = bucketList.get(bucketId);
                if (bucket == null) {
                    bucket = new ImageBucket();
                    bucketList.put(bucketId, bucket);
                    bucket.imageList = new ArrayList<ImageItem>();
                    bucket.bucketName = bucketName;
                }
                bucket.count++;
                ImageItem imageItem = new ImageItem();
                imageItem.setImageId(_id);
                imageItem.setImagePath(path);
                imageItem.setThumbnailPath(thumbnailList.get(_id));
                bucket.imageList.add(imageItem);

            } while (cur.moveToNext());
        }
        cur.close();
        return bucketList;
    }

    /**
     * 得到视频专辑列表
     *
     * @param context
     * @return
     */
    public static ArrayList<ImageBucket> getVideoAlbumList(Context context) {
        ArrayList<ImageBucket> lists = new ArrayList<ImageBucket>();
        HashMap<String, ImageBucket> maps = buildVideosBucketList(context);
        for (Map.Entry<String, ImageBucket> entry : maps.entrySet()) {
            lists.add(entry.getValue());
        }
        return lists;
    }

    public static HashMap<String, ImageBucket> buildVideosBucketList(Context context) {
        ContentResolver cr = context.getContentResolver();
        // 构造缩略图索引
        HashMap<String, String> thumbnailList = getThumbnail(cr);

        HashMap<String, ImageBucket> bucketList = new HashMap<String, ImageBucket>();
        if (context != null) {
            Cursor cursor = cr.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int _id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    String buckeID = cursor
                            .getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID));
                    String bucketName = cursor
                            .getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                    String title = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    String album = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
                    String artist = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
                    String displayName = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                    String mimeType = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                    String path = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    long duration = cursor
                            .getLong(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    long size = cursor
                            .getInt(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                    ImageBucket bucket = bucketList.get(buckeID);
                    if (bucket == null) {
                        bucket = new ImageBucket();
                        bucketList.put(buckeID, bucket);
                        bucket.videoList = new ArrayList<VideoItem>();
                        bucket.bucketName = bucketName;
                    }
                    bucket.count++;
                    VideoItem videoItem = new VideoItem();
                    videoItem.setName(title);
                    videoItem.setSize(size);
                    videoItem.setImagePath(path);
                    videoItem.setThumbnailPath(thumbnailList.get(_id));
                    videoItem.setDuration(duration);
                    bucket.videoList.add(videoItem);
                }
                cursor.close();
            }
        }
        return bucketList;
    }

}
