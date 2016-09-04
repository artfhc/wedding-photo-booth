/*
 * This file is part of Flying PhotoBooth.
 * 
 * Flying PhotoBooth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Flying PhotoBooth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Flying PhotoBooth.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.arthurtimberly.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Message;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.arthurtimberly.MyApplication;
import com.arthurtimberly.R;
import com.arthurtimberly.client.ServiceClient;
import com.arthurtimberly.fragments.ShareFragment;
import com.groundupworks.lib.photobooth.arrangements.BoxArrangement;
import com.groundupworks.lib.photobooth.arrangements.HorizontalArrangement;
import com.groundupworks.lib.photobooth.arrangements.VerticalArrangement;
import com.groundupworks.lib.photobooth.filters.BlackAndWhiteFilter;
import com.groundupworks.lib.photobooth.filters.LineArtFilter;
import com.groundupworks.lib.photobooth.filters.SepiaFilter;
import com.groundupworks.lib.photobooth.framework.BaseController;
import com.groundupworks.lib.photobooth.helpers.ImageHelper;
import com.groundupworks.lib.photobooth.helpers.ImageHelper.Arrangement;
import com.groundupworks.lib.photobooth.helpers.ImageHelper.ImageFilter;
import com.groundupworks.wings.Wings;
import com.groundupworks.wings.dropbox.DropboxEndpoint;
import com.groundupworks.wings.facebook.FacebookEndpoint;
import com.groundupworks.wings.gcp.GoogleCloudPrintEndpoint;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for the {@link ShareFragment}.
 */
public class ShareController extends BaseController {

    private static final String TAG = ShareController.class.getCanonicalName();

    //
    // Controller events. The ui should be notified of these events.
    //

    public static final int ERROR_OCCURRED = -1;

    public static final int THUMB_READY = 0;

    public static final int JPEG_SAVED = 1;

    public static final int GCP_SHARE_MARKED = 2;

    public static final int FACEBOOK_SHARE_MARKED = 3;

    public static final int DROPBOX_SHARE_MARKED = 4;

    public static final int SHOW_TOAST_MESSAGE = 5;

    private String mJpegPath = null;

    private Bitmap mThumb = null;

    private boolean mIsGcpShareActive = true;

    private boolean mIsFacebookShareActive = true;

    private boolean mIsDropboxShareActive = true;

    //
    // BaseController implementation.
    //

    @Override
    protected void handleEvent(Message msg) {
        final MyApplication context = (MyApplication) MyApplication.getContext();
        switch (msg.what) {
            case ShareFragment.UPLOAD_TO_SERVER:
                uploadMessageToServer(context, (ServiceClient.FileUploadPayload) msg.obj);
                break;
            case ShareFragment.IMAGE_VIEW_READY:

                /*
                 * Create an image bitmap from Jpeg data.
                 */
                Bundle bundle = msg.getData();
                String imageDirectory = ImageHelper.getCapturedImageDirectory(context.getString(R.string.image_helper__image_folder_name));

                int jpegDataLength = 0;
                for (int i = 0; i < ShareFragment.MESSAGE_BUNDLE_KEY_JPEG_DATA.length; i++) {
                    if (bundle.containsKey(ShareFragment.MESSAGE_BUNDLE_KEY_JPEG_DATA[i])) {
                        jpegDataLength++;
                    } else {
                        break;
                    }
                }

                byte[][] jpegData = new byte[jpegDataLength][];
                for (int i = 0; i < jpegDataLength; i++) {
                    jpegData[i] = bundle.getByteArray(ShareFragment.MESSAGE_BUNDLE_KEY_JPEG_DATA[i]);
                }

                float rotation = bundle.getFloat(ShareFragment.MESSAGE_BUNDLE_KEY_ROTATION);
                boolean reflection = bundle.getBoolean(ShareFragment.MESSAGE_BUNDLE_KEY_REFLECTION);
                String filterPref = bundle.getString(ShareFragment.MESSAGE_BUNDLE_KEY_FILTER);
                String arrangementPref = bundle.getString(ShareFragment.MESSAGE_BUNDLE_KEY_ARRANGEMENT);
                int thumbMaxWidth = bundle.getInt(ShareFragment.MESSAGE_BUNDLE_KEY_MAX_THUMB_WIDTH);
                int thumbMaxHeight = bundle.getInt(ShareFragment.MESSAGE_BUNDLE_KEY_MAX_THUMB_HEIGHT);

                // Select filter.
                ImageFilter[] filters = new ImageFilter[ShareFragment.MESSAGE_BUNDLE_KEY_JPEG_DATA.length];
                if (filterPref.equals(context.getString(R.string.pref__filter_bw))) {
                    filters[0] = new BlackAndWhiteFilter();
                    filters[1] = new BlackAndWhiteFilter();
                    filters[2] = new BlackAndWhiteFilter();
                    filters[3] = new BlackAndWhiteFilter();
                } else if (filterPref.equals(context.getString(R.string.pref__filter_bw_mixed))) {
                    if (arrangementPref.equals(context.getString(R.string.pref__arrangement_box))) {
                        filters[0] = new BlackAndWhiteFilter();
                        filters[3] = new BlackAndWhiteFilter();
                    } else {
                        filters[0] = new BlackAndWhiteFilter();
                        filters[2] = new BlackAndWhiteFilter();
                    }
                } else if (filterPref.equals(context.getString(R.string.pref__filter_sepia))) {
                    filters[0] = new SepiaFilter();
                    filters[1] = new SepiaFilter();
                    filters[2] = new SepiaFilter();
                    filters[3] = new SepiaFilter();
                } else if (filterPref.equals(context.getString(R.string.pref__filter_sepia_mixed))) {
                    if (arrangementPref.equals(context.getString(R.string.pref__arrangement_box))) {
                        filters[0] = new SepiaFilter();
                        filters[3] = new SepiaFilter();
                    } else {
                        filters[0] = new SepiaFilter();
                        filters[2] = new SepiaFilter();
                    }
                } else if (filterPref.equals(context.getString(R.string.pref__filter_line_art))) {
                    filters[0] = new LineArtFilter();
                    filters[1] = new LineArtFilter();
                    filters[2] = new LineArtFilter();
                    filters[3] = new LineArtFilter();
                } else {
                    // No filter. Keep filter as null.
                }

                // Select arrangement.
                Arrangement arrangement = null;
                if (arrangementPref.equals(context.getString(R.string.pref__arrangement_horizontal))) {
                    arrangement = new HorizontalArrangement();
                } else if (arrangementPref.equals(context.getString(R.string.pref__arrangement_box))) {
                    arrangement = new BoxArrangement();
                } else {
                    arrangement = new VerticalArrangement();
                }

                // Do the image processing.
                Bitmap[] bitmaps = new Bitmap[jpegDataLength];
                boolean isFramesValid = true;
                for (int i = 0; i < jpegDataLength; i++) {
                    // Create frame.
                    Bitmap frame = ImageHelper.createImage(jpegData[i], rotation, reflection, filters[i]);

                    // Ensure frame is non-null.
                    if (frame != null) {
                        bitmaps[i] = frame;
                    } else {
                        isFramesValid = false;
                        break;
                    }
                }

                // Store all the bitmaps in file system
                List<String> paths = new ArrayList<>();
                if (isFramesValid) {
                    paths.addAll(writeActualBitmapToDisc(imageDirectory, bitmaps, context));
                }

                // Create photo strip if all frames are valid.
                Bitmap photoStrip = null;
                if (isFramesValid) {
                    photoStrip = ImageHelper.createPhotoStrip(bitmaps, arrangement);
                }

                // Recycle original bitmaps.
                for (Bitmap bitmap : bitmaps) {
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                }
                bitmaps = null;

                // Notify ui.
                if (photoStrip != null) {
                    // Create thumbnail.
                    Point fittedSize = ImageHelper.getAspectFitSize(thumbMaxWidth, thumbMaxHeight,
                            photoStrip.getWidth(), photoStrip.getHeight());
                    mThumb = Bitmap.createScaledBitmap(photoStrip, fittedSize.x, fittedSize.y, true);
                    if (mThumb != null) {
                        // Thumbnail bitmap is ready.
                        Message uiMsg = Message.obtain();
                        uiMsg.what = THUMB_READY;
                        uiMsg.obj = mThumb;
                        sendUiUpdate(uiMsg);
                    } else {
                        // An error has occurred.
                        reportError();
                    }
                } else {
                    // An error has occurred.
                    reportError();
                }

                /*
                 * Save image bitmap as Jpeg.
                 */
                try {
                    if (imageDirectory != null) {
                        String imageName = ImageHelper.generateCapturedImageName(context
                                .getString(R.string.image_helper__image_filename_prefix));
                        File file = new File(imageDirectory, imageName);
                        boolean isSuccessful = writeBitmapToDisc(file, photoStrip);

                        if (isSuccessful) {
                            mJpegPath = file.getPath();
                            paths.add(0, mJpegPath);

                            // Notify ui the Jpeg is saved.
                            Message uiMsg = Message.obtain();
                            uiMsg.what = JPEG_SAVED;
                            uiMsg.obj = paths.toArray(new String[paths.size()]);
                            sendUiUpdate(uiMsg);
                        } else {
                            reportError();
                        }
                    } else {
                        // Invalid external storage state or failed directory creation.
                        reportError();
                    }
                } catch (IOException e) {
                    reportError();
                }

                /*
                 * Recycle photo strip bitmap if it is not the same object referenced by mThumb.
                 */
                if (photoStrip != null && photoStrip != mThumb) {
                    photoStrip.recycle();
                }
                photoStrip = null;

                break;
            case ShareFragment.GCP_SHARE_REQUESTED:
                // Create record in Wings.
                if (mIsGcpShareActive) {
                    if (mJpegPath != null && Wings.share(mJpegPath, GoogleCloudPrintEndpoint.class)) {
                        // Disable to ensure we only make one share request.
                        mIsGcpShareActive = false;

                        // Notify ui.
                        Message uiMsg = Message.obtain();
                        uiMsg.what = GCP_SHARE_MARKED;
                        sendUiUpdate(uiMsg);
                    } else {
                        reportError();
                    }
                }
                break;
            case ShareFragment.FACEBOOK_SHARE_REQUESTED:
                // Create record in Wings.
                if (mIsFacebookShareActive) {
                    if (mJpegPath != null && Wings.share(mJpegPath, FacebookEndpoint.class)) {
                        // Disable to ensure we only make one share request.
                        mIsFacebookShareActive = false;

                        // Notify ui.
                        Message uiMsg = Message.obtain();
                        uiMsg.what = FACEBOOK_SHARE_MARKED;
                        sendUiUpdate(uiMsg);
                    } else {
                        reportError();
                    }
                }
                break;
            case ShareFragment.DROPBOX_SHARE_REQUESTED:
                // Create record in Wings.
                if (mIsDropboxShareActive) {
                    if (mJpegPath != null && Wings.share(mJpegPath, DropboxEndpoint.class)) {
                        // Disable to ensure we only make one share request.
                        mIsDropboxShareActive = false;

                        // Notify ui.
                        Message uiMsg = Message.obtain();
                        uiMsg.what = DROPBOX_SHARE_MARKED;
                        sendUiUpdate(uiMsg);
                    } else {
                        reportError();
                    }
                }
                break;
            case ShareFragment.FRAGMENT_DESTROYED:
                /*
                 * Recycle thumb bitmap.
                 */
                if (mThumb != null) {
                    mThumb.recycle();
                    mThumb = null;
                }
                break;
            default:
                break;
        }
    }

    @NonNull
    private List<String> writeActualBitmapToDisc(@NonNull String imageDirectory, @NonNull Bitmap[] bitmaps, @NonNull Context context) {
        List<String> paths = new ArrayList<>();
        for (int i = 0; i< bitmaps.length; i++) {
            try {
                String imageName = ImageHelper.generateCapturedImageName(context.getString(R.string.image_helper__image_filename_square_prefix));
                File file = new File(imageDirectory, imageName);
                boolean isSuccessful = writeBitmapToDisc(file, bitmaps[i]);
                if (isSuccessful) {
                    paths.add(file.getPath());
                } else {
                    Log.e(TAG, "Problem output file: " + i + " | " + imageName);
                }
            } catch (IOException ex) {
                Log.e(TAG, "Exception: Problem output file: " + i, ex);
            }
        }
        return paths;
    }

    private boolean writeBitmapToDisc(@NonNull File file, @NonNull Bitmap photoStrip) throws IOException {
        final OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));

        // Convert to Jpeg and writes to file.
        boolean isSuccessful = ImageHelper.writeJpeg(photoStrip, outputStream);
        outputStream.flush();
        outputStream.close();

        return isSuccessful;
    }

    @WorkerThread
    private void uploadMessageToServer(@NonNull MyApplication context, @NonNull final ServiceClient.FileUploadPayload payload) {
        Log.i(TAG, "Ready to upload file: " + payload.files[0].getAbsolutePath());
        if (context.getServiceClient() == null) {
            showToastMessage("Service client not found?!");
        }

        // MultipartBody.Part is used to send also the actual file name
        RequestBody sessionId = RequestBody.create(MediaType.parse("multipart/form-data"), payload.sessionId);
        MultipartBody.Part body = getMultipartBody(payload.files[0], "image");
        MultipartBody.Part square1 = getMultipartBody(payload.files[1], "square1");
        MultipartBody.Part square2 = getMultipartBody(payload.files[2], "square2");
        MultipartBody.Part square3 = getMultipartBody(payload.files[3], "square3");
        MultipartBody.Part square4 = getMultipartBody(payload.files[4], "square4");
        Call<ResponseBody> call = context.getServiceClient().uploadFile(sessionId, body, square1, square2, square3, square4);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                showToastMessage("success file path: " + payload.files[0].getAbsolutePath());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                showToastMessage("Upload error: " + t.getMessage());
            }
        });
    }

    @NonNull
    private MultipartBody.Part getMultipartBody(@NonNull File file, @NonNull String fieldName) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(fieldName, file.getName(), requestFile);
    }

    private void showToastMessage(@NonNull String messageString) {
        Message message = Message.obtain();
        message.what = SHOW_TOAST_MESSAGE;
        message.obj = messageString;
        sendUiUpdate(message);
    }

    //
    // Private methods.
    //

    /**
     * Reports an error event to ui.
     */
    private void reportError() {
        Message uiMsg = Message.obtain();
        uiMsg.what = ERROR_OCCURRED;
        sendUiUpdate(uiMsg);
    }
}
