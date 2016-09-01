package com.groundupworks.flyingphotobooth.client;

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
        @NonNull public final File file;

        public FileUploadPayload(@NonNull String sessionId, @NonNull File file) {
            this.sessionId = sessionId;
            this.file = file;
        }
    }

    @GET("/ping")
    Call<Ping> ping();

    @Multipart
    @POST("file-upload")
    Call<ResponseBody> uploadFile(@Part("sessionId") RequestBody sessionId,
                                  @Part MultipartBody.Part file);
}
