package com.bumptech.glide.load.resource.gifbitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.Resource;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.model.ImageVideoWrapper;
import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser;
import com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream;
import com.bumptech.glide.load.resource.gif.GifData;
import com.bumptech.glide.util.ByteArrayPool;

import java.io.IOException;
import java.io.InputStream;

public class GifBitmapResourceDecoder implements ResourceDecoder<ImageVideoWrapper, GifBitmap> {
    private final ResourceDecoder<ImageVideoWrapper, Bitmap> bitmapDecoder;
    private final ResourceDecoder<InputStream, GifData> gifDecoder;
    private String id;

    public GifBitmapResourceDecoder(ResourceDecoder<ImageVideoWrapper, Bitmap> bitmapDecoder,
            ResourceDecoder<InputStream, GifData> gifDecoder) {
        this.bitmapDecoder = bitmapDecoder;
        this.gifDecoder = gifDecoder;
    }

    @Override
    public Resource<GifBitmap> decode(ImageVideoWrapper source, int width, int height) throws IOException {
        ByteArrayPool pool = ByteArrayPool.get();
        InputStream is = source.getStream();
        byte[] tempBytes = pool.getBytes();
        RecyclableBufferedInputStream bis = new RecyclableBufferedInputStream(is, tempBytes);
        GifBitmap result = null;
        if (is != null) {
            source = new ImageVideoWrapper(bis, source.getFileDescriptor());
            bis.mark(2048);
            ImageHeaderParser.ImageType type = new ImageHeaderParser(bis).getType();
            bis.reset();

            if (type == ImageHeaderParser.ImageType.GIF) {
                Resource<GifData> gifResource = gifDecoder.decode(bis, width, height);
                result = new GifBitmap(null, gifResource);
            }
        }

        if (result == null) {
            Resource<Bitmap> bitmapResource = bitmapDecoder.decode(source, width, height);
            result = new GifBitmap(bitmapResource, null);
        }
        pool.releaseBytes(tempBytes);
        return new GifBitmapResource(result);
    }

    @Override
    public String getId() {
        if (id == null) {
            id = gifDecoder.getId() + bitmapDecoder.getId();
        }
        return id;
    }
}