package com.arthurtimberly.client;

import android.net.Uri;
import android.support.annotation.NonNull;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import java.io.File;

public interface ServiceClient {

    class Ping {
        public String title;
        public String content;
        public int count;
    }

    class FileUploadPayload {
        @NonNull public final String sessionId;
        @NonNull public final File[] files;

        public FileUploadPayload(@NonNull String sessionId,
                                 @NonNull Uri... uriList) {
            this.sessionId = sessionId;
            this.files = new File[uriList.length];
            for (int i = 0; i < uriList.length; i++) {
                this.files[i] = new File(uriList[i].getPath());
            }
        }
    }

    @GET("/ping")
    Call<Ping> ping();

    @Multipart
    @POST("file-upload")
    Call<ResponseBody> uploadFile(@Part("sessionId") RequestBody sessionId,
                                  @Part MultipartBody.Part file,
                                  @Part MultipartBody.Part square1,
                                  @Part MultipartBody.Part square2,
                                  @Part MultipartBody.Part square3,
                                  @Part MultipartBody.Part square4);
}
