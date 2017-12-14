package com.example.lenovo.editorimageandtext.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * 作者：xyl on 2017/5/9 18:02
 */

public class EditPostBean implements Serializable {
    private String path;
    private String imageUrl;
    private String content;
    private Bitmap bitmap;
    private long duration;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
