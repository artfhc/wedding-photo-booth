package com.groundupworks.flyingphotobooth.client;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ServiceClient {

    class Ping {
        public String title;
        public String content;
        public int count;
    }

    @GET("/ping")
    Call<Ping> ping();
}
