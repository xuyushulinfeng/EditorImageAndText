package com.example.lenovo.editorimageandtext.bean;

import android.graphics.Bitmap;

/**
 * 作者：xyl on 2017/2/22 14:35
 */

public class MyBean {
    private String drawe;
    private int type;
    private String place;
    private String annotation;
    private boolean isOpen;
    private String title;
    private Bitmap bitmap;
    private long duration;
    private String imagePath;
    private String original_size;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDrawe() {
        return drawe;
    }

    public void setDrawe(String drawe) {
        this.drawe = drawe;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getOriginal_size() {
        return original_size;
    }

    public void setOriginal_size(String original_size) {
        this.original_size = original_size;
    }
}
