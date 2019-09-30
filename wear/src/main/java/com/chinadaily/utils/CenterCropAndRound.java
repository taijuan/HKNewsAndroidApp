package com.chinadaily.utils;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

import static com.chinadaily.utils.UtilsKt.dp2px;

/**
 * Scale the image so that either the width of the image matches the given width and the height of
 * the image is greater than the given height or vice versa, and then crop the larger dimension to
 * match the given dimension.
 * <p>
 * Does not maintain the image's aspect ratio
 */
public class CenterCropAndRound extends BitmapTransformation {
    private static final String ID = "com.chinadaily.utils.CenterCropAndRound";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    @Override
    protected Bitmap transform(
            @NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return TransformationUtils.roundedCorners(pool, TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight), dp2px(4f));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CenterCropAndRound;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}
