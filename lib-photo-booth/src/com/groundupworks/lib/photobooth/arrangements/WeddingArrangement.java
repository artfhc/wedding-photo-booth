package com.groundupworks.lib.photobooth.arrangements;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class WeddingArrangement extends BaseArrangement {
    private static final String TAG = WeddingArrangement.class.getCanonicalName();

    private final String backgroundFilePath;
    private final Context context;

    public WeddingArrangement(@NonNull Context context, @NonNull String backgroundFilePath) {
        this.backgroundFilePath = backgroundFilePath;
        this.context = context;

    }

    @Nullable
    private Bitmap getBitmapFromAsset() {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(backgroundFilePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
            Log.e(TAG, "Exception occur when decoding " + backgroundFilePath, e);
        }

        return bitmap;
    }

    @Override
    public Bitmap createPhotoStrip(Bitmap[] srcBitmaps) {
        Bitmap returnBitmap = getBitmapFromAsset();

        // Calculate return bitmap width.
        int boxLength = srcBitmaps.length / 2;
        int srcBitmapWidth = srcBitmaps[0].getWidth();
        int returnBitmapWidth = srcBitmapWidth * boxLength + PHOTO_STRIP_PANEL_PADDING * (boxLength + 1);

        // Get header bitmap if applied.
        int headerHeight = 0;
//        Bitmap header = getHeader(returnBitmapWidth);
//        if (header != null) {
//            headerHeight = header.getHeight();
//        }

        // Calculate return bitmap height.
        int srcBitmapHeight = srcBitmaps[0].getHeight();
        int returnBitmapHeight = srcBitmapHeight * boxLength + PHOTO_STRIP_PANEL_PADDING * (boxLength + 1)
            + headerHeight;

//        returnBitmap = Bitmap.createBitmap(returnBitmapWidth, returnBitmapHeight, ImageHelper.BITMAP_CONFIG);
        if (returnBitmap != null) {
            returnBitmap = returnBitmap.copy(Bitmap.Config.ARGB_8888, true);
            // Create canvas and draw photo strip.
            Canvas canvas = new Canvas(returnBitmap);
            // canvas.drawColor(Color.WHITE);

            // Draw header bitmap.
//            if (header != null) {
//                canvas.drawBitmap(header, 0, 0, null);
//                header.recycle();
//                header = null;
//            }

            // Draw photo bitmaps.
            int i = 0;
            for (Bitmap bitmap : srcBitmaps) {
                int leftPadding = 110; //(returnBitmap.getWidth() - (bitmap.getWidth() * 2 + PHOTO_STRIP_PANEL_PADDING)) / 4;
                Log.e(TAG, "Left padding: " + leftPadding);
                // Even indices start at first column and odd indices start at second column.
                int left = leftPadding + (srcBitmapWidth + PHOTO_STRIP_PANEL_PADDING) * (i % 2) + PHOTO_STRIP_PANEL_PADDING;
                int top = 400 + (srcBitmapHeight + PHOTO_STRIP_PANEL_PADDING) * (i / 2) + PHOTO_STRIP_PANEL_PADDING
                    + headerHeight;
                int right = left + srcBitmapWidth - 1;
                int bottom = top + srcBitmapHeight - 1;

                // Draw panel.
                canvas.drawBitmap(bitmap, left, top, null);
                drawPanelBorders(canvas, left, top, right, bottom);

                i++;
            }

            // Draw photo strip borders.
            // drawPhotoStripBorders(canvas, 0, 0, returnBitmapWidth - 1, returnBitmapHeight - 1);
        }

        return returnBitmap;
    }
}