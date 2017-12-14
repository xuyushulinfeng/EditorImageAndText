package com.example.lenovo.editorimageandtext.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author xyl
 */
public class ImageBucket implements Serializable {
    public int count = 0;
    public String bucketName;
    public List<ImageItem> imageList;
    public List<VideoItem> videoList;

}
